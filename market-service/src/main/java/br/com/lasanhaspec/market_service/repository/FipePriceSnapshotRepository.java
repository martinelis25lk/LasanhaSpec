package br.com.lasanhaspec.market_service.repository;

import br.com.lasanhaspec.market_service.domain.FipePriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class FipePriceSnapshotRepository extends JpaRepository<FipePriceSnapshot, Long> {


    // "Cache": me dá o retrato mais recente que eu já tenho pra esse veículo
    Optional<FipePriceSnapshot> findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
            String fipeBandCode, String fipeModelCode, String fipeYearCode
    );

    // "Histórico": me dá todos os retratos, do mais antigo pro mais novo, pra montar o gráfico
    List<FipePriceSnapshot> findByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtAsc(
            String fipeBrandCode, String fipeModelCode, String fipeYearCode
    );




}
