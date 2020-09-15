package com.example.beproj3.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beproj3.Models.Chats;
import com.example.beproj3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.webrtc.ContextUtils.getApplicationContext;

public class AllNativeChatsAdapter extends RecyclerView.Adapter<AllNativeChatsAdapter.AllNativeChatsViewHolder> {
    private List<Chats> userMessagesList;

    private DatabaseReference messagesRef;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String LOG_TAG = "AllNativeChatsAdapter";

    public AllNativeChatsAdapter(List<Chats> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    public class AllNativeChatsViewHolder extends RecyclerView.ViewHolder{

        public TextView uska_msg ,mera_msg ,uska_time ,mera_time;
        public CircleImageView uska_image;

        public AllNativeChatsViewHolder(View itemView) {
            super(itemView);
            uska_msg = itemView.findViewById(R.id.receiver_msgs);
            uska_time = itemView.findViewById(R.id.time_received_receiver);
            uska_image = itemView.findViewById(R.id.profile_img);

            mera_msg = itemView.findViewById(R.id.sender_msgs);
            mera_time = itemView.findViewById(R.id.time_sender);
        }
    }

    @NonNull
    @Override
    public AllNativeChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_layout,parent,false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        return new AllNativeChatsAdapter.AllNativeChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllNativeChatsViewHolder holder, int position) {
        //display all messages
        String messageSenderId = firebaseUser.getUid();

        Chats chat = userMessagesList.get(position);
        String id = chat.getWho_tells();
        String time = chat.getTime();
        String message = chat.getChat();
        String type = chat.getType();
        String url = chat.getUrl();


        //watch 50 no to add image
        holder.uska_msg.setVisibility(View.INVISIBLE);
        //holder.uska_image.setVisibility(View.INVISIBLE);
        holder.uska_time.setVisibility(View.INVISIBLE);

        if(id.equals(firebaseUser.getUid())){
            //its sender
            //holder.mera_msg.setBackgroundResource(R.drawable.me_user);

            // native me chahiye ki apne language me aye

            //holder.mera_msg.setText(message);
//            identifyLanguage(message);




            ///
            //its sender
            holder.mera_msg.setBackgroundResource(R.drawable.me_user);
            if(type.equals("pdf")){
                holder.mera_msg.setText("📎"+" PDF");
                holder.mera_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else if(type.equals("location")){
                holder.mera_msg.setText("📌"+" Location");
                holder.mera_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else    {
                holder.mera_msg.setText(message);
                FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                        .getLanguageIdentification();
                identifier.identifyLanguage(message).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals("und")){
                            //Toast.makeText(getApplicationContext(),"Language Not Identified", Toast.LENGTH_SHORT).show();
                            holder.mera_msg.setText(message);
                        }
                        else {
//                        getLanguageCode(s);
                            int langCode;
                            switch (s){
                                case "hi":
                                    langCode = FirebaseTranslateLanguage.HI;
                                    //mSourceLang.setText("Hindi");
                                    break;
                                case "mr":
                                    langCode = FirebaseTranslateLanguage.MR;
                                    //mSourceLang.setText("Marathi");

                                    break;
                                case "bn":
                                    langCode = FirebaseTranslateLanguage.BN;
                                    //mSourceLang.setText("Bengali");
                                    break;

                                case "ta":
                                    langCode = FirebaseTranslateLanguage.TA;
                                    //mSourceLang.setText("Tamil");
                                    break;

                                case "te":
                                    langCode = FirebaseTranslateLanguage.TE;
                                    //mSourceLang.setText("Telugu");
                                    break;

                                case "en":
                                    langCode = FirebaseTranslateLanguage.EN;
                                    //mSourceLang.setText("English ha");

                                    break;
                                default:
                                    langCode = 0;
                            }
//                        translateText()

                            DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
                            fbcode.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String fb_code = dataSnapshot.getValue().toString();
                                    int to = 0;
                                    switch (fb_code){
                                        case "hi":
                                            to = FirebaseTranslateLanguage.HI;
                                            //mSourceLang.setText("Hindi");
                                            break;
                                        case "mr":
                                            to = FirebaseTranslateLanguage.MR;
                                            //mSourceLang.setText("Marathi");

                                            break;
                                        case "bn":
                                            to = FirebaseTranslateLanguage.BN;
                                            //mSourceLang.setText("Bengali");
                                            break;

                                        case "ta":
                                            to = FirebaseTranslateLanguage.TA;
                                            //mSourceLang.setText("Tamil");
                                            break;

                                        case "te":
                                            to = FirebaseTranslateLanguage.TE;
                                            //mSourceLang.setText("Telugu");
                                            break;

                                        case "en":
                                            to = FirebaseTranslateLanguage.EN;
                                            //mSourceLang.setText("English ha");

                                            break;
                                        default:
                                            to = 0;
                                    }

                                    FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                                            //from language
                                            .setSourceLanguage(langCode)
                                            // to language
                                            .setTargetLanguage(to)
                                            .build();

                                    final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                                            .getTranslator(options);

                                    FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                                            .build();

                                    translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            translator.translate(message).addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String s) {
                                                    holder.mera_msg.setText(s);
                                                }
                                            });
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                });

            }
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

            holder.uska_msg.setBackgroundResource(R.drawable.other_user);
            //holder.uska_msg.setText(message);




            if(type.equals("pdf")){
                holder.uska_msg.setText("📎"+" PDF");
                holder.uska_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else if(type.equals("location")){
                holder.uska_msg.setText("📌"+" Location");
                holder.uska_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else   {
                holder.uska_msg.setText(message);

                //       identifyLanguage(message);
                FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                        .getLanguageIdentification();

                identifier.identifyLanguage(message).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals("und")){
                            Toast.makeText(getApplicationContext(),"Language Not Identified", Toast.LENGTH_SHORT).show();
                            holder.mera_msg.setText(message);
                        }
                        else {
                            //Log.e(LOG_TAG ,"Message hai: "+message);
                            //Log.e(LOG_TAG,"Else me h and doing from - "+ s);
//                      getLanguageCode(s);
                            int langCode;
                            switch (s){
                                case "hi":
                                    langCode = FirebaseTranslateLanguage.HI;
                                    //mSourceLang.setText("Hindi");
                                    break;
                                case "mr":
                                    langCode = FirebaseTranslateLanguage.MR;
                                    //mSourceLang.setText("Marathi");

                                    break;
                                case "bn":
                                    langCode = FirebaseTranslateLanguage.BN;
                                    //mSourceLang.setText("Bengali");
                                    break;

                                case "ta":
                                    langCode = FirebaseTranslateLanguage.TA;
                                    //mSourceLang.setText("Tamil");
                                    break;

                                case "te":
                                    langCode = FirebaseTranslateLanguage.TE;
                                    //mSourceLang.setText("Telugu");
                                    break;

                                case "en":
                                    langCode = FirebaseTranslateLanguage.EN;
                                    //mSourceLang.setText("English ha");

                                    break;
                                default:
                                    langCode = 0;
                            }
//                        translateText()

                            DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
                            fbcode.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String fb_code = dataSnapshot.getValue().toString();
                                    //Log.e(LOG_TAG,"ELse me h and doing to - "+ fb_code);
                                    int to;
                                    switch (fb_code){
                                        case "hi":
                                            to = FirebaseTranslateLanguage.HI;
                                            //mSourceLang.setText("Hindi");
                                            break;
                                        case "mr":
                                            to = FirebaseTranslateLanguage.MR;
                                            //mSourceLang.setText("Marathi");

                                            break;
                                        case "bn":
                                            to = FirebaseTranslateLanguage.BN;
                                            //mSourceLang.setText("Bengali");
                                            break;

                                        case "ta":
                                            to = FirebaseTranslateLanguage.TA;
                                            //mSourceLang.setText("Tamil");
                                            break;

                                        case "te":
                                            to = FirebaseTranslateLanguage.TE;
                                            //mSourceLang.setText("Telugu");
                                            break;

                                        case "en":
                                            to = FirebaseTranslateLanguage.EN;
                                            //mSourceLang.setText("English ha");

                                            break;
                                        default:
                                            to = 0;
                                    }

                                    FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                                            //from language
                                            .setSourceLanguage(langCode)
                                            // to language
                                            .setTargetLanguage(to)
                                            .build();

                                    final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                                            .getTranslator(options);

                                    FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                                            .build();

                                    translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            translator.translate(message).addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String s) {
//                                                Log.e(LOG_TAG , "message :"+message);
//                                                Log.e(LOG_TAG , "from - to :"+fb_code + " ");
//                                                Log.e(LOG_TAG ,"converted: "+s);
                                                    holder.uska_msg.setText(s);

                                                    //String j = holder.mera_msg.getText().toString();

                                                    //Log.e(LOG_TAG,"set kiyela:"+holder.mera_msg.getText().toString());;

                                                    //holder.mera_msg.setText(j);
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });
                        }
                    }
                });

            }


            try {
                holder.uska_time.setText(datto(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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
