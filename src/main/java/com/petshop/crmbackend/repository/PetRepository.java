package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
