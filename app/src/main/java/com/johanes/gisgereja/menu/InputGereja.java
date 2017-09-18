package com.johanes.gisgereja.menu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;

import com.johanes.gisgereja.R;
import com.johanes.gisgereja.helper.DatabaseHelperGereja;
import com.johanes.gisgereja.helper.InputValidation;
import com.johanes.gisgereja.utils.Gereja;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class InputGereja extends AppCompatActivity implements View.OnClickListener {
    final int REQUEST_CODE_GALLERY = 999;

    private final AppCompatActivity activity = InputGereja.this;

    private NestedScrollView nestedScrollView;
    private TextInputLayout textInputNamaGereja;
    private TextInputLayout textInputAlamatGereja;
    private TextInputLayout textInputDeskripsiGereja;
    private TextInputLayout textInputLayoutLongi;
    private TextInputLayout textInputLayoutLati;

    private TextInputEditText textInputEditTextNamaGereja;
    private TextInputEditText textInputEditTextAlamatGereja;
    private TextInputEditText textInputEditTextDescGereja;
    private TextInputEditText textInputEditTextLong;
    private TextInputEditText textInputEditTextLati;


    private ImageView imageView;

    private AppCompatButton buttonAdd;
    private AppCompatButton buttonAddPic;

    private AppCompatTextView textViewPicName;

    private InputValidation inputValidation;
    private DatabaseHelperGereja dbHelper;
    private Gereja gereja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_gereja);
        initViews();
        initListeners();
        initObject();

        dbHelper = new DatabaseHelperGereja(this, "GerejaData.sqlite", null, 1);

    }

    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputNamaGereja = (TextInputLayout) findViewById(R.id.textInputLayoutNamaGereja);
        textInputAlamatGereja = (TextInputLayout) findViewById(R.id.textInputLayoutAlamatGereja);
        textInputDeskripsiGereja = (TextInputLayout) findViewById(R.id.textInputLayoutDescGereja);

        textInputEditTextNamaGereja = (TextInputEditText) findViewById(R.id.textInputEditTextNamaGereja);
        textInputEditTextAlamatGereja = (TextInputEditText) findViewById(R.id.textInputEditTextAlamatGereja);
        textInputEditTextDescGereja = (TextInputEditText) findViewById(R.id.textInputEditTextDescGereja);

        textInputEditTextLati = (TextInputEditText) findViewById(R.id.textInputEditTextLati);
        textInputEditTextLong = (TextInputEditText) findViewById(R.id.textInputEditTextLong);

//        latiVar = Double.parseDouble(textInputEditTextLati.getText().toString().trim());
//        longiVar = Double.parseDouble(textInputEditTextLong.getText().toString().trim());

        buttonAdd = (AppCompatButton) findViewById(R.id.appCompatButtonAddGereja);
        buttonAddPic = (AppCompatButton) findViewById(R.id.buttonUploadImage);
//        textViewPicName = (AppCompatTextView) findViewById(R.id.textViewPicAddress);

        imageView = (ImageView) findViewById(R.id.uploadedImage);
    }

    private void initListeners() {
        buttonAdd.setOnClickListener(this);
        buttonAddPic.setOnClickListener(this);
    }

    private void initObject() {
//        dbHelper = new DatabaseHelperGereja(activity);
        inputValidation = new InputValidation(activity);
//        gereja = new Gereja();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUploadImage:
                chooseImage();
                break;

            case R.id.appCompatButtonAddGereja:
                saveToSql();
                break;


        }

    }

    private void saveToSql() {
        try{
            dbHelper.insertData(
                    textInputEditTextNamaGereja.getText().toString().trim(),
                    textInputEditTextAlamatGereja.getText().toString().trim(),
                    textInputEditTextDescGereja.getText().toString().trim(),
                    Double.parseDouble(textInputEditTextLati.getText().toString().trim()),
                    Double.parseDouble(textInputEditTextLong.getText().toString().trim()),
                    imageViewToByte(imageView)
            );
            Snackbar.make(nestedScrollView, getString(R.string.add_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();
            imageView.setImageResource(R.drawable.church);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    //===========================
    private void emptyInputEditText() {
        textInputEditTextNamaGereja.setText(null);
        textInputEditTextAlamatGereja.setText(null);
        textInputEditTextDescGereja.setText(null);
    }


    private void chooseImage() {
        ActivityCompat.requestPermissions(
                InputGereja.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
        );
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Snackbar.make(nestedScrollView, getString(R.string.error_user_access), Snackbar.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
