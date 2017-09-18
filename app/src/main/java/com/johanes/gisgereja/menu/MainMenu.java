package com.johanes.gisgereja.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.johanes.gisgereja.Login;
import com.johanes.gisgereja.R;
import com.johanes.gisgereja.helper.DatabaseHelper;
import com.johanes.gisgereja.utils.SessionManager;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = MainMenu.this;

    private AppCompatButton appCompatButtonInput;
    private AppCompatButton appCompatButtonMaps;
    private AppCompatButton appCompatButtonInfo;
    private AppCompatButton appCompatButtonList;
    private AppCompatButton appCompatButtonExit;

    private AppCompatTextView textViewName;

    private DatabaseHelper databaseHelper;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn())
        {
            logoutUser();
        }

        initViews();
        initListeners();
        initObjects();
    }


    private void initObjects() {
//        databaseHelper = new DatabaseHelper(activity);
        String emailFromIntent = getIntent().getStringExtra("NAMA");
        textViewName.setText(emailFromIntent);
    }

    private void initViews() {
        appCompatButtonInput = (AppCompatButton) findViewById(R.id.btn_InputData);
        appCompatButtonMaps = (AppCompatButton) findViewById(R.id.btn_Maps);
        appCompatButtonInfo = (AppCompatButton) findViewById(R.id.btn_Info);
        appCompatButtonList = (AppCompatButton) findViewById(R.id.btn_ListG);
        appCompatButtonExit = (AppCompatButton) findViewById(R.id.btn_Exit);
        textViewName = (AppCompatTextView) findViewById(R.id.textViewName);
    }

    private void initListeners() {
        appCompatButtonInput.setOnClickListener(this);
        appCompatButtonMaps.setOnClickListener(this);
        appCompatButtonInfo.setOnClickListener(this);
        appCompatButtonList.setOnClickListener(this);
        appCompatButtonExit.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_InputData:
                Intent id = new Intent(getApplicationContext(), InputGereja.class);
                startActivity(id);
                break;
            case R.id.btn_Maps:
                Intent maps = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(maps);
                break;
            case R.id.btn_ListG:
                Intent list = new Intent(getApplicationContext(), ListGereja.class);
                startActivity(list);
                break;
            case R.id.btn_Info:
                Intent info = new Intent(getApplicationContext(), Info.class);
                startActivity(info);
                break;
            case R.id.btn_Exit:
                this.logoutUser();
        }
    }

    private void logoutUser() {
        session.setLogin(false);
//        databaseHelper.deleteUsers();
        // Launching the the_menu activity
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apa Anda ingin keluar?")
                .setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        })
                //jika pilih no
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();

    }

}
