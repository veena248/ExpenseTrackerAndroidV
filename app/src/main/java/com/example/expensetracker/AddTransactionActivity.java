package com.example.expensetracker;

import android.app.DatePickerDialog;
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

public class AddTransactionActivity extends AppCompatActivity {

    EditText etAmount, etDate, etNote;
    Spinner spinnerCategory;
    RadioGroup radioGroupType;
    Button btnSave;

    DatabaseHelper databaseHelper;

    String[] categories = {"Food", "Travel", "Bills", "Shopping", "Health", "Salary", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        radioGroupType = findViewById(R.id.radioGroupType);
        btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveTransaction());
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

    private void saveTransaction() {
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

        boolean inserted = databaseHelper.insertTransaction(amount, type, category, date, note);

        if (inserted) {
            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }
}