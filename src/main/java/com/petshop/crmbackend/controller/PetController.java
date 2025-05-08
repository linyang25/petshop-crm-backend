package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.dto.AddPetRequest;
import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.entity.Pet;
import com.petshop.crmbackend.repository.PetRepository;
import com.petshop.crmbackend.repository.AppointmentRepository;
import com.petshop.crmbackend.common.ApiResponse;
import com.petshop.crmbackend.service.S3StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;





import javax.validation.Valid;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
//import io.swagger.v3.oas.annotations.Parameter;



@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;
    private final S3StorageService storageService;

    public PetController(PetRepository petRepository,AppointmentRepository appointmentRepository,S3StorageService s3StorageService) {
        this.petRepository = petRepository;
        this.appointmentRepository = appointmentRepository;
        this.storageService = s3StorageService;
    }


    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "添加宠物信息及照片")
    public ApiResponse<?> addPet(
            @Parameter(
                    in = ParameterIn.DEFAULT,
                    name = "info",
                    description = "宠物信息 JSON",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddPetRequest.class)
                    )
            )
            @RequestPart("info") @Valid AddPetRequest info,

            // —— 第二个 part，指定 mediaType = application/octet-stream
            @Parameter(
                    in = ParameterIn.DEFAULT,
                    name = "file",
                    description = "上传的照片",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("file") MultipartFile file
    )  throws IOException {
        // 生成唯一 petId（五位数）
        Long petId = generateUniquePetId();
        if (petId == null) {
            return ApiResponse.error(400, "系统繁忙，请稍后再试");
        }

        // 解析 birthday 支持多种格式
        LocalDate birthday = parseBirthday(info.getBirthday());
        if (birthday == null && info.getBirthday() != null) {
            return ApiResponse.error(400, "生日格式不正确，应为 yyyy-MM-dd 或 yyyy/MM/dd");
        }

        // 若 petId 或信息重复，禁止添加
        Optional<Pet> existingPet = petRepository.findByCustomerNameAndSpeciesAndBreedNameAndPetNameAndGenderAndBirthday(
                info.getCustomerName(),
                info.getSpecies(),
                info.getBreedName(),
                info.getPetName(),
                info.getGender(),
                birthday
        );

        if (existingPet.isPresent()) {
            return ApiResponse.error(400, "不可重复添加；已存在petId 为: " + existingPet.get().getPetId());
        }


        Pet pet = new Pet();
        pet.setPetId(petId);
        pet.setCustomerName(info.getCustomerName());
        pet.setSpecies(info.getSpecies());
        pet.setBreedName(info.getBreedName());
        pet.setPetName(info.getPetName());
        pet.setGender(info.getGender());
        pet.setBirthday(birthday);
        //pet.setProfilePhoto(request.getProfilePhoto());
        pet.setDescription(info.getDescription());
        pet.setCreatedAt(LocalDateTime.now());


        String original = file.getOriginalFilename();
        String key = String.format("pet-images/%d-%d-%s",
                petId, System.currentTimeMillis(), original);
        String url = storageService.upload(file, key);


//        String url = storageService.upload(file, pet.getPetId().toString());
        pet.setProfilePhoto(url);

        petRepository.save(pet);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("petId", pet.getPetId());
        responseData.put("URL", url);
        return ApiResponse.success("宠物信息添加成功", responseData);
    }

    // 生成唯一 petId（5位数字）
    private Long generateUniquePetId() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            Long candidate = (long) (random.nextInt(100000));
            if (!petRepository.existsByPetId(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    // 支持多种生日格式
    private LocalDate parseBirthday(String input) {
        if (input == null || input.isEmpty()) return null;
        String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd"};
        for (String pattern : patterns) {
            try {
                return LocalDate.parse(input, DateTimeFormatter.ofPattern(pattern));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @DeleteMapping("/delete/{petId}")
    @Operation(summary = "逻辑删除宠物信息")
    public ApiResponse<?> deletePet(@PathVariable Long petId) {
        Optional<Pet> optionalPet = petRepository.findByPetId(petId);
        if (!optionalPet.isPresent()) {
            return ApiResponse.error(404, "未找到对应的宠物，无法删除");
        }

        Pet pet = optionalPet.get();
        if (pet.getIsDeleted()) {
            return ApiResponse.error(400, "宠物信息已删除,无法再次删除");
        }

        pet.setIsDeleted(true);
        petRepository.save(pet);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("petId", pet.getPetId());
        return ApiResponse.success("宠物信息删除成功", responseData);
    }


    @PutMapping(value = "/update/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "编辑宠物信息及更换照片（不允许修改客户名）")
    public ApiResponse<?> updatePet(
            @PathVariable Long petId,
            @RequestPart("info") @Valid AddPetRequest request,  // JSON 部分
            @RequestPart(value = "file", required = false) MultipartFile file  // 文件部分，可选上传
    ) throws IOException {
        Optional<Pet> optionalPet = petRepository.findByPetIdAndIsDeletedFalse(petId);
        if (!optionalPet.isPresent()) {
            return ApiResponse.error(404, "未找到对应的宠物信息");
        }

        Pet pet = optionalPet.get();
        // customerName 不可修改
        pet.setSpecies(request.getSpecies());
        pet.setBreedName(request.getBreedName());
        pet.setPetName(request.getPetName());
        pet.setGender(request.getGender());

        if (request.getBirthday() != null && !request.getBirthday().isEmpty()) {
            try {
                LocalDate birthday = LocalDate.parse(request.getBirthday(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                pet.setBirthday(birthday);
            } catch (DateTimeParseException e) {
                return ApiResponse.error(400, "生日格式错误，应为 yyyy-MM-dd");
            }
        }

        // 如果上传了新文件，就替换图片 URL
        if (file != null && !file.isEmpty()) {
            String url = storageService.upload(file, pet.getPetId().toString());
            pet.setProfilePhoto(url);
        }

        pet.setDescription(request.getDescription());
        petRepository.save(pet);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("petId", pet.getPetId());
        return ApiResponse.success("宠物信息更新成功", responseData);
    }

    @GetMapping("/detail/{petId}")
    @Operation(summary = "获取宠物详细信息及预约状态")
    public ApiResponse<?> getPetDetails(@PathVariable Long petId) {
        Optional<Pet> optionalPet = petRepository.findByPetIdAndIsDeletedFalse(petId);
        if (!optionalPet.isPresent()) {
            return ApiResponse.error(404, "未找到该宠物或已被删除");
        }

        Pet pet = optionalPet.get();
        boolean hasAppointment = appointmentRepository.existsByPetId(pet.getPetId());
        Map<String, Object> result = new HashMap<>();
        result.put("customerName", pet.getCustomerName());
        result.put("species", pet.getSpecies());
        result.put("breedName", pet.getBreedName());
        result.put("gender", pet.getGender());
        result.put("birthday", pet.getBirthday());
        result.put("description", pet.getDescription());
        result.put("profilePhoto", pet.getProfilePhoto());
        result.put("pet_Id", pet.getPetId());
        result.put("hasAppointment", hasAppointment);
        if (hasAppointment) {
            Appointment appointment = appointmentRepository
                    .findTopByPetIdOrderByAppointmentDateDescAppointmentTimeDesc(pet.getPetId());
            if (appointment != null) {
                Map<String, Object> appointmentInfo = new HashMap<>();
                appointmentInfo.put("appointmentDate", appointment.getAppointmentDate());
                appointmentInfo.put("appointmentTime", appointment.getAppointmentTime());
                appointmentInfo.put("serviceType", appointment.getServiceType());
                appointmentInfo.put("notes", appointment.getNotes());
                appointmentInfo.put("phone", appointment.getPhone());
                result.put("appointment", appointmentInfo);
            }
        }

        return ApiResponse.success("查询成功", result);
    }

    @GetMapping("/list")
    @Operation(summary = "查询宠物列表（支持多条件组合筛选）")
    public ApiResponse<?> listPets(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String breedName,
            @RequestParam(required = false) Boolean hasAppointment
    ) {
        List<Pet> allPets = petRepository.findAll()
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .filter(p -> petId == null || p.getPetId().equals(petId))
                .filter(p -> customerName == null || p.getCustomerName().equalsIgnoreCase(customerName))
                .filter(p -> petName == null || p.getPetName().equalsIgnoreCase(petName))
                .filter(p -> breedName == null || p.getBreedName().equalsIgnoreCase(breedName))
                .filter(p -> {
                    if (hasAppointment == null) return true;
                    boolean exists = appointmentRepository.existsByPetId(p.getId());
                    return hasAppointment == exists;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Pet p : allPets) {
            Map<String, Object> item = new HashMap<>();
            item.put("petId", p.getPetId());
            item.put("customerName", p.getCustomerName());
            item.put("petName", p.getPetName());
            item.put("breedName", p.getBreedName());
            item.put("gender", p.getGender());
            item.put("hasAppointment", appointmentRepository.existsByPetId(p.getId()));
            result.add(item);
        }

        return ApiResponse.success("查询成功", result);
    }



}
