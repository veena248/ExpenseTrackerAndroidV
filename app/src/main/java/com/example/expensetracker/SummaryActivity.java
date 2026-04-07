package com.example.expensetracker;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    ListView listViewSummary;
    PieChart pieChartCategory;
    DatabaseHelper databaseHelper;
    ArrayList<String> summaryList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        listViewSummary = findViewById(R.id.listViewSummary);
        pieChartCategory = findViewById(R.id.pieChartCategory);

        databaseHelper = new DatabaseHelper(this);

        loadCategorySummary();
    }

    private void loadCategorySummary() {
        summaryList = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        Cursor cursor = databaseHelper.getCategorySummary();

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                double total = cursor.getDouble(1);

                summaryList.add(getCategoryEmoji(category) + category + " : " + CurrencyUtils.formatCurrency(total));
                pieEntries.add(new PieEntry((float) total, category));

            } while (cursor.moveToNext());
        } else {
            summaryList.add("No expense data available");
        }

        cursor.close();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                summaryList
        );
        listViewSummary.setAdapter(adapter);

        showPieChart(pieEntries);
    }

    private void showPieChart(ArrayList<PieEntry> pieEntries) {
        if (pieEntries.isEmpty()) {
            pieChartCategory.clear();
            pieChartCategory.setNoDataText("No chart data available");
            return;
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Expense Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);

        pieChartCategory.setData(data);
        pieChartCategory.setUsePercentValues(false);
        pieChartCategory.setEntryLabelTextSize(12f);
        pieChartCategory.setCenterText("Expenses");
        pieChartCategory.setCenterTextSize(16f);
        pieChartCategory.getDescription().setEnabled(false);
        pieChartCategory.animateY(1000);
        pieChartCategory.invalidate();
    }

    private String getCategoryEmoji(String category) {
        switch (category.toLowerCase()) {
            case "food":
                return "🍔 ";
            case "travel":
                return "🚌 ";
            case "bills":
                return "💡 ";
            case "shopping":
                return "🛍️ ";
            case "health":
                return "💊 ";
            case "salary":
                return "💰 ";
            default:
                return "📁 ";
        }
    }
}