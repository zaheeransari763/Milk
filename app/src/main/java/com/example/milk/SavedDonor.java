package com.example.milk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SavedDonor extends AppCompatActivity
{

    RecyclerView savedDonarList;
    FirebaseAuth mAuth;
    DatabaseReference donorsProfileRef,SavedDonorRef;
    String currentUserId, senderUserId, recieverUserId, CURRENT_STATE, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_donor);

        mAuth = FirebaseAuth.getInstance();
        //currentUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();
        //donorsProfileRef = FirebaseDatabase.getInstance().getReference().child("MotherDonor").child(currentUserId);
        SavedDonorRef = FirebaseDatabase.getInstance().getReference().child("SavedDonor");

        savedDonarList = (RecyclerView) findViewById(R.id.savedDonorList);
        savedDonarList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        savedDonarList.setLayoutManager(linearLayoutManager);

        startListen();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startListen();
    }

    private void startListen()
    {
        Query query = FirebaseDatabase.getInstance().getReference().child("SavedDonor").child(senderUserId).limitToLast(50);
        FirebaseRecyclerOptions<Donor> options = new FirebaseRecyclerOptions.Builder<Donor>().setQuery(query, Donor.class).build();
        FirebaseRecyclerAdapter<Donor, DonorsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Donor, DonorsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull DonorsViewHolder holder, final int position, @NonNull Donor model)
            {
                //final String PostKey = getRef(position).getKey();

                holder.setFullnamee(model.getFullnamee());
                holder.setAgee(model.getAgee());
                holder.setCityy(model.getCityy());
                holder.setEmaill(model.getEmaill());
                holder.setAddresss(model.getAddresss());
                //holder.setImagee(getApplicationContext(),model.getImagee());
                holder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String visit_user_id = getRef(position).getKey();
                        Intent donorProfileIntent = new Intent(SavedDonor.this,DonorsProfile.class);
                        donorProfileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(donorProfileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public DonorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_donor_list,parent,false);
                return new DonorsViewHolder(view);
            }
        };
        savedDonarList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class DonorsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public DonorsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setFullnamee(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.donor_username);
            username.setText(fullname);
        }

        /*public void setImagee(Context ctx, String image)
        {
            CircleImageView donorimage = (CircleImageView) mView.findViewById(R.id.donor_profile_image);
            Picasso.get().load(image).into(donorimage);
        }*/

        public void setAgee(String age)
        {
            TextView dob = (TextView) mView.findViewById(R.id.donor_age);
            dob.setText(age);
        }

        public void setCityy(String city)
        {
            TextView bldgroup = (TextView) mView.findViewById(R.id.donor_city);
            bldgroup.setText(city);
        }

        public void setAddresss(String address)
        {
            TextView addresdoonor = (TextView) mView.findViewById(R.id.donor_address);
            addresdoonor.setText(address);
        }

        public void setEmaill(String email)
        {
            TextView donoremail = (TextView) mView.findViewById(R.id.donor_email);
            donoremail.setText(email);
        }
    }

}
