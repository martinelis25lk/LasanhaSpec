package br.com.lasanhaspec.market_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FipePriceHistoryPointDTO {

    private String referencedMonth;
    private BigDecimal price;
    private LocalDateTime fetchedAt;
    }


