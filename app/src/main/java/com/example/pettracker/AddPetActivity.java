package com.example.pettracker;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.UUID;

public class AddPetActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private EditText nameInput;
    private EditText breederInput;
    private LinearLayout dateLayout;
    private TextInputLayout datetextLay;

    private EditText raceInput;
    private EditText insuranceInput;
    private EditText insuranceNRinput;



    private EditText otherInput;
    private ImageView petImage;
    private ImageView imageDeleteBtn;
    private LinearLayout addImageBtn;

    private Button previewPetBtn;
    private Button addPetBtn;

    private String name;
    private String breederName;
    private int bDay;
    private int bMonth;
    private int bYear;
    private String race;
    private String insurance;
    private String insuranceNR;
    private String other;

    private static final int PICK_IMAGE_REQUEST =1;
    private Uri imageUri;
    private String uniqueId;

    private EditText dateInput;
    private DatePickerDialog picker;

    private FirebaseFirestore db;
    private CollectionReference petsRef;

    private FirebaseAuth mAuth;
    private String user;

    private StorageReference fileReference;
    private StorageReference imageStorageRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("Nytt Husdjur");

        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

        petsRef = FirebaseFirestore.getInstance().collection("users").document(user).collection("pets");
        imageStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = (ProgressBar) findViewById(R.id.progressbarAR);

        addImageBtn = (LinearLayout) findViewById(R.id.addImageLayout);
        addImageBtn.setOnClickListener(this);
        petImage = (ImageView) findViewById(R.id.imageLayout);
        imageDeleteBtn = (ImageView) findViewById(R.id.imageDeleteButton);
        imageDeleteBtn.setOnClickListener(this);

        nameInput = (EditText) findViewById(R.id.petNameID);
        breederInput = (EditText) findViewById(R.id.breederNameID);
        dateInput = (EditText) findViewById(R.id.birthdateID);
        dateLayout = (LinearLayout) findViewById(R.id.birthdateLayoutID);
        datetextLay = (TextInputLayout) findViewById(R.id.birthdateTILID);

        raceInput = (EditText) findViewById(R.id.raceID);
        insuranceInput = (EditText) findViewById(R.id.insuranceInputID);
        insuranceNRinput = (EditText) findViewById(R.id.insuranceInputID);
        otherInput = (EditText) findViewById(R.id.otherInputID);

        previewPetBtn = (Button) findViewById(R.id.petPreviewID);
        previewPetBtn.setOnClickListener(this);

        addPetBtn = (Button) findViewById(R.id.petCreateID);
        addPetBtn.setOnClickListener(this);

        dateInput.setInputType(InputType.TYPE_NULL);
        dateLayout.setOnClickListener(this);
        dateInput.setOnClickListener(this);
        datetextLay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        name = nameInput.getText().toString().trim();
        breederName = breederInput.getText().toString().trim();
        race = raceInput.getText().toString().trim();
        insurance = insuranceInput.getText().toString().trim();
        insuranceNR = insuranceNRinput.getText().toString().trim();
        other = otherInput.getText().toString().trim();


        if(v.getId() == R.id.birthdateID || v.getId() == R.id.birthdateLayoutID || v.getId() == R.id.birthdateTILID){
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);


                picker = new DatePickerDialog(AddPetActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        dateInput.setText(dayOfMonth + "/" + month +"/" + year);
                        bDay = dayOfMonth;
                        bMonth = month;
                        bYear = year;
                    }
                }, year, month, day);
                picker.getDatePicker().setSpinnersShown(true);
                picker.getDatePicker().setCalendarViewShown(false);
                picker.show();
        }

        switch (v.getId()){


            case R.id.imageDeleteButton:
                imageUri = null;
                imageDeleteBtn.setVisibility(View.GONE);
                petImage.setVisibility(View.GONE);
                break;

            case R.id.addImageLayout:
                openFileChooser();
                break;

            case R.id.petCreateID:
                if(name == null){
                    nameInput.setError("Namn behöver fyllas i");
                    nameInput.requestFocus();
                    return;
                }
                uploadNewPet();
                break;

            case R.id.petPreviewID:
                break;
        }
    }

    private void checkInput(){
        if(breederName ==null){
            breederName = "";
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).resize(250, 250).onlyScaleDown().centerInside().into(petImage);
            if(petImage.getVisibility() == View.GONE){
                petImage.setVisibility(View.VISIBLE);
                imageDeleteBtn.setVisibility(View.VISIBLE);

            }
        }
    }

    private void uploadNewPet(){

        if(imageUri != null){
            uniqueId = UUID.randomUUID().toString();
            fileReference = imageStorageRef.child(uniqueId + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("!!!", "2");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Bild kunde inte laddas upp", Toast.LENGTH_SHORT).show();
                    //return;
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }

        Log.d("!!!", "3");

        String image = "";
        if(imageUri != null){
            image = uniqueId + "." + getFileExtension(imageUri);
        }
        Pets pet = new Pets(name, breederName, bDay, bMonth, bYear, race, insurance, insuranceNR,other, image, "");
        //Pets pet = new Pets(name, "", 1, 10, 2000, "", "", "","", image, "");
        petsRef.add(pet).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Recept kunde inte laddas upp", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
