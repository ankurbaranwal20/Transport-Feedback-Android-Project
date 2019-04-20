package com.example.ankurbaranwal.feedback;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ankurbaranwal.feedback.Model.People;
import com.example.ankurbaranwal.feedback.Model.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private ImageView imageView;
    private ImageView locatImage;
    private EditText subjectEdit, localityEdit, feedEdit;
    private Button submitButton;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String sub, location, description, saveCurrentDate, saveCurrentTime;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageUrl;
    private DatabaseReference ProductRef;
    private StorageReference storageReference;

    AdView adView;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(MainActivity.this,"ca-app-pub-9044775629101422~7585002201");
        adView =(AdView)findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        storageReference = FirebaseStorage.getInstance().getReference().child("People");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("People");

        loadingBar = new ProgressDialog(this);


        locatImage = (ImageView) findViewById(R.id.locate);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, 1);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);


        subjectEdit = (EditText) findViewById(R.id.subject);
        localityEdit = (EditText) findViewById(R.id.location);
        feedEdit = (EditText) findViewById(R.id.feedback);
        submitButton = (Button) findViewById(R.id.submit);
        imageView = (ImageView) findViewById(R.id.select_product_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFeedbackData();
            }
        });

        locatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchLocation();
            }
        });


    }

    public void FetchLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    localityEdit.setText(location.toString());
                }
            }
        });

    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null)
        {
            filePath = data.getData();
            imageView.setImageURI(filePath);
        }
    }

    private void ValidateFeedbackData()
    {
        sub = subjectEdit.getText().toString();
        location = localityEdit.getText().toString();
        description = feedEdit.getText().toString();

        if (filePath == null)
        {
            Toast.makeText(this, "Image is mandatory...", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(sub))
        {
            Toast.makeText(this,"Subject is compulsory.",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(location))
        {
            Toast.makeText(this,"Without Location we can not do anything.",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(description))
        {
            Toast.makeText(this,"Tell us your suggestion",Toast.LENGTH_SHORT).show();

        }
        else
        {
            StoreFeedackInformation();
        }
    }

    private void StoreFeedackInformation()
    {
        loadingBar.setTitle("Feedback");
        loadingBar.setMessage("Please wait, while we are submiting your review.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss &");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference file = storageReference.child(filePath.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = file.putFile(filePath);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(MainActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "Image Uploaded...", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = file.getDownloadUrl().toString();
                        return file.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }
    private void SaveProductInfoToDatabase()
    {
        People people = new People(productRandomKey,sub,location,description,downloadImageUrl);

        HashMap<String, Object> product = new HashMap<>();
        product.put("pid",people.getId());
        product.put("date", saveCurrentDate);
        product.put("time", saveCurrentTime);
        product.put("image", people.getImage());
        product.put("Subject",people.getSubject());
        product.put("Location",people.getLocality());
        product.put("Description",people.getFeedback());

        ProductRef.child(productRandomKey).updateChildren(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(MainActivity.this,MainActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Added Succesfully..", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(MainActivity.this, "Error :"+ message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure want to leave?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}


