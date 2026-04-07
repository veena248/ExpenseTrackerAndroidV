package com.example.expensetracker;

public class TransactionModel {
    private final int id;
    private final double amount;
    private final String type;
    private final String category;
    private final String date;
    private final String note;

    public TransactionModel(int id, double amount, String type, String category, String date, String note) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}