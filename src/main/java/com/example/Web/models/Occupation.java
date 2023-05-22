package com.example.Web.models;

import com.example.Web.repo.OccupationRepository;

import javax.persistence.*;

import java.util.Optional;
import java.util.Set;

//Таблица всех профессий и описаний к ним
@Entity
public class Occupation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name, description;

    @OneToMany(mappedBy = "occupation")
    private Set<ExpertsOpinion> expertsOpinions;
    public Occupation(String occupation_name,String description){
        this.name=occupation_name;
        this.description=description;
    }

    public Occupation() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ExpertsOpinion> getExpertsOpinions() {
        return expertsOpinions;
    }

    public void setExpertsOpinions(Set<ExpertsOpinion> expertsOpinions) {
        this.expertsOpinions = expertsOpinions;
    }
}
