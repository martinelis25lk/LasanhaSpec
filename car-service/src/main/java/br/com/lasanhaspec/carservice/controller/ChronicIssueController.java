package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.dto.*;
import br.com.lasanhaspec.carservice.service.ChronicIssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/chronic-issues")
public class ChronicIssueController {

    private final ChronicIssueService chronicIssueService;

    public ChronicIssueController(ChronicIssueService chronicIssueService){
        this.chronicIssueService = chronicIssueService;
    }

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<ChronicIssueCardDTO>> listAll(
            @RequestParam(required = false) String status
    ){
        return ResponseEntity.ok(chronicIssueService.listAll(status));
    }

    // MODELOS COM ISSUES
    @GetMapping("/models")
    public ResponseEntity<List<VehicleChronicSummaryDTO>> getAllModelsWithIssues(){
        return ResponseEntity.ok(chronicIssueService.getAllModelsWithIssues());
    }

    // DETALHE DO MODEL
    @GetMapping("/models/{id}")
    public ResponseEntity<VehicleChronicPageDTO> getModelChronicPage(@PathVariable Long id){
        return ResponseEntity.ok(chronicIssueService.getModelChronicPage(id));
    }

    // DETALHE DO ISSUE
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ChronicIssueDetailDTO> getIssueDetail(@PathVariable Long id){
        return ResponseEntity.ok(chronicIssueService.getIssueDetail(id));
    }

    // REPORTAR OCORRÊNCIA
    @PostMapping("/{issueId}/occurrence")
    public ResponseEntity<Void> reportOccurrence(
            @PathVariable Long issueId,
            @RequestBody ReportOccurrenceRequestDTO dto
    ) {
        chronicIssueService.reportOccurrence(issueId, dto);
        return ResponseEntity.ok().build();
    }

    // CRIAR
    @PostMapping
    public ResponseEntity<Long> createIssue(
            @Valid @RequestBody ChronicIssueDTO dto,
            Authentication authentication
    ){
        String email = authentication.getName();
        Long id = chronicIssueService.createIssue(dto, email);
        return ResponseEntity.ok(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ChronicIssueDetailDTO> updateIssue(
            @PathVariable Long id,
            @RequestBody ChronicIssueDTO dto
    ){
        return ResponseEntity.ok(chronicIssueService.updateChronicIssue(id, dto));
    }

    // DELETE
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
