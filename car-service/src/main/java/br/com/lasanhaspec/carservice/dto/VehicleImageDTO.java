package br.com.lasanhaspec.carservice.dto;

public class VehicleImageDTO {

    private Long id;
    private String imageUrl;
    private Boolean primaryImage;

    public VehicleImageDTO(Long id, String imageUrl, Boolean primaryImage) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.primaryImage = primaryImage;
    }

    public Long getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public Boolean getPrimaryImage() { return primaryImage; }
}