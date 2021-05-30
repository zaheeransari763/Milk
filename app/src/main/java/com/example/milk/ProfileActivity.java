package com.example.milk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{

    TextView nameProfile, emailProfile, phoneProfile, ageProfile, cityProfile, districtProfile, addressProfile;
    FirebaseAuth mAuth;
    DatabaseReference DonorRef;
    String currentUserId;
    private CircleImageView userProfileImage;

    Uri imageUri;

    private ProgressDialog loadingBar;
    StorageReference mImageStorage ;
    private StorageTask uploadTask;
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        userProfileImage = (CircleImageView) findViewById(R.id.profile_image_upload);
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        currentUserId = mAuth.getCurrentUser().getUid();
        DonorRef = FirebaseDatabase.getInstance().getReference().child("MotherDonor").child(currentUserId);

        nameProfile = (TextView) findViewById(R.id.profile_name);
        emailProfile = (TextView) findViewById(R.id.profile_email);
        phoneProfile = (TextView) findViewById(R.id.profile_phone);
        ageProfile = (TextView) findViewById(R.id.profile_age);
        cityProfile = (TextView) findViewById(R.id.profile_city);
        districtProfile = (TextView) findViewById(R.id.profile_district);
        addressProfile = (TextView) findViewById(R.id.profile_address);



        DonorRef.addValueEventListener(new ValueEventListener()
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

                    nameProfile.setText(name);
                    emailProfile.setText(email);
                    phoneProfile.setText(phone);
                    ageProfile.setText(age);
                    districtProfile.setText(district);
                    cityProfile.setText(city);
                    addressProfile.setText(address);

                    final String image = dataSnapshot.child("image").getValue().toString();
                    if(!image.equals("default"))
                    {
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.add_profile_photo).into(userProfileImage);
                        Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.add_profile_photo).into(userProfileImage, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {

                            }
                            @Override
                            public void onError()
                            {
                                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.add_profile_photo).into(userProfileImage);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && uploadTask!= null && uploadTask.isInProgress() && data != null && data.getData() != null)
            {
                Toast.makeText(this, "Upload In Progress", Toast.LENGTH_SHORT).show();
            }
            else
            {
                UploadImage();
            }
        }
    }

    private void UploadImage() {
        loadingBar = new ProgressDialog(ProfileActivity.this);
        loadingBar.setTitle("Uploading Image...");
        loadingBar.setMessage("Please wait while we upload and process the image.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if(imageUri != null)
        {
            String current_user_id = mAuth.getUid();
            final StorageReference fileReference = mImageStorage.child("profile_images").child(current_user_id + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        String current_uid = mAuth.getUid();
                        DonorRef = FirebaseDatabase.getInstance().getReference("MotherDonor").child(current_uid);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", mUri);
                        DonorRef.updateChildren(map);
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }


}
