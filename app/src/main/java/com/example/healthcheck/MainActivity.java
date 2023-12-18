package com.example.healthcheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    FileChooser fragment;//choosing file fragment
    Button buttonShowInfo, contact;
    public static Uri path;//path of chosen file
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        this.fragment = (FileChooser) fragmentManager.findFragmentById(R.id.fragment_fileChooser);
        this.buttonShowInfo = this.findViewById(R.id.button_showInfo);
        this.buttonShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = fragment.getPath();//getting file path and showing information
                startActivity(new Intent(getApplicationContext(), Show.class));
            }
        });
        //adding to contact
        this.contact = this.findViewById(R.id.contactbtn);
        this.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), contact.class));
            }
        });
    }
}