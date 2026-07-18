package br.com.lasanhaspec.carservice.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class ChronicIssueDTO {

    @NotNull
    @Positive
    private Long vehicleCatalogModelId;


    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    @Size(min = 3, max = 2000)
    private String description;

    @NotBlank
    @Size(min = 3, max = 20)
    private String severity;

    @NotBlank
    @Size(min = 3, max = 20)
    private String category;



    @PositiveOrZero
    private Integer millageMin;


    @PositiveOrZero
    private Integer millageMax;


    @NotNull
    @PositiveOrZero
    private Integer costMin;

    @NotNull
    @PositiveOrZero
    private Integer costMax;

    @NotBlank
    @Size(min = 2, max = 100)
    private String affectedEngines;

    @NotBlank
    @Size(min = 4, max = 100)
    private String affectedYears;

    @NotBlank
    @Size(min = 3, max = 20)
    private String repairComplexity;



    @NotNull
    @Positive
    private Long createdByUserId;

    private List<String> symptoms;

    private List<String> preventiveMaintenance;

    public String getIssueStatus() {
        return IssueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        IssueStatus = issueStatus;
    }

    private String IssueStatus;



    public Long getVehicleCatalogModelId() {
        return vehicleCatalogModelId;
    }

    public void setVehicleCatalogModelId(Long vehicleCatalogModelId) {
        this.vehicleCatalogModelId = vehicleCatalogModelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getMillageMin() {
        return millageMin;
    }

    public void setMillageMin(Integer millageMin) {
        this.millageMin = millageMin;
    }

    public Integer getMillageMax() {
        return millageMax;
    }

    public void setMillageMax(Integer millageMax) {
        this.millageMax = millageMax;
    }

    public Integer getCostMin() {
        return costMin;
    }

    public void setCostMin(Integer costMin) {
        this.costMin = costMin;
    }

    public Integer getCostMax() {
        return costMax;
    }

    public void setCostMax(Integer costMax) {
        this.costMax = costMax;
    }

    public String getAffectedEngines() {
        return affectedEngines;
    }

    public void setAffectedEngines(String affectedEngines) {
        this.affectedEngines = affectedEngines;
    }

    public String getAffectedYears() {
        return affectedYears;
    }

    public void setAffectedYears(String affectedYears) {
        this.affectedYears = affectedYears;
    }

    public String getRepairComplexity() {
        return repairComplexity;
    }

    public void setRepairComplexity(String repairComplexity) {
        this.repairComplexity = repairComplexity;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getPreventiveMaintenance() {
        return preventiveMaintenance;
    }

    public void setPreventiveMaintenance(List<String> preventiveMaintenance) {
        this.preventiveMaintenance = preventiveMaintenance;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }




}
