package com.eldarmamedov.unibank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;


@Entity
@NoArgsConstructor
@Getter @Setter
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alpha_code")
    private String alphaCode;

}
