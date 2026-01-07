package com.example.finalprojemm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.finalprojemm.model.Habit;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HabitTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Tablo ve Sütun İsimleri
    private static final String TABLE_HABITS = "habits";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_STREAK = "streak";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabloyu oluşturan SQL sorgusu
        String createTable = "CREATE TABLE " + TABLE_HABITS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_STREAK + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        onCreate(db);
    }

    // Yeni Alışkanlık Ekle
    public void addHabit(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_STREAK, 0); // Başlangıçta 0 gün
        db.insert(TABLE_HABITS, null, values);
        db.close();
    }

    // Tüm Alışkanlıkları Getir
    public List<Habit> getAllHabits() {
        List<Habit> habitList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HABITS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                int streak = cursor.getInt(2);
                habitList.add(new Habit(id, title, streak));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return habitList;
    }

    // Seriyi Güncelle (Artır)
    public void updateStreak(int id, int currentStreak) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STREAK, currentStreak + 1); // 1 artırıyoruz
        db.update(TABLE_HABITS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Alışkanlık Sil
    public void deleteHabit(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HABITS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    // Mevcut kodların altına bu metodu ekle
    public void updateHabitTitle(int id, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, newTitle); // Sadece başlığı güncelle
        db.update(TABLE_HABITS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}