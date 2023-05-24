package com.example.Web.models;

import javax.persistence.*;
//результаты теста после прохождения пользователем

@Entity
//@Table(name = "allTestsResult")
public class AllTestsResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private FinishedSessionUserTest finishedSessionUserTest;

    private Double result;
    private String label;

    public AllTestsResult(FinishedSessionUserTest finishedSessionUserTest, Double result, String label) {
        this.finishedSessionUserTest = finishedSessionUserTest;
        this.result = result;
        this.label = label;
    }

    public AllTestsResult() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FinishedSessionUserTest getFinishedSessionUserTest() {
        return finishedSessionUserTest;
    }

    public void setFinishedSessionUserTest(FinishedSessionUserTest finishedSessionUserTest) {
        this.finishedSessionUserTest = finishedSessionUserTest;
    }

    public Double getResult_ms() {
        return result;
    }

    public void setResult_ms(Double result_ms) {
        this.result = result_ms;
    }
}
