package com.example.mydp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import dialog.CustumProgressDialog;
import model.ApplicationClass;
import model.SampleImage;
import view_holder.SampleImageViewHolder;

public class CreatedDPactivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder>adapter;
    RecyclerView recyclerView;
    StorageReference mStoreRef;
    CustumProgressDialog dialog;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(CreatedDPactivity.this);
        setContentView(R.layout.activity_created_d_pactivity);
        db=FirebaseFirestore.getInstance();
        mStoreRef= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        dialog=new CustumProgressDialog(CreatedDPactivity.this);

        recyclerView=findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }
    @Override
    protected void onStart() {
        super.onStart();
        Query query=db.collection("DPs").whereEqualTo("uid",mAuth.getUid());
        FirestoreRecyclerOptions<SampleImage> options=new FirestoreRecyclerOptions.Builder<SampleImage>()
                .setQuery(query,SampleImage.class)
                .build();
        adapter=new FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SampleImageViewHolder holder, int position, @NonNull final SampleImage model) {

                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.imgMore.setVisibility(View.VISIBLE);
                holder.imgMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu=new PopupMenu(CreatedDPactivity.this,holder.imgMore);
                        popupMenu.getMenuInflater().inflate(R.menu.photo_menu,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId())
                                {
                                    case R.id.nav_download:
                                        downloadImage(model.getImageUrl());
                                        break;
                                    case R.id.nav_delete:
                                        deleteImage(model.getDpid());
                                        break;
                                    case R.id.nav_share:
                                        shareImage(model.getImageUrl());
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();


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

    private void shareImage(String url) {
        ApplicationClass.shareImage(url,"MyDp",CreatedDPactivity.this);
    }

    private void deleteImage(final String pid) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.do_want_delete_this);
        builder.setTitle(R.string.delete_image);
        builder.setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("DPs").document(pid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mStoreRef.child("image").child("Dps").child(mAuth.getUid()).child(pid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(CreatedDPactivity.this,"deleted",Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(CreatedDPactivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
               dialog.dismiss();
            }
        }).show();

    }

    private void downloadImage(final String  url) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.do_you_want_save_this_image);
        builder.setTitle(R.string.save_image);
        builder.setCancelable(false).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Glide.with(CreatedDPactivity.this)
                        .asBitmap()
                        .load(url)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                saveToLocal(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });



            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).show();

    }
    private void saveToLocal(final Bitmap bitmap) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setMessage(R.string.do_you_want_save_this_image);
//        builder.setTitle(R.string.save_image);
//        builder.setCancelable(false).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Glide.with(CreatedDPactivity.this)
//                        .asBitmap()
//                        .load(url)
//                        .into(new CustomTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//                                saveToLocal(resource);
//                            }
//
//                            @Override
//                            public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                            }
//                        });
//
//
//            }
//        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path= MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"Tittle","null");
if(path!=null)
    Toast.makeText(CreatedDPactivity.this,"Downloaded",Toast.LENGTH_LONG).show();
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
