package com.example.finalprojemm.model;

public class Habit {
    private int id;
    private String title;
    private int streak; // Kaç gün üst üste yapıldı

    public Habit(int id, String title, int streak) {
        this.id = id;
        this.title = title;
        this.streak = streak;
    }

    // Getter ve Setter Metotları
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
}