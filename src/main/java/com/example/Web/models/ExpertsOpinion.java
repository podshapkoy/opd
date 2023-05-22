package com.example.Web.models;

import javax.persistence.*;
//оценка экспертом профессии, добавленной пользователем
@Entity
//@Table(name = "ExpertsOpinion")

public class ExpertsOpinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Occupation occupation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Adjective adjective;

    public ExpertsOpinion(User user, Occupation occupation, Adjective adjective) {
        this.occupation = occupation;
        this.user = user;
        this.adjective = adjective;
    }

    public ExpertsOpinion() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Adjective getAdjective() {
        return adjective;
    }

    public void setAdjective(Adjective adjective) {
        this.adjective = adjective;
    }
}
