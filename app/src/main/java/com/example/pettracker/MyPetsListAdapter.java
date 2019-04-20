package com.example.pettracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPetsListAdapter extends RecyclerView.Adapter {

    private static List<Pets> myPetsList;
    private Context context;

    public static class PetsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private FirebaseAuth mAuth;
        public String user;
        private String petID;

        private TextView petName;
        private ImageView petImage;

        private FirebaseFirestore db;

        public Context mContext;



        public PetsViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db = FirebaseFirestore.getInstance();

            mContext = itemView.getContext();
            petName = (TextView) itemView.findViewById(R.id.petNameID);
            petImage = (ImageView) itemView.findViewById(R.id.petImageID);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            final Pets pet = myPetsList.get(position);
            petID = pet.getPetId();
            if (v.getId() == R.id.recyclerSquareMainID) {
                Intent intent = new Intent(v.getContext(), ShowPetActivity.class);
                intent.putExtra("petID", petID);
                v.getContext().startActivity(intent);

            }


        }
    }

    public MyPetsListAdapter(List<Pets> myPetsList) {
        this.myPetsList = myPetsList;


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_square, viewGroup, false);

        PetsViewHolder petsViewHolder = new PetsViewHolder(view);
        context = viewGroup.getContext();
        return petsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int in) {
        final PetsViewHolder vh = (PetsViewHolder) viewHolder;
        final int i = in;
        vh.petName.setText(myPetsList.get(i).getName());

        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        if(!myPetsList.get(i).getImageId().equals("")){
            StorageReference sr = firebaseStorage.getReference().child(myPetsList.get(i).getImageId());
            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context).load(uri).resize(150, 95).onlyScaleDown().centerCrop().into(vh.petImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    vh.petImage.setVisibility(View.GONE);
                    //Picasso.with(context).load(R.drawable.ic_restaurant_color_24dp).into(vh.imageView);
                    //vh.imageView.setImageResource(R.drawable.ic_restaurant_color_24dp);
                }
            });
        }else{
            vh.petImage.setVisibility(View.GONE);
            //Picasso.with(context).load(R.drawable.ic_restaurant_color_24dp).into(vh.imageView);
            //vh.imageView.setImageResource(R.drawable.ic_restaurant_color_24dp);
        }


    }

    @Override
    public int getItemCount() {
        return myPetsList.size();
    }
}
