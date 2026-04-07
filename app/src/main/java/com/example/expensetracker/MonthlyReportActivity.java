package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyReportActivity extends AppCompatActivity {

    EditText etSelectMonth;
    Button btnShowReport;
    TextView tvMonthlyIncome, tvMonthlyExpense, tvMonthlyBalance;
    BarChart barChartMonthly;

    DatabaseHelper databaseHelper;
    String selectedMonthYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        etSelectMonth = findViewById(R.id.etSelectMonth);
        btnShowReport = findViewById(R.id.btnShowReport);
        tvMonthlyIncome = findViewById(R.id.tvMonthlyIncome);
        tvMonthlyExpense = findViewById(R.id.tvMonthlyExpense);
        tvMonthlyBalance = findViewById(R.id.tvMonthlyBalance);
        barChartMonthly = findViewById(R.id.barChartMonthly);

        databaseHelper = new DatabaseHelper(this);

        etSelectMonth.setOnClickListener(v -> showMonthPicker());
        btnShowReport.setOnClickListener(v -> showMonthlyReport());
    }

    private void showMonthPicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedMonthYear = String.format(
                            Locale.getDefault(),
                            "%02d/%04d",
                            month + 1,
                            year
                    );
                    etSelectMonth.setText(selectedMonthYear);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showMonthlyReport() {
        if (selectedMonthYear.isEmpty()) {
            etSelectMonth.setError("Select month");
            return;
        }

        double income = databaseHelper.getMonthlyIncome(selectedMonthYear);
        double expense = databaseHelper.getMonthlyExpense(selectedMonthYear);
        double balance = income - expense;

        tvMonthlyIncome.setText("Monthly Income: " + CurrencyUtils.formatCurrency(income));
        tvMonthlyExpense.setText("Monthly Expense: " + CurrencyUtils.formatCurrency(expense));
        tvMonthlyBalance.setText("Monthly Balance: " + CurrencyUtils.formatCurrency(balance));

        showBarChart((float) income, (float) expense);
    }

    private void showBarChart(float income, float expense) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, income));
        entries.add(new BarEntry(1f, expense));

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Report");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);

        barChartMonthly.setData(data);
        barChartMonthly.getDescription().setEnabled(false);
        barChartMonthly.setFitBars(true);
        barChartMonthly.animateY(1000);

        String[] labels = {"Income", "Expense"};
        XAxis xAxis = barChartMonthly.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        barChartMonthly.getAxisRight().setEnabled(false);
        barChartMonthly.invalidate();
    }
}