package com.eldarmamedov.unibank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fin", nullable = false)
    private String fin;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts = new ArrayList<>();

    public User(String fin, String name, String surname, String phoneNumber) {
        this.fin = fin;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }
}
