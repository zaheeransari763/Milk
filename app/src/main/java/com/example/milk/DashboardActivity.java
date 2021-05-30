package com.example.milk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.jar.Attributes;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity
{
    private ImageView donate,request,doners,contact,profile, saved,logout;
    private TextView guide, usernameTop, contactTop;
    FirebaseAuth mAuth;
    DatabaseReference donorRef;
    String currentUserId;
    CircleImageView dashprofileimage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        donorRef = FirebaseDatabase.getInstance().getReference().child("MotherDonor").child(currentUserId);

        usernameTop = (TextView) findViewById(R.id.name_user);
        contactTop = (TextView) findViewById(R.id.user_info);
        dashprofileimage=(CircleImageView)findViewById(R.id.imageView2);

        guide = (TextView) findViewById(R.id.text_guide);

        donorRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String name = dataSnapshot.child("Fullname").getValue().toString();
                    String phone = dataSnapshot.child("Contact").getValue().toString();

                    usernameTop.setText(name);
                    contactTop.setText(phone);

                    final String image = dataSnapshot.child("image").getValue().toString();
                    if(!image.equals("default"))
                    {
                        Picasso.with(DashboardActivity.this).load(image).placeholder(R.drawable.icguide).into(dashprofileimage);
                        Picasso.with(DashboardActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.icguide).into(dashprofileimage, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {

                            }
                            @Override
                            public void onError()
                            {
                                Picasso.with(DashboardActivity.this).load(image).placeholder(R.drawable.icguide).into(dashprofileimage);
                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        donate=(ImageView)findViewById(R.id.donateImageView);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,DonateActivity.class);
                startActivity(intent);
            }
        });


        request=(ImageView)findViewById(R.id.requestImageView);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,RequestActivity.class);
                startActivity(intent);
            }
        });

        doners=(ImageView)findViewById(R.id.donorListImage);
        doners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,DonorListActivity.class);
                startActivity(intent);
            }
        });

        contact=(ImageView)findViewById(R.id.contactImageView);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,ContactActivity.class);
                startActivity(intent);
            }
        });

        profile=(ImageView)findViewById(R.id.profileImageView);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        saved=(ImageView)findViewById(R.id.savedDonorImageView);
        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,SavedDonor.class);
                startActivity(intent);
            }
        });
        logout=(ImageView)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent=new Intent(DashboardActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,GuideActivity.class);
                startActivity(intent);
            }
        });
    }
}
