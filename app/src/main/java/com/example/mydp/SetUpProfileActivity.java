package com.example.mydp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dialog.CustumProgressDialog;
import model.ApplicationClass;

public class SetUpProfileActivity extends AppCompatActivity {
    private static final int GALLEY = 1;
    CircleImageView imageView;
    EditText editText;
    Button btnSetUp;
    String downloadUrl=null;
    Uri uri=null;
    StorageReference mStore;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(SetUpProfileActivity.this);
        setContentView(R.layout.activity_set_up_profile);
        imageView=findViewById(R.id.img_user);
        editText=findViewById(R.id.et_user_name);
        btnSetUp=findViewById(R.id.btn_set);

        mStore= FirebaseStorage.getInstance().getReference();
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfile();
            }
        });
    }

    private void setProfile() {
        final String name=editText.getText().toString();
        if(uri==null)
        {
            Toast.makeText(SetUpProfileActivity.this,"select image",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(name))
        {
            Toast.makeText(SetUpProfileActivity.this,"enter user name",Toast.LENGTH_LONG).show();
        }
        else
        {
            final CustumProgressDialog dialog=new CustumProgressDialog(SetUpProfileActivity.this);
            dialog.startProgressBar("Set up...");
            final StorageReference mChildRef=mStore.child("image").child("profile");
            mChildRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mChildRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final HashMap<String,Object>hashMap=new HashMap<>();
                            hashMap.put("uimage",uri.toString());
                            hashMap.put("uname",name);
                            ApplicationClass.translatedData=new HashMap<>();
                            ApplicationClass.setTranslatedDataToMap("hname",name);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hashMap.putAll(ApplicationClass.translatedData);
                                    db.collection("users").document(mAuth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.stopProgressBar();
                                            Toast.makeText(SetUpProfileActivity.this,"All Sat",Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.stopProgressBar();
                                            Toast.makeText(SetUpProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            },3000);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.stopProgressBar();
                            Toast.makeText(SetUpProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }

    }

    private void openGallery() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,GALLEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==GALLEY)
            {
                uri=data.getData();
                imageView.setImageURI(data.getData());
            }

        }
        else
        {
            Log.v("TAG","result cancel");
        }
    }
    public void goToBack(View view)
    {
       finish();
    }
}
