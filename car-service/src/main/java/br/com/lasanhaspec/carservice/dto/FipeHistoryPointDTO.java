package br.com.lasanhaspec.carservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FipeHistoryPointDTO {

    private String referenceMonth;
    private BigDecimal price;
    private LocalDateTime fetchedAt;

    public String getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(String referenceMonth) { this.referenceMonth = referenceMonth; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
}