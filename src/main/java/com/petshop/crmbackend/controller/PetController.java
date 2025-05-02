package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.common.ApiResponse;
import com.petshop.crmbackend.dto.AddPetRequest;
import com.petshop.crmbackend.entity.Pet;
import com.petshop.crmbackend.repository.PetRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/pets")
@Tag(name = "宠物管理")
public class PetController {

    private final PetRepository petRepository;

    public PetController(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @PostMapping("/add")
    public ApiResponse<?> addPet(@Valid @RequestBody AddPetRequest request) {
        Pet pet = new Pet();
        pet.setCustomerName(request.getCustomerName());
        pet.setSpecies(request.getSpecies());
        pet.setBreedName(request.getBreedName());
        pet.setPetName(request.getPetName());
        pet.setGender(request.getGender());
        pet.setProfilePhoto(request.getProfilePhoto());
        pet.setDescription(request.getDescription());

        // 解析生日字符串，支持 yyyy-MM-dd 和 yyyy/MM/dd 格式
        String dateStr = request.getBirthday();
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                LocalDate birthday;
                if (dateStr.contains("/")) {
                    birthday = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                } else {
                    birthday = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE); // yyyy-MM-dd
                }
                pet.setBirthday(birthday);
            } catch (DateTimeParseException e) {
                return ApiResponse.error(400, "生日格式错误，请使用 yyyy-MM-dd 或 yyyy/MM/dd");
            }
        }

        pet.setCreatedAt(LocalDateTime.now());

        petRepository.save(pet);
        return ApiResponse.success("宠物添加成功", null);
    }


}
