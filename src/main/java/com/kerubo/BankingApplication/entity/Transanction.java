package com.kerubo.BankingApplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transanctions")
public class Transanction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transanctionId;
    private String transanctionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;

    // Use LocalDate to store only the date, no time or time zone information
    private LocalDate createdAt;

    // Automatically set createdAt before persisting
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDate.now();  // Set createdAt to current date
        }
    }
}
