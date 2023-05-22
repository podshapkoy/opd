package com.example.Web.models;

import java.util.List;

//Для хранения выбранных прилагательный или в целом для получения списков
public class result {
    private int id;
    private List<String> result;

    public result() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public result(int id, List<String> result) {
        this.id = id;
        this.result = result;
    }

    private int[] selectedIds;
    private String[] selectedNames;

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
