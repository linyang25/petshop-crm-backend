package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Pet;
import com.petshop.crmbackend.repository.projection.LabelValueProjection;
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

//    @Query("SELECT p.species, COUNT(p) FROM Pet p WHERE p.isDeleted = false GROUP BY p.species")
//    List<Object[]> countBySpecies();



    @Query("SELECT AVG(DATEDIFF(CURRENT_DATE, p.birthday)) FROM Pet p WHERE p.isDeleted = false")
    Double averageAgeDays();

    List<Pet> findByPetNameContaining(String petName);
    /**
     * 新增：根据业务主键列表，批量查所有宠物
     */
    List<Pet> findAllByPetIdIn(List<Long> petIds);
    /** 按物种统计（排除已删除宠物） */
    @Query(
            "SELECT p.species AS label, COUNT(p) AS value " +
                    "FROM Pet p " +
                    "WHERE p.isDeleted = false " +
                    "GROUP BY p.species"
    )
    List<LabelValueProjection> countBySpecies();

    /**
     * 按品种统计（排除已删除宠物）
     */
    @Query(
            "SELECT p.breedName AS label, COUNT(p) AS value " +
                    "FROM Pet p " +
                    "WHERE p.isDeleted = false " +
                    "GROUP BY p.breedName"
    )
    List<LabelValueProjection> countByBreed();


}
