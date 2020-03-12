package com.example.beproj3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Emergency extends AppCompatActivity {
    CardView polic ,ambul , fira ,seni;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        polic = findViewById(R.id.police);
        ambul = findViewById(R.id.ambu);
        fira = findViewById(R.id.fire);
        seni = findViewById(R.id.sen);
        back = findViewById(R.id.backo);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Emergency.this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

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
