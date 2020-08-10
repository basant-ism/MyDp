package com.example.mydp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import model.ApplicationClass;
import model.SampleImage;
import view_holder.SampleImageViewHolder;

public class SampleActivity extends AppCompatActivity {
FirebaseFirestore db;
FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder>adapter;
RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(SampleActivity.this);
        setContentView(R.layout.activity_sample);
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query=db.collection("image");
        FirestoreRecyclerOptions<SampleImage>options=new FirestoreRecyclerOptions.Builder<SampleImage>()
                                                        .setQuery(query,SampleImage.class)
                                                        .build();
        adapter=new FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SampleImageViewHolder holder, int position, @NonNull final SampleImage model) {

                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment=new CreateDpFragment(model.getImageUrl());
                        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }
                });
            }

            @NonNull
            @Override
            public SampleImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);
                return new SampleImageViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }
    public void goToBack(View view)
    {
        finish();
    }
}
