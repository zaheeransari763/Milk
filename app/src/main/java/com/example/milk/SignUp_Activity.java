package com.example.milk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp_Activity extends AppCompatActivity {
    private EditText email,name,password,contact,age,city,district,address;
    private Button reg_btn;
    FirebaseAuth mAuth;
    DatabaseReference donorRef;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_sign_up_);

        mAuth = FirebaseAuth.getInstance();
        //donorRef = FirebaseDatabase.getInstance().getReference().child("MotherDonor");

        loadingBar = new ProgressDialog(this);

        email=(EditText)findViewById(R.id.reg_email);
        name=(EditText)findViewById(R.id.reg_name);
        password=(EditText)findViewById(R.id.reg_pass);
        contact=(EditText)findViewById(R.id.reg_phone);
        age=(EditText)findViewById(R.id.reg_age);
        district=(EditText)findViewById(R.id.reg_district);
        address=(EditText)findViewById(R.id.reg_address);
        city=(EditText)findViewById(R.id.reg_city);

        reg_btn=(Button)findViewById(R.id.btn_sing_up);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserToDB();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser studcurrentUser = mAuth.getCurrentUser();
        if(studcurrentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent=new Intent(this,DashboardActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void AddUserToDB() {
        final String emailDB = email.getText().toString();
        final String passDB = password.getText().toString();
        final String nameDB = name.getText().toString();
        final String phoneDB = contact.getText().toString();
        final String ageDB = age.getText().toString();
        final String cityDB = city.getText().toString();
        final String districtDB = district.getText().toString();
        final String addressDB = address.getText().toString();

        if (TextUtils.isEmpty(emailDB)) {
            Toast.makeText(this, "E-mail is empty...", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailDB).matches())
        {
            Toast.makeText(this, "Incorrect E-mail", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(passDB)) {
            Toast.makeText(this, "Password is empty...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nameDB)) {
            Toast.makeText(this, "Name is empty...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneDB)) {
            Toast.makeText(this, "Phone is empty...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ageDB)) {
            Toast.makeText(this, "Age is empty...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityDB)) {
            Toast.makeText(this, "City is empty...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(districtDB)) {
            Toast.makeText(this, "District is empty...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressDB)) {
            Toast.makeText(this, "Address is empty...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(emailDB,passDB).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String ClientID = firebaseUser.getUid();

                        donorRef = FirebaseDatabase.getInstance().getReference("MotherDonor").child(ClientID);

                        HashMap<String, Object> donorMap = new HashMap();
                        donorMap.put("Fullname", nameDB);
                        donorMap.put("Email",emailDB);
                        donorMap.put("Contact",phoneDB);
                        donorMap.put("Password",passDB);
                        donorMap.put("UID",ClientID);
                        donorMap.put("City",cityDB);
                        donorMap.put("Address",addressDB);
                        donorMap.put("Age",ageDB);
                        donorMap.put("District",districtDB);
                        donorMap.put("image","default");
                        donorRef.setValue(donorMap).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Intent DashMainIntent = new Intent(SignUp_Activity.this,DashboardActivity.class);
                                    DashMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(DashMainIntent);
                                    finish();
                                }
                            }
                        });

                        Toast.makeText(SignUp_Activity.this, "Authenticated Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SignUp_Activity.this, "Error Occurred ;" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

