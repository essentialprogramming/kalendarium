package com.api.entities;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Country")
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "countryid", nullable = false, unique = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "defaultlocale")
    private String defaultLocale;

    @Column(name = "displayorder")
    private int displayOrder;

    @Column(name = "countrycode")
    private String countryCode;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<State> states;




}
