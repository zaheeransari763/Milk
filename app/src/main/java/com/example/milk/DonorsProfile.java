package com.example.milk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorsProfile extends AppCompatActivity
{
    TextView donorName, donorAge, donorCity, donorContact, donorEmail, donorAddress, donorDistrict;
    Button rqstByMail, callDonor, saveDonor, rqstByMessage;
    private CircleImageView donorProfile;
    private DatabaseReference donorsProfileRef, SavedDonorRef;
    private FirebaseAuth mAuth;
    String currentUserId, senderUserId, recieverUserId, CURRENT_STATE, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_profile);

        mAuth = FirebaseAuth.getInstance();

        currentUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();
        donorsProfileRef = FirebaseDatabase.getInstance().getReference().child("MotherDonor").child(currentUserId);
        SavedDonorRef = FirebaseDatabase.getInstance().getReference().child("SavedDonor");

        rqstByMail = (Button) findViewById(R.id.requestByMail);
        callDonor = (Button)findViewById(R.id.callDonor);
        saveDonor = (Button) findViewById(R.id.saveDonor);
        rqstByMessage = (Button) findViewById(R.id.messageDonor);

        donorName = (TextView) findViewById(R.id.donorProfile_username);
        donorAge = (TextView) findViewById(R.id.donorProfile_age);
        donorCity = (TextView) findViewById(R.id.donorProfile_city);
        donorContact = (TextView) findViewById(R.id.donorProfile_contact);
        donorEmail = (TextView) findViewById(R.id.donorProfile_email);
        donorAddress = (TextView) findViewById(R.id.donorProfile_address);
        donorDistrict = (TextView) findViewById(R.id.donorProfile_district);

        donorsProfileRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String name = dataSnapshot.child("Fullname").getValue().toString();
                    String age = dataSnapshot.child("Age").getValue().toString();
                    String city = dataSnapshot.child("City").getValue().toString();
                    String district = dataSnapshot.child("District").getValue().toString();
                    String phone = dataSnapshot.child("Contact").getValue().toString();
                    String email = dataSnapshot.child("Email").getValue().toString();
                    String address = dataSnapshot.child("Address").getValue().toString();

                    donorName.setText(name);
                    donorAge.setText(age);
                    donorCity.setText(city);
                    donorDistrict.setText(district);
                    donorContact.setText(phone);
                    donorEmail.setText(email);
                    donorAddress.setText(address);

                    MaintainanceOfButton();

                    /*final String image = dataSnapshot.child("image").getValue().toString();
                    if(!image.equals("default"))
                    {
                        //Picasso.with(DonorsProfile.this).load(image).placeholder(R.drawable.default_avatar).into(donorProfile);
                        Picasso.with(DonorsProfile.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(donorProfile, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            { }

                            @Override
                            public void onError()
                            {
                                //Picasso.with(DonorsProfile.this).load(image).placeholder(R.drawable.default_avatar).into(donorProfile);
                            }
                        });
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

        private void MaintainanceOfButton()
        {
            rqstByMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    SendMailToDonor();
                }
            });

            callDonor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CallToDonor();
                }
            });


            rqstByMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendMessageToDonor();
                }
            });

            saveDonor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveDonorToDB();
                }
            });
        }

    private void SaveDonorToDB()
    {
        donorsProfileRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String name = dataSnapshot.child("Fullname").getValue().toString();
                    String email = dataSnapshot.child("Email").getValue().toString();
                    String phone = dataSnapshot.child("Contact").getValue().toString();
                    String age = dataSnapshot.child("Age").getValue().toString();
                    String district = dataSnapshot.child("District").getValue().toString();
                    String city = dataSnapshot.child("City").getValue().toString();
                    String address = dataSnapshot.child("Address").getValue().toString();

                    HashMap<String, Object> donorMap = new HashMap();
                    donorMap.put("Fullname", name);
                    donorMap.put("Email",email);
                    donorMap.put("Contact",phone);
                    donorMap.put("UID",senderUserId);
                    donorMap.put("City",city);
                    donorMap.put("Address",address);
                    donorMap.put("Age",age);
                    donorMap.put("District",district);
                    donorMap.put("image","default");
                    SavedDonorRef.child(senderUserId).child(currentUserId).setValue(donorMap).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(DonorsProfile.this, "Donor Saved...", Toast.LENGTH_SHORT).show();
                                /*Intent DashMainIntent = new Intent(SignUp_Activity.this,DashboardActivity.class);
                                DashMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(DashMainIntent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessageToDonor() {
        donorsProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String message = dataSnapshot.child("Phone").getValue().toString();
                    Uri mess = Uri.parse("sms:" + message);
                    Intent contactIntent = new Intent(Intent.ACTION_VIEW,mess);
                    contactIntent.putExtra(Intent.EXTRA_TEXT,"Dear Sir/Madam\n" +
                            "By donating the milk you will be hero someone's eye.\n " +
                            "Your small help can help and save someone's life which \n" +
                            "will be good deed for you. If interested to donate\n" +
                            "Please contact the following person\n" +
                            "Contact Details\n" +
                            "Your Email Here\n" +
                            "Your Phone Number here\n");
                    startActivity(Intent.createChooser(contactIntent,"Choose SMS Client"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

        private void CallToDonor() {
        donorsProfileRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String phone = dataSnapshot.child("Phone").getValue().toString();
                    Uri call = Uri.parse("tel:" + phone);
                    Intent contactIntent = new Intent(Intent.ACTION_DIAL,call);
                    //contactIntent.setType("");
                    startActivity(Intent.createChooser(contactIntent,"Choose Calling Client"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

        private void SendMailToDonor()
        {
            donorsProfileRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        String email = dataSnapshot.child("Email").getValue().toString();
                        Uri uri = Uri.parse(email);
                        Intent intent = new Intent(Intent.ACTION_SEND, uri);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Request for the Milk.");
                        intent.putExtra(Intent.EXTRA_TEXT,"Dear Sir/Madam\n" +
                                "\n" +
                                "By donating the blood you will be hero someone's eye. Your small help\n" +
                                "can help and save someone's life which will be very deed for you. If interested to donate\n" +
                                "please contact the following person\n" +
                                "\n" +
                                "If you are intrested please .contact as soon as possible\n" +
                                "Contact Details\n" +
                                "Your Email Here\n" +
                                "Your Phone Number here\n" +
                                "\n");
                        startActivity(Intent.createChooser(intent,"Choose Mailing Client"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                { }
            });
        }
    }