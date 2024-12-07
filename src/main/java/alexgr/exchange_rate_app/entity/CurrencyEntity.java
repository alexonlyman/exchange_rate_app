package alexgr.exchange_rate_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "currency")
@Entity
public class CurrencyEntity {

    @Id
    @Column(name = "currency_code")
    private String currencyCode;
    @Column(name = "currency_description")
    private String currencyDescription;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "rates")
    private BigDecimal rates;
}
