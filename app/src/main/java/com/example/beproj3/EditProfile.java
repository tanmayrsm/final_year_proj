package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.UserStatus;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditProfile extends Activity {
    ImageView close , image_profile;
    TextView save ,profile_pic_change;
    MaterialEditText name , email;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;

    Spinner spinner1;
    String[] my_lango = {"en_GB", "hi_IN", "mr_IN", "bn_IN", "ta_IN", "te_IN"};
    String[] fb_vals  = {"en","hi","mr","bn","ta","te"};
    String[] lang = {"English","Hindi","Marathi","Bengali","Tamil","Telugu"};
    String my,fb,lango;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        image_profile = findViewById(R.id.image_profile);
        close = findViewById(R.id.close);

        save = findViewById(R.id.save);
        profile_pic_change = findViewById(R.id.tv_change);

        name = findViewById(R.id.name);
        email = findViewById(R.id.username);

        spinner1 = findViewById(R.id.spin2);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    if(user.getImage_url() != null)
                        Glide.with(getApplicationContext()).load(user.getImage_url()).into(image_profile);
                    else
                        Glide.with(getApplicationContext()).load(R.drawable.ic_face_black_24dp).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storageRef = FirebaseStorage.getInstance().getReference("images");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfile.this ,MainActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        profile_pic_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                        .start(EditProfile.this);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                        .start(EditProfile.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("At pos0:", (String) spinner1.getItemAtPosition(0));
                String text = spinner1.getSelectedItem().toString();
                index = getIndexOf(lang,text);

                my = my_lango[index];
                fb = fb_vals[index];
                lango = lang[index];

                Log.e("Lang:",lango);
                updateProfile(name.getText().toString(),email.getText().toString(),my,fb,lango);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        image_profile = findViewById(R.id.image_profile);
        close = findViewById(R.id.close);

        save = findViewById(R.id.save);
        profile_pic_change = findViewById(R.id.tv_change);

        name = findViewById(R.id.name);
        email = findViewById(R.id.username);

        spinner1 = findViewById(R.id.spin2);

        DatabaseReference jam = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        jam.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jam.child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jam.child("lang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String j = dataSnapshot.getValue().toString();
                //my_lango = setter(j);
                int k = getIndex(spinner1 ,j);
//                Log.e("k:",String.valueOf(k));
//                Log.e("Val:",lang[k]);
                change(k);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void change(int k) {
        String temp;
        temp = my_lango[0];
        my_lango[0] = my_lango[k];
        my_lango[k] = temp;

        String temp2;
        temp2 = fb_vals[0];
        fb_vals[0] = fb_vals[k];
        fb_vals[k] = temp2;

        String temp3;
        temp3 = lang[0];
        lang[0] = lang[k];
        lang[k] = temp3;

        Log.e("Lang array",lang.toString());

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lang); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spinnerArrayAdapter);
    }

    public static int getIndexOf(String[] strings, String item) {
        for (int i = 0; i < strings.length; i++) {
            if (item.equals(strings[i])) return i;
        }
        return -1;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void uploadImage(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if(mImageUri != null){
            String jam = "";
            if(getFileExtension(mImageUri) == null)
                jam = "jpg";
            else    jam = getFileExtension(mImageUri);
            final StorageReference filereference = storageRef.child(System.currentTimeMillis() + "." +jam);
            uploadTask = filereference.putFile(mImageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        String myUrl= downloadUrl.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(firebaseUser.getUid());

                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("image_url" , ""+myUrl);

                        reference.updateChildren(hashMap);
                        //Toast.makeText(EditProfile.this, "Profile Image updated", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }else {
                        Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(String fullname, String username, String my ,String fb ,String lang) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("name" , fullname);
        hashMap.put("email" , username);
        hashMap.put("my_lang" , my);
        hashMap.put("fb_val" , fb);
        hashMap.put("lang" , lang);

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfile.this, "Cannot update profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                CropImage.ActivityResult result2 = CropImage.getActivityResult(data);
                mImageUri = result2.getUri();
                uploadImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("Error crop:",error.toString());
            }
        }
    }

}
