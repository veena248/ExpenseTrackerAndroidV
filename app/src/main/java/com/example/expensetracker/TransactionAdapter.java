package com.example.expensetracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<TransactionModel> {

    public TransactionAdapter(Context context, ArrayList<TransactionModel> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, parent, false);
        }

        TransactionModel item = getItem(position);

        TextView tvCategory = convertView.findViewById(R.id.tvItemCategory);
        TextView tvAmount = convertView.findViewById(R.id.tvItemAmount);
        TextView tvType = convertView.findViewById(R.id.tvItemType);
        TextView tvDate = convertView.findViewById(R.id.tvItemDate);
        TextView tvNote = convertView.findViewById(R.id.tvItemNote);

        if (item != null) {
            tvCategory.setText(getCategoryEmoji(item.getCategory()) + item.getCategory());
            tvAmount.setText(CurrencyUtils.formatCurrency(item.getAmount()));
            tvType.setText(item.getType());
            tvDate.setText(item.getDate());
            tvNote.setText(item.getNote() == null || item.getNote().trim().isEmpty() ? "No note added" : item.getNote());

            if (item.getType().equalsIgnoreCase("Income")) {
                tvAmount.setTextColor(Color.parseColor("#24C38B"));
            } else {
                tvAmount.setTextColor(Color.parseColor("#FF5C7A"));
            }
        }

        return convertView;
    }

    private String getCategoryEmoji(String category) {
        if (category == null) return "📁 ";
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
            case "other":
                return "📁 ";
            default:
                return "📌 ";
        }
    }
}