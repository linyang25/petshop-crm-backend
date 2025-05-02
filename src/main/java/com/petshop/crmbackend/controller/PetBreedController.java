package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.repository.PetBreedRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/pet-breeds")
@Tag(name = "宠物品种管理")
public class PetBreedController {

    private final PetBreedRepository petBreedRepository;

    public PetBreedController(PetBreedRepository petBreedRepository) {
        this.petBreedRepository = petBreedRepository;
    }

    @GetMapping("/grouped")
    @Operation(summary = "获取所有品种")
    public List<Map<String, Object>> getGroupedBreeds() {
        List<String> speciesList = petBreedRepository.findAllDistinctSpecies();
        List<Map<String, Object>> result = new ArrayList<>();

        for (String species : speciesList) {
            List<String> breeds = petBreedRepository.findBreedNamesBySpecies(species);
            Map<String, Object> entry = new HashMap<>();
            entry.put("species", species);
            entry.put("breeds", breeds);
            result.add(entry);
        }

        return result;
    }
}
