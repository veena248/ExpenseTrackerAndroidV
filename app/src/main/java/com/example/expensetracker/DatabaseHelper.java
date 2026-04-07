package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_ID = "id";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_TYPE = "type";
    public static final String COL_CATEGORY = "category";
    public static final String COL_DATE = "date";
    public static final String COL_NOTE = "note";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_AMOUNT + " REAL, "
                + COL_TYPE + " TEXT, "
                + COL_CATEGORY + " TEXT, "
                + COL_DATE + " TEXT, "
                + COL_NOTE + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public boolean insertTransaction(double amount, String type, String category, String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_TYPE, type);
        values.put(COL_CATEGORY, category);
        values.put(COL_DATE, date);
        values.put(COL_NOTE, note);

        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        return result != -1;
    }

    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + COL_ID + " DESC", null);
    }

    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TYPE + "=?",
                new String[]{"Income"}
        );

        double total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getTotalExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TYPE + "=?",
                new String[]{"Expense"}
        );

        double total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TRANSACTIONS, COL_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public Cursor getCategorySummary() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COL_CATEGORY + ", SUM(" + COL_AMOUNT + ") AS total " +
                        "FROM " + TABLE_TRANSACTIONS + " " +
                        "WHERE " + COL_TYPE + "=? " +
                        "GROUP BY " + COL_CATEGORY,
                new String[]{"Expense"}
        );
    }

    public double getMonthlyIncome(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COL_TYPE + "=? AND " + COL_DATE + " LIKE ?",
                new String[]{"Income", "%/" + monthYear}
        );

        double total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getMonthlyExpense(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COL_TYPE + "=? AND " + COL_DATE + " LIKE ?",
                new String[]{"Expense", "%/" + monthYear}
        );

        double total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public Cursor getTransactionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public boolean updateTransaction(int id, double amount, String type, String category, String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_TYPE, type);
        values.put(COL_CATEGORY, category);
        values.put(COL_DATE, date);
        values.put(COL_NOTE, note);

        int rows = db.update(TABLE_TRANSACTIONS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public Cursor getRecentTransactions(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + COL_ID + " DESC LIMIT " + limit,
                null
        );
    }

    public Cursor getFilteredTransactions(String typeFilter, String sortOrder) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query;
        String[] args = null;

        String orderBy = sortOrder.equals("Oldest") ? COL_ID + " ASC" : COL_ID + " DESC";

        if (typeFilter.equals("All")) {
            query = "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + orderBy;
        } else {
            query = "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TYPE + "=? ORDER BY " + orderBy;
            args = new String[]{typeFilter};
        }

        return db.rawQuery(query, args);
    }
}