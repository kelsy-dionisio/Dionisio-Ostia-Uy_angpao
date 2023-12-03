package com.mobdeve.s16.uy.kenneth.angpao;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryViewHolder extends RecyclerView.ViewHolder {

    TextView historyNinong, historyAmount, historyDate;
    ImageButton deleteButton, editButton;
    private HistoryAdapter.AdapterOnItemClickListener listener;

    public HistoryViewHolder(@NonNull View itemView, HistoryAdapter.AdapterOnItemClickListener listener) {
        super(itemView);

        this.listener = listener;

        historyNinong = itemView.findViewById(R.id.historyNinong);
        historyAmount = itemView.findViewById(R.id.historyAmount);
        historyDate = itemView.findViewById(R.id.historyDate);
        deleteButton = itemView.findViewById(R.id.deleteHistoryBtn);
        editButton = itemView.findViewById(R.id.editHistoryBtn);

        deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position);
                }
            }
        });

        editButton.setOnClickListener(v -> {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(position);
                }
            }
        });
    }
}

