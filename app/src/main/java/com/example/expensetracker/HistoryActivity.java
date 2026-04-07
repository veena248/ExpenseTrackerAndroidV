package com.example.expensetracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listViewTransactions;
    Spinner spinnerFilterType, spinnerSortOrder;
    DatabaseHelper databaseHelper;
    ArrayList<TransactionModel> transactionList;
    TransactionAdapter adapter;

    String currentFilter = "All";
    String currentSort = "Newest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewTransactions = findViewById(R.id.listViewTransactions);
        spinnerFilterType = findViewById(R.id.spinnerFilterType);
        spinnerSortOrder = findViewById(R.id.spinnerSortOrder);

        databaseHelper = new DatabaseHelper(this);

        setupSpinners();
        loadTransactions();

        listViewTransactions.setOnItemClickListener((parent, view, position, id) -> {
            TransactionModel selectedTransaction = transactionList.get(position);

            Intent intent = new Intent(HistoryActivity.this, EditTransactionActivity.class);
            intent.putExtra("transaction_id", selectedTransaction.getId());
            startActivity(intent);
        });

        listViewTransactions.setOnItemLongClickListener((parent, view, position, id) -> {
            TransactionModel selectedTransaction = transactionList.get(position);
            showDeleteDialog(selectedTransaction.getId());
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    private void setupSpinners() {
        String[] filterOptions = {"All", "Income", "Expense"};
        String[] sortOptions = {"Newest", "Oldest"};

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterOptions
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterType.setAdapter(filterAdapter);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sortOptions
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortOrder.setAdapter(sortAdapter);

        spinnerFilterType.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(String value) {
                currentFilter = value;
                loadTransactions();
            }
        });

        spinnerSortOrder.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(String value) {
                currentSort = value;
                loadTransactions();
            }
        });
    }

    private void loadTransactions() {
        transactionList = new ArrayList<>();

        Cursor cursor = databaseHelper.getFilteredTransactions(currentFilter, currentSort);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TYPE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE));

                transactionList.add(new TransactionModel(id, amount, type, category, date, note));

            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter = new TransactionAdapter(this, transactionList);
        listViewTransactions.setAdapter(adapter);
    }

    private void showDeleteDialog(int transactionId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Do you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteTransaction(transactionId);
                    if (deleted) {
                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        loadTransactions();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}