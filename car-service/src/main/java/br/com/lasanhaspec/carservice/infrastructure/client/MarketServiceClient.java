package br.com.lasanhaspec.carservice.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import br.com.lasanhaspec.carservice.dto.FipeHistoryPointDTO;
import br.com.lasanhaspec.carservice.dto.FipeResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;

@Component
public class MarketServiceClient {



    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(MarketServiceClient.class);

    public MarketServiceClient(WebClient.Builder builder,
                               @Value("${market-service.url}") String marketServiceUrl) {
        this.webClient = builder
                .baseUrl(marketServiceUrl)
                .build();
    }

    public FipeResponseDTO getFipePrice(String brandCode, String modelCode, String yearCode) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/market/fipe/price")
                            .queryParam("brandCode", brandCode)
                            .queryParam("modelCode", modelCode)
                            .queryParam("yearCode", yearCode)
                            .build())
                    .retrieve()
                    .bodyToMono(FipeResponseDTO.class)
                    .block();
        } catch (Exception e) {
            log.warn("FIPE service unavailable for vehicle request: {}", e.getMessage(), e);
            return null;
        }
    }

    public List<FipeHistoryPointDTO> getFipeHistory(String brandCode, String modelCode, String yearCode) {
        try {
            List<FipeHistoryPointDTO> history = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/market/fipe/history")
                            .queryParam("brandCode", brandCode)
                            .queryParam("modelCode", modelCode)
                            .queryParam("yearCode", yearCode)
                            .build())
                    .retrieve()
                    .bodyToFlux(FipeHistoryPointDTO.class)
                    .collectList()
                    .block();
            return history != null ? history : Collections.emptyList();
        } catch (Exception e) {
            log.warn("FIPE history unavailable for vehicle request: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }






}