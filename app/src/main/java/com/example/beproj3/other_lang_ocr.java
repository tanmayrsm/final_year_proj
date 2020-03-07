package com.example.beproj3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class other_lang_ocr extends AppCompatActivity {
    TextView hin1 ,mar1 ,ben1 ,tam1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_lang_ocr);

        hin1 = findViewById(R.id.hindi);
        mar1 = findViewById(R.id.mar);
        ben1 = findViewById(R.id.ben);
        tam1 = findViewById(R.id.tam);

        hin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), OCR2Activity.class);
                intent.putExtra("lan", "mar");
                startActivity(intent);
            }
        });
        mar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), OCR2Activity.class);
                intent.putExtra("lan", "mar");
                startActivity(intent);
            }
        });
        ben1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), OCR2Activity.class);
                intent.putExtra("lan", "ben");
                startActivity(intent);
            }
        });
        tam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), OCR2Activity.class);
                intent.putExtra("lan", "tam");
                startActivity(intent);
            }
        });

    }
}
