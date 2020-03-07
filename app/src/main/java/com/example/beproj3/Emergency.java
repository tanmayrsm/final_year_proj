package com.example.beproj3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Emergency extends AppCompatActivity {
    Button polic ,ambul , fira ,seni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        polic = findViewById(R.id.police);
        ambul = findViewById(R.id.ambu);
        fira = findViewById(R.id.fire);
        seni = findViewById(R.id.sen);

        polic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Emergency.this, "We will use normal calling services for this", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:100"));

                startActivity(i);
            }
        });

        ambul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Emergency.this, "We will use normal calling services for this", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:102"));

                startActivity(i);
            }
        });

        fira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Emergency.this, "We will use normal calling services for this", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:101"));

                startActivity(i);
            }
        });

        seni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Emergency.this, "We will use normal calling services for this", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:1091"));

                startActivity(i);
            }
        });


    }
}
