package com.eldarmamedov.unibank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fromCurr", nullable = false)
    private String fromCurr;

    @Column(name = "toCurr", nullable = false)
    private String toCurr;

    @Column(name = "rate", nullable = false)
    private Double rate;

}

