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

public class MainActivity extends AppCompatActivity
{
    public EditText username,password;
    public Button loginbtn;
    FirebaseAuth mAuth;
    DatabaseReference donorRef;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //donorRef = FirebaseDatabase.getInstance().getReference().child("MotherDonor");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        loginbtn = (Button) findViewById(R.id.login_button);
        loginbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LoginUserToDB();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            SendToMain();
        }

    }

    private void SendToMain() {
        Intent main = new Intent(this,DashboardActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    private void LoginUserToDB() {
        final String emailDB = username.getText().toString();
        final String passwordDB = password.getText().toString();
        if (TextUtils.isEmpty(emailDB))
        {
            Toast.makeText(this, "E-mail is empty...", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailDB).matches())
        {
            Toast.makeText(this, "Incorrect E-mail", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(passwordDB))
        {
            Toast.makeText(this, "Password is empty...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(emailDB,passwordDB).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        Intent DashMainIntent = new Intent(MainActivity.this,DashboardActivity.class);
                        DashMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(DashMainIntent);
                        finish();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error Occurred ;" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void OpenSignupPage(View view) {
        startActivity(new Intent(MainActivity.this,SignUp_Activity.class));
    }
}

