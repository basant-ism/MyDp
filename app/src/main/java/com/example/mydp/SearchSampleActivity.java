package com.example.mydp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import model.ApplicationClass;
import model.SampleImage;
import view_holder.SampleImageViewHolder;

public class SearchSampleActivity extends AppCompatActivity {
    SearchView searchView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder> adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        ApplicationClass.loadLocale(this);
        setContentView(R.layout.activity_search_sample);
        searchView=findViewById(R.id.search_view);
        ImageView imageView =(ImageView) searchView.findViewById(R.id.search_button);
        imageView.performClick();
        db= FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchData(null);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return true;
            }
        });
    }

    private void searchData(String text) {
        Query query=db.collection("image");
        if(text!=null&&!text.equals(""))
            query=db.collection("image").orderBy("title").startAt(text.toLowerCase());



        FirestoreRecyclerOptions<SampleImage> options=new FirestoreRecyclerOptions.Builder<SampleImage>()
                .setQuery(query,SampleImage.class)
                .build();
        adapter=new FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SampleImageViewHolder holder, int position, @NonNull final SampleImage model) {
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.imgMore.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
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
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);
                return new SampleImageViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SearchSampleActivity.this,MainActivity.class));
    }
}
