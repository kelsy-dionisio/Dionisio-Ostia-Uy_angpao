package com.mobdeve.s16.uy.kenneth.angpao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NinongDetailsActivity extends AppCompatActivity {

    ImageButton backBtn, editNinongBtn, deleteNinongBtn;
    TextView ninongText, nameText, infoText, infoText2;
    ImageView ninongImage2;

    AlertDialog dialog;
    MyDatabaseHelper dbHelper;
    RecyclerView recyclerView;
    int ninongId;
    ArrayList<HistoryData> history;

    String ninongPrefix, ninongName, additionalInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninong_details);
        dbHelper = new MyDatabaseHelper(this);
        Intent intent = getIntent();
        ninongId = intent.getIntExtra("NINONG_ID_KEY", -1);

        // Add these lines to retrieve Ninong ID if it exists

        ninongText = findViewById(R.id.ninongText2);
        nameText = findViewById(R.id.nameText2);
        infoText2 = findViewById(R.id.infoText2);
        infoText = findViewById(R.id.infoText2);
        ninongImage2 = findViewById(R.id.ninongImage2);

        additionalInfo = intent.getStringExtra("NINONG_DESC_KEY");

        infoText.setText(ninongPrefix + " " + ninongName + " " + additionalInfo);

        if (ninongId != -1) {
            NinongData ninongData = dbHelper.getNinongById(ninongId);

            ninongPrefix = ninongData.getPrefix();
            ninongName = ninongData.getName();
            additionalInfo = ninongData.getAddInfo();

            ninongText.setText(""+ninongPrefix);
            nameText.setText(""+ninongName);
            infoText2.setText(additionalInfo);

            String base64Image = ninongData.getPic();
            Bitmap bitmap = convertBase64ToBitmap(base64Image);
            if(bitmap != null){
                ninongImage2.setImageBitmap(bitmap);
            } else {
                ninongImage2.setImageResource(R.drawable.ninongpic_circle);
            }
        } else {
            infoText2.setText("");
        }

        editNinongBtn = findViewById(R.id.editNinongBtn);
        deleteNinongBtn = findViewById(R.id.deleteNinongBtn);
        recyclerView  = findViewById(R.id.recyclerView1);

        history = (ArrayList<HistoryData>) dbHelper.getAngpaosByNinongId(ninongId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter historyAdapter = new HistoryAdapter(getApplicationContext(), history);

        recyclerView.setAdapter(historyAdapter);

        this.backBtn = findViewById(R.id.backBtn);
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteNinongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialogNinong(ninongId);
            }
        });

        editNinongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NinongDetailsActivity.this, AddNinongActivity.class);

                // Pass the Ninong ID to the AddNinongActivity
                intent.putExtra("NINONG_ID_KEY", ninongId);

                // Pass the existing Ninong data to the AddNinongActivity
                intent.putExtra("NINONG_PREFIX_KEY", ninongText.getText().toString());
                intent.putExtra("NINONG_NAME_KEY", nameText.getText().toString());
                intent.putExtra("NINONG_STATUS", "edit");

                startActivity(intent);
            }
        });
    }

    private void showDeleteConfirmationDialogNinong(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        TextView message = dialogView.findViewById(R.id.messageText);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

        message.setText("Are you sure you want to delete this item?");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call deleteNinong method here
                int result = dbHelper.deleteNinong(ninongId); // Assuming you have ninongId available
                if (result > 0) {
                    Toast.makeText(NinongDetailsActivity.this, "Ninong deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after deletion
                } else {
                    Toast.makeText(NinongDetailsActivity.this, "Failed to delete Ninong", Toast.LENGTH_SHORT).show();
                }
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
        dialog.show();

        Log.d("NinongDetailsActivity", "Ninong ID to be deleted: " + ninongId);
    }


    protected void onResume() {
        super.onResume();
        // Reload data when the activity resumes
        if (ninongId != -1) {
            NinongData ninongData = dbHelper.getNinongById(ninongId);

            ninongPrefix = ninongData.getPrefix();
            ninongName = ninongData.getName();
            additionalInfo = ninongData.getAddInfo();

            ninongText.setText(""+ninongPrefix);
            nameText.setText(""+ninongName);
            infoText2.setText(additionalInfo);
            String base64Image = ninongData.getPic();
            Bitmap bitmap = convertBase64ToBitmap(base64Image);
            if(bitmap != null){
                ninongImage2.setImageBitmap(bitmap);
            } else {
                ninongImage2.setImageResource(R.drawable.ninongpic_circle);
            }
        } else {
            infoText2.setText("");
        }

        history = (ArrayList<HistoryData>) dbHelper.getAngpaosByNinongId(ninongId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new HistoryAdapter(getApplicationContext(), history));
    }

    private Bitmap convertBase64ToBitmap(String base64Image) {
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}