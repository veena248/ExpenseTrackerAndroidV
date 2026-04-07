package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity {

    EditText etAmount, etDate, etNote;
    Spinner spinnerCategory;
    RadioGroup radioGroupType;
    RadioButton rbIncome, rbExpense;
    Button btnUpdate;

    DatabaseHelper databaseHelper;
    int transactionId = -1;

    String[] categories = {"Food", "Travel", "Bills", "Shopping", "Health", "Salary", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        radioGroupType = findViewById(R.id.radioGroupType);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        btnUpdate = findViewById(R.id.btnUpdate);

        databaseHelper = new DatabaseHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> showDatePicker());

        if (getIntent() != null) {
            transactionId = getIntent().getIntExtra("transaction_id", -1);
        }

        if (transactionId != -1) {
            loadTransactionData(transactionId);
        }

        btnUpdate.setOnClickListener(v -> updateTransaction());
    }

    private void loadTransactionData(int id) {
        Cursor cursor = databaseHelper.getTransactionById(id);

        if (cursor.moveToFirst()) {
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TYPE));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE));

            etAmount.setText(String.valueOf(amount));
            etDate.setText(date);
            etNote.setText(note);

            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(category)) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }

            if (type.equalsIgnoreCase("Income")) {
                rbIncome.setChecked(true);
            } else {
                rbExpense.setChecked(true);
            }
        }

        cursor.close();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    etDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateTransaction() {
        String amountText = etAmount.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        int selectedTypeId = radioGroupType.getCheckedRadioButtonId();

        if (amountText.isEmpty()) {
            etAmount.setError("Enter amount");
            return;
        }

        if (selectedTypeId == -1) {
            Toast.makeText(this, "Select Income or Expense", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            etDate.setError("Select date");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedTypeId);
        String type = selectedRadioButton.getText().toString();

        boolean updated = databaseHelper.updateTransaction(transactionId, amount, type, category, date, note);

        if (updated) {
            Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}