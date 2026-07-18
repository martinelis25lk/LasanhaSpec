package br.com.lasanhaspec.carservice.repository;


import br.com.lasanhaspec.carservice.domain.enums.IssueStatus;
import br.com.lasanhaspec.carservice.domain.models.ChronicIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChronicIssueRepository extends JpaRepository<ChronicIssue, Long> {

    List<ChronicIssue> findByVehicleCatalogModelIdAndStatus(Long vehicleCatalogModelId, IssueStatus status);



    List<ChronicIssue> findByStatus(IssueStatus status);


}
