package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.dto.AddPetRequest;
import com.petshop.crmbackend.entity.Pet;
import com.petshop.crmbackend.repository.PetRepository;
import com.petshop.crmbackend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetRepository petRepository;

    public PetController(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @PostMapping("/add")
    @Operation(summary = "添加宠物信息")
    public ApiResponse<?> addPet(@Valid @RequestBody AddPetRequest request) {
        // 生成唯一 petId（五位数）
        String petId = generateUniquePetId();
        if (petId == null) {
            return ApiResponse.error(400, "系统繁忙，请稍后再试");
        }

        // 解析 birthday 支持多种格式
        LocalDate birthday = parseBirthday(request.getBirthday());
        if (birthday == null && request.getBirthday() != null) {
            return ApiResponse.error(400, "生日格式不正确，应为 yyyy-MM-dd 或 yyyy/MM/dd");
        }

        // 若 petId 或信息重复，禁止添加
        boolean exists = petRepository.existsByPetId(petId) ||
                petRepository.existsByCustomerNameAndSpeciesAndBreedNameAndPetNameAndGenderAndBirthday(
                        request.getCustomerName(),
                        request.getSpecies(),
                        request.getBreedName(),
                        request.getPetName(),
                        request.getGender(),
                        birthday
                );

        if (exists) {
            //return ApiResponse.error(400, "宠物信息已添加，不可重复添加");
            return ApiResponse.error(400, "宠物信息已添加，petId 为: " + petId);
        }


        Pet pet = new Pet();
        pet.setPetId(petId);
        pet.setCustomerName(request.getCustomerName());
        pet.setSpecies(request.getSpecies());
        pet.setBreedName(request.getBreedName());
        pet.setPetName(request.getPetName());
        pet.setGender(request.getGender());
        pet.setBirthday(birthday);
        pet.setProfilePhoto(request.getProfilePhoto());
        pet.setDescription(request.getDescription());
        pet.setCreatedAt(LocalDateTime.now());

        petRepository.save(pet);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("petId", pet.getPetId());
        return ApiResponse.success("注册成功", responseData);
    }

    // 生成唯一 petId（5位数字）
    private String generateUniquePetId() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            String candidate = String.format("%05d", random.nextInt(100000));
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
    public ApiResponse<?> deletePet(@PathVariable String petId) {
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



}
