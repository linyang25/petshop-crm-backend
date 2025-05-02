package com.petshop.crmbackend.entity;

import javax.persistence.*;

@Entity
@Table(name = "pet_breed")
public class PetBreed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String species;      // 宠物种类（猫、狗）
    private String breedName;    // 品种名称（如布偶猫）
    private String description;  // 品种描述（可选）
    private String imageUrl;     // 图片链接（可选）

    // 无参构造函数
    public PetBreed() {
    }

    // 带参构造函数（可选）
    public PetBreed(String species, String breedName, String description, String imageUrl) {
        this.species = species;
        this.breedName = breedName;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
