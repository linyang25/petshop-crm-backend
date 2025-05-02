package com.petshop.crmbackend.dto;

import javax.validation.constraints.NotBlank;

public class AddPetRequest {

    @NotBlank(message = "客户名不能为空")
    private String customerName;

    @NotBlank(message = "宠物种类不能为空")
    private String species;

    @NotBlank(message = "宠物品种不能为空")
    private String breedName;

    @NotBlank(message = "宠物昵称不能为空")
    private String petName;

    private String gender;
    private String birthday;
    private String profilePhoto;  // 可选：图片链接
    private String description;   // 可选：品种或宠物说明


    // Getter & Setter
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
