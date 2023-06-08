package com.example.Web.models;

import javax.persistence.*;

@Entity
public class pvkDevelopment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String pvk;
    @Column(name = "criteria")
    private String criteria;
}