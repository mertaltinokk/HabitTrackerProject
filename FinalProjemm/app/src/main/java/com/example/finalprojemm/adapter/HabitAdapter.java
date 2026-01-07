package com.example.finalprojemm.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojemm.R;
import com.example.finalprojemm.database.DatabaseHelper;
import com.example.finalprojemm.model.Habit;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private Context context;
    private List<Habit> habitList;
    private DatabaseHelper databaseHelper;

    public HabitAdapter(Context context, List<Habit> habitList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.habitList = habitList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_habit_card, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        holder.txtName.setText(habit.getTitle());
        holder.txtStreak.setText("🔥 " + habit.getStreak() + " Günlük Seri");
        holder.progressBar.setProgress(habit.getStreak());
        holder.progressBar.setMax(30);

        // Tik işaretine basınca (Seriyi Artır)
        holder.imgCheck.setOnClickListener(v -> {
            databaseHelper.updateStreak(habit.getId(), habit.getStreak());
            habit.setStreak(habit.getStreak() + 1);
            notifyItemChanged(holder.getAdapterPosition());
            Toast.makeText(context, "Harika! Zincir devam ediyor.", Toast.LENGTH_SHORT).show();
        });

        // Karta UZUN BASINCA (Silme ve Güncelleme Menüsü)
        holder.itemView.setOnLongClickListener(v -> {
            showOptionsDialog(habit, holder.getAdapterPosition());
            return true;
        });
    }

    // Seçenekler Menüsü (Sil veya Düzenle)
    private void showOptionsDialog(Habit habit, int position) {
        CharSequence[] options = new CharSequence[]{"Düzenle", "Sil", "İptal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("İşlem Seçin");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Düzenle seçildi
                showUpdateDialog(habit, position);
            } else if (which == 1) {
                // Sil seçildi
                deleteItem(habit, position);
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // İsim Güncelleme Penceresi
    private void showUpdateDialog(Habit habit, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alışkanlığı Düzenle");

        final EditText input = new EditText(context);
        input.setText(habit.getTitle()); // Eski ismi içine yaz
        builder.setView(input);

        builder.setPositiveButton("Güncelle", (dialog, which) -> {
            String newTitle = input.getText().toString();
            if (!newTitle.isEmpty()) {
                // Veritabanında güncelle
                databaseHelper.updateHabitTitle(habit.getId(), newTitle);
                // Listede güncelle
                habit.setTitle(newTitle);
                notifyItemChanged(position);
                Toast.makeText(context, "Güncellendi", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Silme İşlemi
    private void deleteItem(Habit habit, int position) {
        databaseHelper.deleteHabit(habit.getId());
        habitList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Silindi", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtStreak;
        ProgressBar progressBar;
        ImageView imgCheck;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtHabitName);
            txtStreak = itemView.findViewById(R.id.txtStreak);
            progressBar = itemView.findViewById(R.id.progressBarHabit);
            imgCheck = itemView.findViewById(R.id.imgCheck);
        }
    }
}