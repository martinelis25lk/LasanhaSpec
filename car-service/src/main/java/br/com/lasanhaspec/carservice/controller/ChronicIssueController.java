package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.dto.*;
import br.com.lasanhaspec.carservice.service.ChronicIssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/chronic-issues")
@RestController
public class ChronicIssueController {


    private final ChronicIssueService chronicIssueService;

    public ChronicIssueController(ChronicIssueService chronicIssueService){
        this.chronicIssueService = chronicIssueService;
    }



    // GET    /chronic-issues/models            todos os modelos com issue?
    @GetMapping("/models")
    public ResponseEntity<List<VehicleChronicSummaryDTO>> getAllModelsWithIssues(){
        return ResponseEntity.ok(chronicIssueService.getAllModelsWithIssues());

    }

    // GET    /chronic-issues                  listAll (com filtro opcional de status)
    @GetMapping
    public ResponseEntity<List<ChronicIssueCardDTO>> listAll(
            @RequestParam(required = false) String status
    ){
        return ResponseEntity.ok(chronicIssueService.listAll(status));
    }





    // GET    /chronic-issues/models/{id}       getModelChronicPage
    @GetMapping("/models/{id}")
    public ResponseEntity<VehicleChronicPageDTO> getModelChronicPage(@PathVariable("id") Long modelId){
        return ResponseEntity.ok(chronicIssueService.getModelChronicPage(modelId));
    }



    // GET    /chronic-issues/{id}              getIssueDetail - pagina de detalhe de 1 problema cronico
    @GetMapping("/{id}")
    public ResponseEntity<ChronicIssueDetailDTO> getIssueDetail(@PathVariable Long id){
        return ResponseEntity.ok(chronicIssueService.getIssueDetail(id));

    }


   // POST   /chronic-issues/{id}/occurrence   reportOccurrence
   @PostMapping("/{issueId}/occurrence")
   public ResponseEntity<Void> reportOccurrence(
           @PathVariable Long issueId,
           @RequestBody ReportOccurrenceRequestDTO dto
   ) {
       chronicIssueService.reportOccurrence(issueId, dto);
       return ResponseEntity.ok().build();
   }


    // POST   /chronic-issues                   createIssue
    @PostMapping()
    public ResponseEntity<Long> createIssue(
            @Valid @RequestBody  ChronicIssueDTO dto,
            Authentication authentication
            ){

        String email = authentication.getName();
        Long id = chronicIssueService.createIssue(dto, email);
        return ResponseEntity.ok(id);

    }


    @PutMapping("/{id}")
    public ResponseEntity<ChronicIssueDetailDTO> updateIssue(@Valid @PathVariable Long id, @RequestBody ChronicIssueDTO dto){
        return ResponseEntity.ok(chronicIssueService.updateChronicIssue(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id){
        chronicIssueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveIssue(@PathVariable Long id){
        chronicIssueService.approveChronicIssue(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> rejectIssue(@PathVariable Long id){
        chronicIssueService.rejectChronicIssue(id);
        return ResponseEntity.noContent().build();
    }


}
