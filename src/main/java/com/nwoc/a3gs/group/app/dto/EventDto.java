package com.nwoc.a3gs.group.app.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class EventDto {
    private Long id;

    private String name;

    private String image;

    private String description;

    private String bannerImage;

    @NotNull(message = "Please Upload tile Image")
    private MultipartFile files;

    @NotNull(message = "Please Upload banner Image")
    private MultipartFile bannerFiles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public MultipartFile getFiles() {
        return files;
    }

    public void setFiles(MultipartFile files) {
        this.files = files;
    }

    public MultipartFile getBannerFiles() {
        return bannerFiles;
    }

    public void setBannerFiles(MultipartFile bannerFiles) {
        this.bannerFiles = bannerFiles;
    }
}
