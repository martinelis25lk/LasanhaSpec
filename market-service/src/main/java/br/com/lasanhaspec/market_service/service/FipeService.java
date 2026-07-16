package br.com.lasanhaspec.market_service.service;

import br.com.lasanhaspec.market_service.client.FipeClient;
import br.com.lasanhaspec.market_service.domain.FipePriceSnapshot;
import br.com.lasanhaspec.market_service.dto.ExternalFipeResponseDTO;
import br.com.lasanhaspec.market_service.dto.FipePriceHistoryPointDTO;
import br.com.lasanhaspec.market_service.dto.FipePriceResponseDTO;
import br.com.lasanhaspec.market_service.repository.FipePriceSnapshotRepository;
import br.com.lasanhaspec.market_service.util.FipeValueParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class FipeService {

    private static final Logger log = LoggerFactory.getLogger(FipeService.class);

    // Quanto tempo um snapshot ainda é considerado "fresco" o suficiente pra
    // não precisar bater na API pública de novo. A FIPE só publica uma tabela
    // nova por mês, então não tem por que consultar toda hora.
    private static final long CACHE_TTL_DAYS = 20;

    private final FipeClient fipeClient;
    private final FipePriceSnapshotRepository snapshotRepository;

    public FipeService(FipeClient fipeClient, FipePriceSnapshotRepository snapshotRepository) {
        this.fipeClient = fipeClient;
        this.snapshotRepository = snapshotRepository;
    }

    public FipePriceResponseDTO getPrice(String brandCode, String modelCode, String yearCode) {

        Optional<FipePriceSnapshot> cached = snapshotRepository
                .findTopByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtDesc(
                        brandCode, modelCode, yearCode);

        if (cached.isPresent() && isFresh(cached.get())) {
            return toDTO(cached.get());
        }

        try {
            ExternalFipeResponseDTO external = fipeClient.getPrice(brandCode, modelCode, yearCode);
            FipePriceSnapshot saved = saveSnapshotIfNewMonth(brandCode, modelCode, yearCode, external, cached);
            return toDTO(saved);

        } catch (Exception e) {
            // A API pública da FIPE é gratuita e não tem SLA - se ela falhar,
            // é melhor devolver um preço um pouco desatualizado do que nenhum preço
            if (cached.isPresent()) {
                log.warn("FIPE externa indisponível, devolvendo cache de {} para {}/{}/{}: {}",
                        cached.get().getFetchedAt(), brandCode, modelCode, yearCode, e.getMessage());
                return toDTO(cached.get());
            }
            log.warn("FIPE externa indisponível e sem cache para {}/{}/{}: {}",
                    brandCode, modelCode, yearCode, e.getMessage());
            throw new RuntimeException("Erro ao buscar dados da FIPE", e);
        }
    }

    public List<FipePriceHistoryPointDTO> getHistory(String brandCode, String modelCode, String yearCode) {
        return snapshotRepository
                .findByFipeBrandCodeAndFipeModelCodeAndFipeYearCodeOrderByFetchedAtAsc(brandCode, modelCode, yearCode)
                .stream()
                .map(s -> new FipePriceHistoryPointDTO(s.getReferenceMonth(), s.getPrice(), s.getFetchedAt()))
                .toList();
    }

    private boolean isFresh(FipePriceSnapshot snapshot) {
        return ChronoUnit.DAYS.between(snapshot.getFetchedAt(), LocalDateTime.now()) < CACHE_TTL_DAYS;
    }

    private FipePriceSnapshot saveSnapshotIfNewMonth(String brandCode, String modelCode, String yearCode,
                                                     ExternalFipeResponseDTO external,
                                                     Optional<FipePriceSnapshot> lastKnown) {

        BigDecimal price = FipeValueParser.parse(external.getValor());
        boolean sameMonthAsLast = lastKnown.isPresent()
                && lastKnown.get().getReferenceMonth() != null
                && lastKnown.get().getReferenceMonth().equals(external.getMesReferencia());

        if (sameMonthAsLast) {
            FipePriceSnapshot existing = lastKnown.get();
            existing.setPrice(price);
            existing.setFetchedAt(LocalDateTime.now());
            return snapshotRepository.save(existing);
        }

        FipePriceSnapshot newSnapshot = new FipePriceSnapshot(
                brandCode, modelCode, yearCode,
                price,
                external.getMesReferencia(),
                external.getMarca(),
                external.getModelo(),
                external.getAnoModelo(),
                external.getCombustivel(),
                external.getCodigoFipe(),
                LocalDateTime.now()
        );
        return snapshotRepository.save(newSnapshot);
    }

    private FipePriceResponseDTO toDTO(FipePriceSnapshot snapshot) {
        return new FipePriceResponseDTO(
                FipeValueParser.format(snapshot.getPrice()),
                snapshot.getBrandName(),
                snapshot.getModelName(),
                snapshot.getModelYear(),
                snapshot.getFuel(),
                snapshot.getFipeCode(),
                snapshot.getReferenceMonth()
        );
    }
}