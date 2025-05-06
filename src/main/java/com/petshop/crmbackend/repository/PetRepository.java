package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    boolean existsByPetId(Long petId);

    Optional<Pet> findByPetId(Long petId);

    Optional<Pet> findByPetIdAndIsDeletedFalse(Long petId);

    boolean existsByPetIdAndIsDeletedFalse(Long petId);  // 删除 static

    boolean existsByCustomerNameAndSpeciesAndBreedNameAndPetNameAndGenderAndBirthday(
            String customerName,
            String species,
            String breedName,
            String petName,
            String gender,
            LocalDate birthday
    );
}