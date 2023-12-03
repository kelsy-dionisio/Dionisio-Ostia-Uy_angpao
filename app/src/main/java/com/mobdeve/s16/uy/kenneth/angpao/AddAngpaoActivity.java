package com.mobdeve.s16.uy.kenneth.angpao;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;

public class AddAngpaoActivity extends AppCompatActivity {

    private static final String TAG = "AddAngpaoActivity";

    EditText ninongInput, pesoInput, dateInput;
    Button cancelBtn;
    Button recordBtn;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<NinongData> adapter;
    private TextView mDisplayDate, recordTitle;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private TextView textView;
    private ImageButton button;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_angpao);

        MyDatabaseHelper myDB = new MyDatabaseHelper(AddAngpaoActivity.this);

        ninongInput = findViewById(R.id.ninongInput);
        pesoInput = findViewById(R.id.pesoInput);
        dateInput = findViewById(R.id.dateInput);
        recordTitle = findViewById(R.id.recordTitle);

        // Retrieve ninong names from the database
        List<NinongData> ninongsList = myDB.getAllNinongs();

        // Create an ArrayAdapter using the ninongsList
        autoCompleteTextView = findViewById(R.id.ninongInput);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ninongsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NinongData selectedNinong = adapter.getItem(i);
            }
        });

        Intent intent = getIntent();
        String status = intent.getStringExtra("ANGPAO_STATUS");
        recordTitle.setText(status);
        int angpaoId = intent.getIntExtra("ANGPAO_ID_KEY", -1);
        Log.d("amgpao id", String.valueOf(angpaoId));
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(AddAngpaoActivity.this);

        if (angpaoId != -1) {
            HistoryData existingAngpao = dbHelper.getAngpaoById(angpaoId);
            if (existingAngpao != null) {
                // Pre-fill the fields
                ninongInput.setText(existingAngpao.getNinongPrefix() + " " + existingAngpao.getNinongName());
                pesoInput.setText(existingAngpao.getAmount());
                dateInput.setText(existingAngpao.getDate());
                autoCompleteTextView = findViewById(R.id.ninongInput);
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ninongsList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoCompleteTextView.setAdapter(adapter);
            }
        }

        //record btn
        this.recordBtn = findViewById(R.id.recordBtn);
        this.recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected Ninong name from the AutoCompleteTextView
                String selectedNinongName = autoCompleteTextView.getText().toString();

                // Check if a Ninong is selected
                if (!selectedNinongName.isEmpty()) {
                    MyDatabaseHelper myDB = new MyDatabaseHelper(AddAngpaoActivity.this);
                    List<NinongData> allNinongs = myDB.getAllNinongs();

                    // Find the selected Ninong by name
                    NinongData selectedNinong = null;
                    for (NinongData ninong : allNinongs) {
                        // Split the selectedNinongName by space and take the second part
                        String[] parts = selectedNinongName.split(" ");
                        String nameWithoutPrefix = parts.length > 1 ? parts[1] : selectedNinongName;

                        if (ninong.getName().equals(nameWithoutPrefix)) {
                            selectedNinong = ninong;
                            break;
                        }
                    }

                    if (selectedNinong != null) {
                        Intent intent = getIntent();
                        if (angpaoId != -1) {
                            // Editing existing Angpao entry
                            myDB.updateAngpao(
                                    angpaoId,
                                    selectedNinong.getId(),
                                    Integer.parseInt(pesoInput.getText().toString().trim()),
                                    dateInput.getText().toString().trim()
                            );
                        } else {
                            // Adding new Angpao entry
                            myDB.insertAngpao(
                                    selectedNinong.getId(),
                                    Integer.parseInt(pesoInput.getText().toString().trim()),
                                    dateInput.getText().toString().trim()
                            );
                        }
                        finish();
                    } else {
                        Toast.makeText(AddAngpaoActivity.this, "Selected Ninong not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddAngpaoActivity.this, "Please select a Ninong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //date input
        button = findViewById(R.id.dateBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(); // Open date picker dialog
            }
        });

        //cancel btn
        this.cancelBtn = findViewById(R.id.cancelBtn);
        this.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

        private void openDatePicker(){
            Calendar calendar = Calendar.getInstance();

            // Get the current year
            int currentYear = calendar.get(Calendar.YEAR);

            // Get the current month (Note: Month value is 0-based, i.e., January is 0)
            int currentMonth = calendar.get(Calendar.MONTH);

            // Get the current day of the month
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    //Showing the picked value in the textView
                    dateInput.setText(String.valueOf(year)+ "/"+String.valueOf(month+1)+ "/"+String.valueOf(day));

                }
            }, currentYear, currentMonth, currentDay);

            datePickerDialog.show();
        }
}