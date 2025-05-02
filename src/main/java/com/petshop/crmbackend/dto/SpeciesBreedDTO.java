// SpeciesBreedDTO.java
package com.petshop.crmbackend.dto;

import java.util.List;

public class SpeciesBreedDTO {
    private String species;
    private List<String> breeds;

    public SpeciesBreedDTO(String species, List<String> breeds) {
        this.species = species;
        this.breeds = breeds;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public List<String> getBreeds() {
        return breeds;
    }

    public void setBreeds(List<String> breeds) {
        this.breeds = breeds;
    }
}
