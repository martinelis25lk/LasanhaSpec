package br.com.lasanhaspec.market_service.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@Entity
@Table(
        name = "fipe_price_history",
        indexes = {
                @Index(
                        name = "idx_fipe_snapshot_lookup",
                        columnList = "fipeBrandCode, fipeModelCode, fipeYearCode, fetchedAt "
                )
        }


)



    public class FipePriceSnapshot{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String fipeBrandCode;
        private String fipeModelCode;
        private String fipeYearCode;

        private BigDecimal price;

        // Ex: "junho de 2026" - vem direto da FIPE, é o que identifica "um mês novo"
        private String referenceMonth;

        private String brandName;
        private String modelName;
        // Ano-modelo real (ex: "2015"), diferente do fipeYearCode que é só o código
        // usado na URL da API (ex: "2015-1")
        private String modelYear;
        private String fuel;
        private String fipeCode;

        private LocalDateTime fetchedAt;

        public FipePriceSnapshot(String fipeBrandCode,
                                  String fipeModelCode,
                                  String fipeYearCode,
                                  BigDecimal price,
                                  String referenceMonth,
                                  String brandName,
                                  String modelName,
                                  String modelYear,
                                  String fuel,
                                  String fipeCode,
                                  LocalDateTime fetchedAt){

            this.fipeBrandCode = fipeBrandCode;
            this.fipeModelCode = fipeModelCode;
            this.fipeYearCode = fipeYearCode;
            this.price = price;
            this.referenceMonth = referenceMonth;
            this.brandName = brandName;
            this.modelName = modelName;
            this.modelYear = modelYear;
            this.fuel = fuel;
            this.fipeCode = fipeCode;
            this.fetchedAt = fetchedAt;
        }
    }

