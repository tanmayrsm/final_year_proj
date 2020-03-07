package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beproj3.Models.Prevalent;
import com.example.beproj3.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    EditText edEmail ,edPass;
    Button login;
    FirebaseAuth auth;
    TextView newUser;
    ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);
        String emailUser = Paper.book().read(Prevalent.UserEmailKey);
        String passo = Paper.book().read(Prevalent.UserPasswordKey);

        loadingbar = new ProgressDialog(this);
        loadingbar.setTitle("Logging In");
        loadingbar.setMessage("Please wait...");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        if(emailUser!=null && passo!=null)
            AllowAccess(emailUser ,passo);
        else    loadingbar.dismiss();

        edEmail = findViewById(R.id.email);
        edPass = findViewById(R.id.password);

        newUser = findViewById(R.id.new_user);

        login = findViewById(R.id.submit);
        auth = FirebaseAuth.getInstance();

        Paper.init(this);

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this,Regsiter.class);
                startActivity(intent);
            }
        });
    }

    private void AllowAccess(String emailUser, String passo) {
        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference("Users");

        rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dss : dataSnapshot.getChildren()) {

                    User user = dss.getValue(User.class);

                    if(user.getEmail().equals(emailUser) && user.getPassword().equals(passo)){
                        loadingbar.dismiss();
                        Intent i = new Intent(Login.this,MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingbar.dismiss();
            }
        });
    }

    public void login(View v){
        String email = edEmail.getText().toString();
        String pass = edPass.getText().toString();

        if(!email.equals("") && !pass.equals("")){
            auth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Paper.book().write(Prevalent.UserEmailKey , email);
                                Paper.book().write(Prevalent.UserPasswordKey ,pass);

                                Intent intent = new Intent(Login.this ,MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                String error2 = task.getException().getMessage();
                                Toast.makeText(Login.this, "something Fishy: "+error2, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}