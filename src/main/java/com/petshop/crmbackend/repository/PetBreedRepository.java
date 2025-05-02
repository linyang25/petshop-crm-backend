package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.PetBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetBreedRepository extends JpaRepository<PetBreed, Long> {

    // 1. 根据种类查找品种名
    @Query("SELECT b.breedName FROM PetBreed b WHERE b.species = :species")
    List<String> findBreedNamesBySpecies(String species);

    // 2. 查询所有 distinct 的种类
    @Query("SELECT DISTINCT b.species FROM PetBreed b")
    List<String> findAllDistinctSpecies();
}
