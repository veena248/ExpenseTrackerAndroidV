package com.example.expensetracker;

import android.view.View;
import android.widget.AdapterView;

public abstract class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value = parent.getItemAtPosition(position).toString();
        onItemSelected(value);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public abstract void onItemSelected(String value);
}