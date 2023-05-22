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

    private String result_ms;

    public AllTestsResult(FinishedSessionUserTest finishedSessionUserTest, String result_ms) {
        this.finishedSessionUserTest = finishedSessionUserTest;
        this.result_ms = result_ms;
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

    public String getResult_ms() {
        return result_ms;
    }

    public void setResult_ms(String result_ms) {
        this.result_ms = result_ms;
    }
}
