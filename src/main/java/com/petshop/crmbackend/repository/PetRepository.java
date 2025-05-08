package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    boolean existsByPetId(Long petId);

    Optional<Pet> findByPetId(Long petId);

    Optional<Pet> findByPetIdAndIsDeletedFalse(Long petId);

    boolean existsByPetIdAndIsDeletedFalse(Long petId);  // 删除 static

    Optional<Pet> findByCustomerNameAndSpeciesAndBreedNameAndPetNameAndGenderAndBirthday(
            String customerName,
            String species,
            String breedName,
            String petName,
            String gender,
            LocalDate birthday
    );
    long countByIsDeletedFalse();

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p.species, COUNT(p) FROM Pet p WHERE p.isDeleted = false GROUP BY p.species")
    List<Object[]> countBySpecies();

    @Query("SELECT p.breedName, COUNT(p) FROM Pet p WHERE p.isDeleted = false GROUP BY p.breedName")
    List<Object[]> countByBreed();

    @Query("SELECT AVG(DATEDIFF(CURRENT_DATE, p.birthday)) FROM Pet p WHERE p.isDeleted = false")
    Double averageAgeDays();
}
