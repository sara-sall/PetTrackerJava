package com.example.pettracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.pettracker.MyPetsListAdapter;
import com.example.pettracker.Pets;
import com.example.pettracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyPetsListAdapter adapter;
    private Toolbar toolbar;
    private ArrayList<Pets> myPetsList;

    private FloatingActionButton fab;

    private FirebaseFirestore db;
    private CollectionReference petsRef;

    private FirebaseAuth mAuth;
    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        petsRef = db.collection("users").document(user).collection("pets");

        toolbar = findViewById(R.id.toolbarID);
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);

        fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(v.getContext(), AddPetActivity.class);
                v.getContext().startActivity(intent);
            }
        });


        myPetsList = new ArrayList<Pets>();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyPetsListAdapter(myPetsList);
        recyclerView.setAdapter(adapter);

        checkPets();

    }


    public void checkPets() {
        petsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("!!!", "Listen failed.", e);
                    return;
                }
                for (DocumentChange doc : value.getDocumentChanges()) {
                    switch (doc.getType()){
                        case ADDED:
                            petsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot q = task.getResult();
                                    myPetsList.clear();
                                    for (DocumentSnapshot d : q.getDocuments()) {
                                        Pets pet = d.toObject(Pets.class);
                                        myPetsList.add(pet);
                                        pet.setPetId(d.getId());
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case REMOVED:
                            Pets p = null;
                            for(Pets pet: myPetsList){
                                if(pet.getPetId().equals(doc.getDocument().getId())){
                                    p = pet;
                                }
                            }

                            if (p != null){
                                myPetsList.remove(p);
                                adapter.notifyDataSetChanged();
                            }

                            break;
                    }



                }
            }
        });

    }
}
