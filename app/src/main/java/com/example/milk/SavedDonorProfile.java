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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SavedDonorProfile extends AppCompatActivity
{
    TextView saveddonorName, saveddonorAge, saveddonorCity, saveddonorContact, saveddonorEmail, saveddonorAddress, saveddonorDistrict;
    Button savedrqstByMail, savedcallDonor, savedrqstByMessage;
    private CircleImageView saveddonorProfile;
    private DatabaseReference donorsProfileRef, SavedDonorRef;
    private FirebaseAuth mAuth;
    String currentUserId, senderUserId, recieverUserId, CURRENT_STATE, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_donor_profile);

        mAuth = FirebaseAuth.getInstance();

        //currentUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();
        //donorsProfileRef = FirebaseDatabase.getInstance().getReference().child("SavedDonor").child(currentUserId);
        SavedDonorRef = FirebaseDatabase.getInstance().getReference().child("SavedDonor").child(senderUserId);

        savedrqstByMail = (Button) findViewById(R.id.savedrequestByMail);
        savedcallDonor = (Button)findViewById(R.id.savedcallDonor);
        savedrqstByMessage = (Button) findViewById(R.id.savedmessageDonor);

        saveddonorName = (TextView) findViewById(R.id.saveddonorProfile_username);
        saveddonorAge = (TextView) findViewById(R.id.saveddonorProfile_age);
        saveddonorCity = (TextView) findViewById(R.id.saveddonorProfile_city);
        saveddonorContact = (TextView) findViewById(R.id.saveddonorProfile_contact);
        saveddonorEmail = (TextView) findViewById(R.id.saveddonorProfile_email);
        saveddonorAddress = (TextView) findViewById(R.id.saveddonorProfile_address);
        saveddonorDistrict = (TextView) findViewById(R.id.saveddonorProfile_district);

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

                    saveddonorName.setText(name);
                    saveddonorAge.setText(age);
                    saveddonorCity.setText(city);
                    saveddonorDistrict.setText(district);
                    saveddonorContact.setText(phone);
                    saveddonorEmail.setText(email);
                    saveddonorAddress.setText(address);

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
        savedrqstByMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendMailToDonor();
            }
        });

        savedcallDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallToDonor();
            }
        });


        savedrqstByMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessageToDonor();
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
