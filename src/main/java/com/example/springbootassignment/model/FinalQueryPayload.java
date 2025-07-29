package com.example.springbootassignment.model;

public class FinalQueryPayload {
    private String finalQuery;

    public FinalQueryPayload() {}

    public FinalQueryPayload(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }
}