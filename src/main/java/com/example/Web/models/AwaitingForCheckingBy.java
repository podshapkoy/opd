package com.example.Web.models;

import javax.persistence.*;

@Entity
public class AwaitingForCheckingBy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Adjective adjective;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Occupation occupation;


    public AwaitingForCheckingBy(User user,Occupation occupation, Adjective adjective) {
        this.occupation = occupation;
        this.adjective = adjective;
        this.user = user;
    }

    public AwaitingForCheckingBy() {

    }
}
