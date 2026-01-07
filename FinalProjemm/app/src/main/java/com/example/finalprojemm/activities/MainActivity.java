package com.example.finalprojemm.activities; // Paket ismini kendi projene göre kontrol et

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojemm.R;
import com.example.finalprojemm.adapter.HabitAdapter;
import com.example.finalprojemm.database.DatabaseHelper;
import com.example.finalprojemm.model.Habit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fabAdd;
    TextView txtTitle; // Başlığı değiştirmek için tanımladık

    HabitAdapter adapter;
    DatabaseHelper databaseHelper;
    List<Habit> habitList;

    // SharedPreferences için anahtar kelimeler
    private static final String PREFS_NAME = "HabitAppPrefs";
    private static final String KEY_USER_NAME = "UserName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bileşenleri Tanımla
        recyclerView = findViewById(R.id.recyclerViewHabits);
        fabAdd = findViewById(R.id.fabAdd);
        txtTitle = findViewById(R.id.pageTitle); // Başlık metnini kodla yakalıyoruz

        // Veritabanı Başlat
        databaseHelper = new DatabaseHelper(this);

        // KULLANICI KONTROLÜ (YENİ EKLENEN KISIM)
        checkUserIdentity();

        // Listeyi Yükle
        loadData();

        // Ekle Butonuna Tıklama Olayı
        fabAdd.setOnClickListener(v -> showAddHabitDialog());
    }

    // --- YENİ EKLENEN METOTLAR ---

    // Kullanıcı adını kontrol eden ve duruma göre işlem yapan fonksiyon
    private void checkUserIdentity() {
        // Not defterini (SharedPreferences) aç
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // "UserName" adında kayıtlı bir şey var mı? Yoksa null döndür.
        String savedName = prefs.getString(KEY_USER_NAME, null);

        if (savedName == null) {
            // Kayıt yoksa, ilk defa giriyordur -> İsim sor
            showNameInputDialog();
        } else {
            // Kayıt varsa -> İsmiyle hitap et
            updateGreeting(savedName);
        }
    }

    // İlk girişte isim soran pencere
    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tanışalım!");
        builder.setMessage("Sana nasıl hitap etmemi istersin?");
        builder.setCancelable(false); // İsim girmeden kapatamasın

        final EditText input = new EditText(this);
        input.setHint("Adın...");
        builder.setView(input);

        builder.setPositiveButton("Başla", (dialog, which) -> {
            String name = input.getText().toString();
            if (!name.isEmpty()) {
                saveUserName(name); // İsmi kaydet
                updateGreeting(name); // Başlığı güncelle
            } else {
                Toast.makeText(MainActivity.this, "Lütfen bir isim gir.", Toast.LENGTH_SHORT).show();
                showNameInputDialog(); // Boş geçerse tekrar sor
            }
        });

        builder.show();
    }

    // İsmi hafızaya kaydeden yardımcı fonksiyon
    private void saveUserName(String name) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_NAME, name);
        editor.apply(); // Kaydet
    }

    // Başlığı güncelleyen fonksiyon
    private void updateGreeting(String name) {
        txtTitle.setText("Hoş geldin, " + name + " 👋");
    }

    // -----------------------------

    private void loadData() {
        habitList = databaseHelper.getAllHabits();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HabitAdapter(this, habitList, databaseHelper);
        recyclerView.setAdapter(adapter);
    }

    private void showAddHabitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yeni Hedef");
        builder.setMessage("Hangi alışkanlığı kazanmak istiyorsun?");

        final EditText input = new EditText(this);
        input.setHint("Örn: Su iç, Kod yaz...");
        builder.setView(input);

        builder.setPositiveButton("Ekle", (dialog, which) -> {
            String habitName = input.getText().toString();
            if (!habitName.isEmpty()) {
                databaseHelper.addHabit(habitName);
                loadData();
            }
        });

        builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}