package com.mobdeve.s16.uy.kenneth.angpao;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;

public class AddNinongActivity extends AppCompatActivity {
    String ninongPrefix, nName, nAdd, nImage;
    Uri uri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private String base64Image;
    EditText ninongName, ninongAdd;
    MyDatabaseHelper dbHelper;
    TextView textAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_add_ninong);

        Button addButton = findViewById(R.id.add);
        Button cancelButton = findViewById(R.id.cancel);
        ninongName = findViewById(R.id.nameInput);
        ninongAdd = findViewById(R.id.descInput);
        RadioGroup prefixGroup = findViewById(R.id.prefixGroup);

        textAdd = findViewById(R.id.textAdd);
        String status = intent.getStringExtra("NINONG_STATUS");
        textAdd.setText(status);

        imageView = findViewById(R.id.nImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddNinongActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        //Button homeButton = findViewById(R.id.home);

        dbHelper = new MyDatabaseHelper(this);

        prefixGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                if (radioButton != null) {
                    ninongPrefix = radioButton.getText().toString();
                }
            }
        });

        int ninongId = intent.getIntExtra("NINONG_ID_KEY", -1);


        if (ninongId != -1) {
            NinongData existingNinong = dbHelper.getNinongById(ninongId);

            if (existingNinong != null) {

                String base64Image = existingNinong.getPic();
                Bitmap bitmap = convertBase64ToBitmap(base64Image);
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.ninongpic_circle);
                }


                ninongName.setText(existingNinong.getName());
                ninongAdd.setText(existingNinong.getAddInfo());

                RadioButton radioNinong = findViewById(R.id.radioNinong);
                RadioButton radioNinang = findViewById(R.id.radioNinang);

                if (existingNinong.getPrefix().equals("Ninong")) {
                    radioNinong.setChecked(true);
                } else if (existingNinong.getPrefix().equals("Ninang")) {
                    radioNinang.setChecked(true);
                }
            }
        }

        /*homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the home activity
                Intent intent = new Intent(AddNinongActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish this activity if you want to go back to the home activity
            }
        });*/

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nName = ninongName.getText().toString();
                nAdd = ninongAdd.getText().toString();
                String newBase64Image = uri != null ? convertImageToBase64(uri) : null;

                if (uri != null) {
                    base64Image = convertImageToBase64(uri);
                }

                if (!nName.isEmpty()) {
                    if (ninongId != -1) {
                        NinongData existingNinong = dbHelper.getNinongById(ninongId);

                        if (uri == null) {
                            newBase64Image = existingNinong.getPic();
                        }

                        long updateResult = dbHelper.updateNinong(ninongId, newBase64Image, ninongPrefix, nName, nAdd);

                        if (updateResult > 0) {
                            Toast.makeText(AddNinongActivity.this, "Ninong updated", Toast.LENGTH_SHORT).show();

                            // Update the existing NinongDetailsActivity with the edited information
                            Intent updateIntent = new Intent();
                            updateIntent.putExtra("NINONG_PREFIX_KEY", ninongPrefix);
                            updateIntent.putExtra("NINONG_NAME_KEY", nName);
                            updateIntent.putExtra("NINONG_DESC_KEY", nAdd);
                            updateIntent.putExtra("ANGPAO_STATUS", "add");
                            setResult(RESULT_OK, updateIntent);
                        } else {
                            // Update failed
                            Toast.makeText(AddNinongActivity.this, "Failed to update Ninong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Insert new Ninong data into the database
                        long result = dbHelper.insertNinong(ninongPrefix, base64Image, nName, nAdd);

                        if (result != -1) {
                            // Insert successful
                            int newNinongId = dbHelper.getNinongIdByName(nName); // Assuming you have a method to get the ID by name
                            Intent intent = new Intent(AddNinongActivity.this, NinongDetailsActivity.class);
                            intent.putExtra("NINONG_ID_KEY", newNinongId);
                            intent.putExtra("NINONG_PREFIX_KEY", ninongPrefix);
                            intent.putExtra("NINONG_NAME_KEY", nName);
                            intent.putExtra("NINONG_DESC_KEY", nAdd);
                            setResult(RESULT_OK, intent);
                            startActivity(intent);
                            finish();// Finish this activity if you want to go back to the list
                        } else {
                            // Insert failed
                            Toast.makeText(AddNinongActivity.this, "Failed to add Ninong", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Show a message that name is required
                    Toast.makeText(AddNinongActivity.this, "Ninong name is required", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        imageView.setImageURI(uri);
    }

    private String convertImageToBase64(Uri imageUri) {
        if (imageUri == null) {
            return null;
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
