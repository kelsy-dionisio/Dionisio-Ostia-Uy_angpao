package com.mobdeve.s16.uy.kenneth.angpao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>{


    Context context;
    List<HistoryData> historyData;
    AlertDialog dialog;
    ImageButton deleteButton, editButton;
    private AdapterOnItemClickListener listener;

    public HistoryAdapter(Context context, List<HistoryData> historyData) {
        this.context = context;
        this.historyData = historyData;
    }

    public interface AdapterOnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(AdapterOnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_view, parent, false);
        return new HistoryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.historyNinong.setText(historyData.get(position).getNinongPrefix() + " " + historyData.get(position).getNinongName());
        holder.historyAmount.setText("₱"+historyData.get(position).getAmount());
        holder.historyDate.setText(historyData.get(position).getDate());
        HistoryData selectedHistory = historyData.get(position);

        deleteButton = holder.itemView.findViewById(R.id.deleteHistoryBtn);
        editButton = holder.itemView.findViewById(R.id.editHistoryBtn);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(selectedHistory.getAngpaoId(), holder.getAdapterPosition());
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), AddAngpaoActivity.class);
                intent.putExtra("ANGPAO_ID_KEY", selectedHistory.getAngpaoId());
                intent.putExtra("ANGPAO_NINONG_TYPE_KEY", selectedHistory.getNinongPrefix());
                intent.putExtra("ANGPAO_NINONG_NAME_KEY", selectedHistory.getNinongName());
                intent.putExtra("ANGPAO_AMOUNT_KEY", selectedHistory.getAmount());
                intent.putExtra("ANGPAO_DATE_KEY", selectedHistory.getDate());
                intent.putExtra("ANGPAO_STATUS", "edit");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editButton = itemView.findViewById(R.id.editHistoryBtn);
            deleteButton = itemView.findViewById(R.id.deleteHistoryBtn);
        }
    }

    private void showDeleteConfirmationDialog(int angpaoId, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        TextView message = dialogView.findViewById(R.id.messageText);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

        message.setText("Are you sure you want to delete this item?");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                myDB.deleteAngpao(angpaoId);

                historyData.remove(position);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView(dialogView);
        dialog = builder.create();
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            dialog.show();
        }
    }



    @Override
    public int getItemCount() {
        return historyData.size();
    }



}
