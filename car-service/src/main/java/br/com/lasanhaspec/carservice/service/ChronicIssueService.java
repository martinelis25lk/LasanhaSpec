package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.enums.IssueCategory;
import br.com.lasanhaspec.carservice.domain.enums.IssueSeverity;
import br.com.lasanhaspec.carservice.domain.enums.IssueStatus;
import br.com.lasanhaspec.carservice.domain.enums.RepairComplexity;
import br.com.lasanhaspec.carservice.domain.models.*;
import br.com.lasanhaspec.carservice.dto.*;
import br.com.lasanhaspec.carservice.exception.BusinessException;
import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.repository.*;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChronicIssueService {


    private final ChronicIssueRepository chronicIssueRepository;
    private final VehicleCatalogRepository vehicleCatalogRepository;
    private final IssueOccurrenceRepository issueOccurrenceRepository;
    private final ReliabilityScoreCalculator reliabilityScoreCalculator;
    private final UserVehicleRepository userVehicleRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(ChronicIssueService.class);



    public ChronicIssueService(
            ChronicIssueRepository chronicIssueRepository,
            VehicleCatalogRepository vehicleCatalogRepository,
            IssueOccurrenceRepository issueOccurrenceRepository,
            ReliabilityScoreCalculator reliabilityScoreCalculator,
            UserVehicleRepository userVehicleRepository,
            UserRepository userRepository
    ){
        this.chronicIssueRepository = chronicIssueRepository;
        this.vehicleCatalogRepository = vehicleCatalogRepository;
        this.issueOccurrenceRepository = issueOccurrenceRepository;
        this.reliabilityScoreCalculator = reliabilityScoreCalculator;
        this.userVehicleRepository = userVehicleRepository;
        this.userRepository = userRepository;

    }




    //criar um novo cronico
    public Long createIssue(ChronicIssueDTO chronicIssueDTO, String email){



        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        VehicleCatalogModel model = vehicleCatalogRepository
                .findById(chronicIssueDTO.getVehicleCatalogModelId())
                .orElseThrow(() -> new ResourceNotFoundException("catalog vehicle not found"));

        boolean userHasThisModel = userVehicleRepository
                .existsByUserIdAndVehicleCatalogModelId(user.getId(), model.getId());

        if (!userHasThisModel) {
            throw new BusinessException("You must have this vehicle model in your garage to create a chronic issue");
        }
        //cria a entidade nova

        log.info("User {} created chronic issue '{}' for vehicle model {}",
                email, user.getFullName(), model.getId());

        ChronicIssue chronic = new ChronicIssue();

        chronic.setVehicleCatalogModel(model);
        chronic.setTitle(chronicIssueDTO.getTitle());
        chronic.setDescription(chronicIssueDTO.getDescription());
        chronic.setSeverity(IssueSeverity.valueOf(chronicIssueDTO.getSeverity()));
        chronic.setStatus(IssueStatus.PENDING);
        chronic.setIssueCategory(IssueCategory.valueOf(chronicIssueDTO.getCategory()));
        chronic.setMillageMax(chronicIssueDTO.getMillageMax());
        chronic.setMillageMin(chronicIssueDTO.getMillageMin());
        chronic.setCostMax(chronicIssueDTO.getCostMax());
        chronic.setCostMin(chronicIssueDTO.getCostMin());
        chronic.setAffectedEngines(chronicIssueDTO.getAffectedEngines());
        chronic.setAffectedYears(chronicIssueDTO.getAffectedYears());
        chronic.setRepairComplexity(RepairComplexity.valueOf(chronicIssueDTO.getRepairComplexity()));
        chronic.setSymptoms(chronicIssueDTO.getSymptoms());
        chronic.setPreventiveMaintenance(chronicIssueDTO.getPreventiveMaintenance());
        chronic.setCreatedByUserId(user.getId());

        return chronicIssueRepository.save(chronic).getId();


    }



    public ChronicIssueDetailDTO updateChronicIssue(Long id, ChronicIssueDTO  dto){
        ChronicIssue chronicIssue = chronicIssueRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("chronic issue not found"));


        chronicIssue.setSymptoms(dto.getSymptoms());
        chronicIssue.setAffectedEngines(dto.getAffectedEngines());
        chronicIssue.setAffectedYears(dto.getAffectedYears());
        chronicIssue.setRepairComplexity(RepairComplexity.valueOf(dto.getRepairComplexity()));
        chronicIssue.setPreventiveMaintenance(dto.getPreventiveMaintenance());

        chronicIssueRepository.save(chronicIssue);

        ChronicIssueDetailDTO chronicIssueDetailDTO = new ChronicIssueDetailDTO();

        chronicIssueDetailDTO.setRepairComplexity(chronicIssue.getRepairComplexity());
        chronicIssueDetailDTO.setAffectedYears(chronicIssue.getAffectedYears());
        chronicIssueDetailDTO.setAffectedEngines(chronicIssue.getAffectedEngines());
        chronicIssueDetailDTO.setSymptoms(chronicIssue.getSymptoms());
        chronicIssueDetailDTO.setPreventiveMaintenance(chronicIssue.getPreventiveMaintenance());


        return chronicIssueDetailDTO;

    }


    public void deleteIssue(Long id){
        ChronicIssue chronicIssue = chronicIssueRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("chronic issue not found"));

        chronicIssueRepository.delete(chronicIssue);

    }






    //private IssueStatus status;
    // PENDING, APPROVED, REJECTED

    public void approveChronicIssue(Long id){

        ChronicIssue chronicIssue = chronicIssueRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("issue not found"));


        if(chronicIssue.getStatus() == IssueStatus.IN_REVIEW
                || chronicIssue.getStatus() == IssueStatus.PENDING) {
            chronicIssue.setStatus(IssueStatus.APPROVED);
            chronicIssueRepository.save(chronicIssue);
        }

    }


    public void rejectChronicIssue(Long id){
        ChronicIssue chronicIssue = chronicIssueRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("issue not found"));

        if (chronicIssue.getStatus() != IssueStatus.IN_REVIEW
                && chronicIssue.getStatus() != IssueStatus.PENDING) {
            throw new BusinessException("Only pending or in review issues can be rejected");
        }

        chronicIssue.setStatus(IssueStatus.REJECTED);
        chronicIssueRepository.save(chronicIssue);

    }





    //lista todos os modelos q tem cronicos approvados
    public List<VehicleChronicSummaryDTO> getAllModelsWithIssues() {

        List<VehicleCatalogModel> models =
                vehicleCatalogRepository.findModelsWithIssuesByStatus(IssueStatus.APPROVED);

        return models.stream().map(model -> {

            List<ChronicIssue> issues =
                    chronicIssueRepository.findByVehicleCatalogModelIdAndStatus(
                            model.getId(),
                            IssueStatus.APPROVED
                    );

            Double reliabilityScore = reliabilityScoreCalculator.calculate(issues);
            Double criticalFailureRate = calculateCriticalFailureRate(issues);
            Double avgAnnualCost = calculateAvgAnualCost(issues);

            List<String> topIssueNames = issues.stream()
                    .sorted((a, b) -> Integer.compare(b.getUsefulVotes(), a.getUsefulVotes()))
                    .limit(3)
                    .map(ChronicIssue::getTitle)
                    .toList();

            VehicleChronicSummaryDTO dto = new VehicleChronicSummaryDTO();
            dto.setId(model.getId());
            dto.setName(model.getBrand() + " " + model.getModel());
            dto.setImageUrl(null);
            dto.setReliabilityScore(reliabilityScore);
            dto.setAvgAnnualCost(avgAnnualCost);
            dto.setCriticalFailureRate(criticalFailureRate);
            dto.setTopIssueNames(topIssueNames);

            return dto;

        }).toList();
    }





    // detalhe de um cronico em especifico
    public ChronicIssueDetailDTO getIssueDetail(Long issueId) {

        ChronicIssue chronicIssue = chronicIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("chronic issue not found"));

        //  1. mapear para card
        ChronicIssueCardDTO cardDTO = new ChronicIssueCardDTO();

        cardDTO.setId(chronicIssue.getId());
        cardDTO.setNotUsefulVotes(chronicIssue.getNotUsefulVotes());
        cardDTO.setUsefulVotes(chronicIssue.getUsefulVotes());
        cardDTO.setOccurrences(chronicIssue.getOccurrences());
        cardDTO.setSeverity(chronicIssue.getSeverity().name());
        cardDTO.setTitle(chronicIssue.getTitle());
        cardDTO.setDescription(chronicIssue.getDescription());

        cardDTO.setMillageMax(chronicIssue.getMillageMax());

        cardDTO.setMillageMin(chronicIssue.getMillageMin());
        cardDTO.setCostMin(chronicIssue.getCostMin());
        cardDTO.setCostMax(chronicIssue.getCostMax());

        //  2. buscar occurrences
        List<IssueOcurrence> occurrences = issueOccurrenceRepository
                .findByChronicIssueId(issueId);

        //  3. mapear occurrences
        List<OccurrenceReportDTO> occurrenceDTOs = occurrences.stream()
                .map(occurrence -> {
                    OccurrenceReportDTO dto = new OccurrenceReportDTO();
                    dto.setMillageAtOccurrence(occurrence.getMillageAtOccurrence());
                    dto.setRepairCost(occurrence.getRepairCost());
                    dto.setDescription(occurrence.getDescription());
                    return dto;
                })
                .toList();

        //  4. montar DTO final
        ChronicIssueDetailDTO detailDTO = new ChronicIssueDetailDTO();

        detailDTO.setChronicIssueCardDTO(cardDTO);
        detailDTO.setSymptoms(chronicIssue.getSymptoms());
        detailDTO.setPreventiveMaintenance(chronicIssue.getPreventiveMaintenance());
        detailDTO.setAffectedEngines(chronicIssue.getAffectedEngines());
        detailDTO.setAffectedYears(chronicIssue.getAffectedYears());
        detailDTO.setRepairComplexity(chronicIssue.getRepairComplexity());

        // importante: você precisa ter esse campo no DTO
        detailDTO.setOccurrenceReports(occurrenceDTOs);

        return detailDTO;
    }




    //pagina completa de um modelo com seus cronicos
    public VehicleChronicPageDTO getModelChronicPage(Long vehicleId){

        // busca o modelo
        // busca os cronicos aprovados
        // calcula a metrica e monta o dto



        //1 - busca o modelo
        VehicleCatalogModel model = vehicleCatalogRepository
                .findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("model from vehicle not found"));


        //2 - busca os cronicos aprovados desse modelo

        List<ChronicIssue> issues =
                chronicIssueRepository.findByVehicleCatalogModelIdAndStatus(vehicleId,IssueStatus.APPROVED);

        //3 - calcula as métricas


        Double reliabilityScore = reliabilityScoreCalculator.calculate(issues);
        Double criticalFailureRate = calculateCriticalFailureRate(issues);
        Double avgAnnualCost = calculateAvgAnualCost(issues);


        //4 - mapeia cada ChronicIssue para ChronicIssueCardDTO

        List<ChronicIssueCardDTO> issueDTOs = issues.stream()
                .map(issue -> {
                    ChronicIssueCardDTO dto = new ChronicIssueCardDTO();
                    dto.setId(issue.getId());
                    dto.setTitle(issue.getTitle());
                    dto.setCostMax(issue.getCostMax());
                    dto.setCostMin(issue.getCostMin());
                    dto.setDescription(issue.getDescription());
                    dto.setSeverity(issue.getSeverity().name());
                    dto.setOccurrences(issue.getOccurrences());
                    dto.setMillageMax(issue.getMillageMax());
                    dto.setMillageMin(issue.getMillageMin());
                    dto.setUsefulVotes(issue.getUsefulVotes());
                    dto.setNotUsefulVotes(issue.getNotUsefulVotes());
                    return dto;
                }).toList();

        VehicleChronicPageDTO dto = new VehicleChronicPageDTO();

        dto.setName(model.getBrand() + " " + model.getModel()); // <- model, não issue
        dto.setYear(model.getYear());
        dto.setDocumentedIssues(issues.size()); // <- tamanho da lista de crônicos
        dto.setReliabilityScore(reliabilityScore);
        dto.setAvgAnnualCost(avgAnnualCost);
        dto.setCriticalFailureRate(criticalFailureRate);
        dto.setIssues(issueDTOs);




        return dto;



    }






    //registrar ocorrencia
    public void reportOccurrence(Long issueId, ReportOccurrenceRequestDTO dto) {

        ChronicIssue issue = chronicIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        UserVehicle vehicle = userVehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("vehicle not found"));

        if (!vehicle.getVehicleCatalogModel().getId()
                .equals(issue.getVehicleCatalogModel().getId())) {
            throw new BusinessException("vehicle from uservehicle and issue vehicle do not match");
        }

        if (issueOccurrenceRepository.existsByChronicIssueIdAndUserVehicleId(issueId, dto.getVehicleId())) {
            throw new BusinessException("occurrence already registered to this vehicle");
        }

        IssueOcurrence occurrence = new IssueOcurrence();
        occurrence.setChronicIssue(issue);
        occurrence.setUserVehicle(vehicle);
        occurrence.setMillageAtOccurrence(dto.getMillageAtOccurrence());
        occurrence.setRepairCost(dto.getRepairCost());
        occurrence.setDescription(dto.getDescription());

        issueOccurrenceRepository.save(occurrence);

        issue.setOccurrences(issue.getOccurrences() + 1);
        chronicIssueRepository.save(issue);
    }



    private Double calculateCriticalFailureRate(List<ChronicIssue> issues){


        if(issues == null || issues.isEmpty()) {
            return 0.0;
        }

        long criticalCount = issues.stream()
                .filter(issue -> issue.getSeverity() == IssueSeverity.CRITICAL)
                .count();

        return (criticalCount * 100.0)/ issues.size();

    }


    private Double calculateAvgAnualCost(List<ChronicIssue> issues){

        if(issues == null || issues.isEmpty()){
            return 0.0;

        }

        return  issues.stream()
                .map(ChronicIssue::getCostMax)
                .filter(Objects::nonNull)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);

    }






    // 1 - contar total de issues
    // 2 - contar quantos tem severity critical
    // 3 - fazer a divisão e multiplicar por 100










//issuevote - esse problema é util ou relevante? qqr usuario pode votar

    //issueOccurrence - "eu tive esse problema no meu carro" usuario registra ua ocorrencia real
    // é o botão "i experienced this issue"




}
