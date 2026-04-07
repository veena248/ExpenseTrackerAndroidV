package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.animation.ValueAnimator;
import android.widget.ListView;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    TextView tvIncome, tvExpense, tvBalance;
    Button btnAdd, btnHistory, btnSummary, btnMonthlyReport;

    TextView tvBudgetAmount, tvBudgetSpent, tvBudgetRemaining;
    ProgressBar progressBudget;
    Button btnSetBudget;
    SharedPreferences sharedPreferences;
    DatabaseHelper databaseHelper;

    ListView listViewRecent;
    ArrayList<TransactionModel> recentList;
    TransactionAdapter recentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationHelper.createNotificationChannel(this);
        requestNotificationPermission();

        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvBalance = findViewById(R.id.tvBalance);

        btnAdd = findViewById(R.id.btnAddTransaction);
        btnHistory = findViewById(R.id.btnViewHistory);
        btnSummary = findViewById(R.id.btnCategorySummary);
        btnMonthlyReport = findViewById(R.id.btnMonthlyReport);

        databaseHelper = new DatabaseHelper(this);

        listViewRecent = findViewById(R.id.listViewRecent);

        tvBudgetAmount = findViewById(R.id.tvBudgetAmount);
        tvBudgetSpent = findViewById(R.id.tvBudgetSpent);
        tvBudgetRemaining = findViewById(R.id.tvBudgetRemaining);
        progressBudget = findViewById(R.id.progressBudget);
        btnSetBudget = findViewById(R.id.btnSetBudget);

        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        btnSetBudget.setOnClickListener(v -> showBudgetDialog());

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        btnSummary.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
            startActivity(intent);
        });

        btnMonthlyReport.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MonthlyReportActivity.class);
            startActivity(intent);
        });


        NotificationHelper.createNotificationChannel(this);
        scheduleDailyReminder();

        requestNotificationPermission();


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSummary();
        loadRecentTransactions();
        loadBudgetProgress();
    }

    private void loadSummary() {
        double income = databaseHelper.getTotalIncome();
        double expense = databaseHelper.getTotalExpense();
        double balance = income - expense;

        animateAmount(tvIncome, income);
        animateAmount(tvExpense, expense);
        animateAmount(tvBalance, balance);
    }

    private void scheduleDailyReminder() {
        Intent intent = new Intent(this, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long interval = 4 * 60 * 60 * 1000; // 4 hours

        long triggerTime = System.currentTimeMillis() + interval; // start after 4 hours

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    interval,
                    pendingIntent
            );
        }
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
    }

    private void animateAmount(TextView textView, double targetAmount) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) targetAmount);
        animator.setDuration(1200);

        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(CurrencyUtils.formatCurrency(value));
        });

        animator.start();
    }

    private void loadRecentTransactions() {
        recentList = new ArrayList<>();

        Cursor cursor = databaseHelper.getRecentTransactions(3);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TYPE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE));

                recentList.add(new TransactionModel(id, amount, type, category, date, note));
            } while (cursor.moveToNext());
        }

        cursor.close();

        recentAdapter = new TransactionAdapter(this, recentList);
        listViewRecent.setAdapter(recentAdapter);
    }

    private void showBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Monthly Budget");

        final EditText input = new EditText(this);
        input.setHint("Enter budget amount");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String budgetText = input.getText().toString().trim();

            if (!budgetText.isEmpty()) {
                try {
                    double budget = Double.parseDouble(budgetText);
                    sharedPreferences.edit().putFloat("monthly_budget", (float) budget).apply();
                    loadBudgetProgress();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("Cancel", null);

        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void loadBudgetProgress() {
        double budget = sharedPreferences.getFloat("monthly_budget", 0f);
        double spent = databaseHelper.getTotalExpense();
        double remaining = budget - spent;

        if (remaining < 0) {
            remaining = 0;
        }

        tvBudgetAmount.setText("Budget: " + CurrencyUtils.formatCurrency(budget));
        tvBudgetSpent.setText("Spent: " + CurrencyUtils.formatCurrency(spent));

        if (budget > 0 && spent > budget) {
            tvBudgetRemaining.setText("Limit exceeded 🚨");
        } else {
            tvBudgetRemaining.setText("Remaining: " + CurrencyUtils.formatCurrency(remaining));
        }

        if (budget > 0) {
            int progress = (int) ((spent / budget) * 100);
            if (progress > 100) progress = 100;
            progressBudget.setProgress(progress);
        } else {
            progressBudget.setProgress(0);
        }
    }

}