package com.example.Web.models;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

//Список всех прилагательных ПВК
@Entity
//@Table(name = "adjective")
public class Adjective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column(name = "category")
    private String category;

    public Adjective(String trait_name) {
        this.name = trait_name;

    }


    public Adjective() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
