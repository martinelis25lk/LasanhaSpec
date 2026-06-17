package br.com.lasanhaspec.carservice.infrastructure.client;

import br.com.lasanhaspec.carservice.dto.FipeResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MarketServiceClient {



    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(MarketServiceClient.class);

    public MarketServiceClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8081")
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




}