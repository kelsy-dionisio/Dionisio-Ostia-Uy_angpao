package com.mobdeve.s16.uy.kenneth.angpao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NinongAdapter extends RecyclerView.Adapter<NinongViewHolder> {

    Context context;
    List<NinongData> ninongData;

    public NinongAdapter(Context context, List<NinongData> ninongData) {
        this.context = context;
        this.ninongData = ninongData;
    }

    @NonNull
    @Override
    public NinongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NinongViewHolder(LayoutInflater.from(context).inflate(R.layout.ninong_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NinongViewHolder holder, int position) {
        holder.ninongName.setText(ninongData.get(position).getPrefix() + " " + ninongData.get(position).getName());

        String picBase64 = ninongData.get(position).getPic();
        if (picBase64 != null && !picBase64.isEmpty()) {
            Bitmap bitmap = decodeBase64(picBase64);
            holder.nThumbnail.setImageBitmap(bitmap);
        } else {
            holder.nThumbnail.setImageResource(R.drawable.ninongpic_circle);
        }

        String pref = ninongData.get(position).getPrefix();
        String name = ninongData.get(position).getName();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NinongDetailsActivity.class);
                intent.putExtra("NINONG_PREFIX_KEY", pref);
                intent.putExtra("NINONG_NAME_KEY", name);
                intent.putExtra("NINONG_ID_KEY", ninongData.get(position).getId()); // Ensure you have getId() or a method to get the Ninong ID
                context.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return ninongData.size();
    }

    // Method to update NinongData and refresh the adapter
    public void updateNinongData(List<NinongData> newNinongData) {
        this.ninongData.clear();
        this.ninongData.addAll(newNinongData);
        notifyDataSetChanged();
    }

    public void setNinongData(List<NinongData> ninongData) {
        this.ninongData = ninongData;
        notifyDataSetChanged();
    }

    private Bitmap decodeBase64(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
