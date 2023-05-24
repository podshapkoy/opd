package com.example.Web.models;

import java.util.List;

//Для хранения выбранных прилагательный или в целом для получения списков
public class Result {
    private int id;
    private List<String> labels;
    private String label;
    private int[] selectedIds;
    private String[] selectedNames;
    private Double result;
    private List<Double> results;

    public Result() {
    }

    public Result(List<Double> results, List<String> labels) {
        this.labels=labels;
        this.results = results;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Double> getResults() {
        return results;
    }

    public void setResults(List<Double> results) {
        this.results = results;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Result(Double result, String label) {
        this.label = label;
        this.result = result;
    }
    //    public List<String> getResult() {
//        return result;
//    }
//
//    public void setResult(List<String> result) {
//        this.result = result;
//    }
//
//    public Result(int id, List<String> result) {
//        this.id = id;
//        this.result = result;
//    }



    public int[] getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(int[] selectedIds) {
        this.selectedIds = selectedIds;
    }

    public String[] getSelectedNames() {
        return selectedNames;
    }

    public void setSelectedNames(String[] selectedNames) {
        this.selectedNames = selectedNames;
    }

}
