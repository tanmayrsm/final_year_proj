package com.example.beproj3.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beproj3.MainActivity;
import com.example.beproj3.Models.Chats;
import com.example.beproj3.R;
import com.example.beproj3.chat_history;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class AllChatsAdapter extends RecyclerView.Adapter<AllChatsAdapter.AllChatsViewHolder> {
    private List<Chats> userMessagesList;

    private DatabaseReference messagesRef;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    Activity context;

    String uska_img_url = "";

    public AllChatsAdapter(Activity context,List<Chats> userMessagesList){
        this.userMessagesList = userMessagesList;
        this.context = context;
    }

    public class AllChatsViewHolder extends RecyclerView.ViewHolder{

        public TextView uska_msg ,mera_msg ,uska_time ,mera_time;
        public CircleImageView uska_image;

        public AllChatsViewHolder(View itemView) {
            super(itemView);
            uska_msg = itemView.findViewById(R.id.receiver_msgs);
            uska_time = itemView.findViewById(R.id.time_received_receiver);
            uska_image = itemView.findViewById(R.id.profile_img);

            mera_msg = itemView.findViewById(R.id.sender_msgs);
            mera_time = itemView.findViewById(R.id.time_sender);

//            if(user.getImage_url()!=null)
//                Glide.with(context).load(user.getImage_url()).into(holder.prof_image);

            mera_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public AllChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_layout,parent,false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        return new AllChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllChatsViewHolder holder, int position) {
        //PDFDocument pdf = new PDFDocument(file.getAbsolutePath(), null);;


        //display all messages
        String messageSenderId = firebaseUser.getUid();

        Chats chat = userMessagesList.get(position);
        String id = chat.getWho_tells();
        String time = chat.getTime();
        String message = chat.getChat();
        String type = chat.getType();
        String url = chat.getUrl();



        //holder.uska_image.setImageDrawable(null);
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users").child(id).child("image_url");

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    uska_img_url = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //watch 50 no to add image
        holder.uska_msg.setVisibility(View.INVISIBLE);
        //holder.uska_image.setVisibility(View.INVISIBLE);
        holder.uska_time.setVisibility(View.INVISIBLE);

        if(id.equals(firebaseUser.getUid())){
            //its sender
            holder.mera_msg.setBackgroundResource(R.drawable.me_user);
            if(!url.equals("")){
                holder.mera_msg.setText("📎"+" PDF");
                holder.mera_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else if(type.equals("location")){
                holder.mera_msg.setText("📌"+" Location");
                holder.mera_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else    holder.mera_msg.setText(message);
            try {
                holder.mera_time.setText(datto(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
        else{
            holder.mera_msg.setVisibility(View.INVISIBLE);
            holder.mera_time.setVisibility(View.INVISIBLE);

            holder.uska_msg.setVisibility(View.VISIBLE);
            //holder.uska_image.setVisibility(View.VISIBLE);
            holder.uska_time.setVisibility(View.VISIBLE);

            Log.e("Uska img url:",uska_img_url);

            if(uska_img_url!=null){
                    //Glide.with(context).load(uska_img_url).into(holder.uska_image);
            }


            else    holder.uska_msg.setBackgroundResource(R.drawable.other_user);

            if(!url.equals("")){
                holder.uska_msg.setText("📎"+" PDF");
                holder.uska_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else if(type.equals("location")){
                holder.uska_msg.setText("📌"+" Location");
                holder.uska_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else    holder.uska_msg.setText(message);


            try {
                holder.uska_time.setText(datto(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.mera_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("adapter","clicked at "+ position);

                if(holder.mera_msg.getText().toString().equals("📎"+" PDF")){
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(url));

                    String filepath = Environment.getExternalStorageDirectory().getPath();
                    File file = new File(filepath+ "/" + System.currentTimeMillis() + ".pdf", String.valueOf(Uri.parse(url)));
                    Log.e("Storing at",filepath+ "/Download/" + System.currentTimeMillis() + ".pdf");

                    //useless as we get exception all tym
                    if (!file.exists()) {

                            file.mkdirs();
                        //    Log.e("Andar","Andar ka exception:"+e.getMessage());
                            //e.printStackTrace();

                    }
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                    Uri returnUri = intent.getData();
//
                    Log.e("Saved at :"," return uri:"+file.getAbsolutePath());
                    //String mimeType = getContentResolver().getType(returnUri);


                }
                else if(holder.mera_msg.getText().toString().equals("📌"+" Location")){
                    String[] splitStr = message.split("\\s+");
                    ((chat_history)context).salla(splitStr[0],splitStr[1]);

                }
            }
        });

        holder.uska_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("adapter","clicked at "+ position);
                if(holder.uska_msg.getText().toString().equals("📎"+" PDF")){
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(url));

                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
//                    Uri returnUri = intent.getData();
//
//                    Log.e("NAme of :"," return uri:"+returnUri);
                    //String mimeType = getContentResolver().getType(returnUri);


                }

                else if(holder.uska_msg.getText().toString().equals("📌"+" Location")){
                    String[] splitStr = message.split("\\s+");
                    ((chat_history)context).salla(splitStr[0],splitStr[1]);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public String datto(String in_date) throws ParseException {

        String input = in_date;
        //Date/time pattern of input date
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        Date date = null;
        String output = null;
        try{
            //Conversion of input String to date
            date= df.parse(input);
            //old date format to new date format
            output = outputformat.format(date);
            System.out.println(output);
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        return output;
    }
}
