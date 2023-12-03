package com.mobdeve.s16.uy.kenneth.angpao;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NinongViewHolder extends RecyclerView.ViewHolder {

    TextView ninongName;
    ImageView nThumbnail;
    public NinongViewHolder(@NonNull View itemView) {
        super(itemView);
        ninongName = itemView.findViewById(R.id.ninongName);
        nThumbnail = itemView.findViewById(R.id.nThumbnail);
    }
}
