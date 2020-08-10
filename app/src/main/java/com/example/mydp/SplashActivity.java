package com.example.mydp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;
//import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
//import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;

import authantication.UserLoginActivity;
import model.ApplicationClass;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(SplashActivity.this);
        mAuth= FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences("MyAppTheme",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isDark",false))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_splash);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPreferences=getSharedPreferences("Setting",MODE_PRIVATE);
                ApplicationClass.LANGUAGE_MODE=sharedPreferences.getString("language","en");

                FirebaseModelManager modelManager=FirebaseModelManager.getInstance();
                FirebaseTranslateRemoteModel hiModel=new FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.HI).build();
                FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
                modelManager.download(hiModel,conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v("GGG","yes");
                        FirebaseUser user=mAuth.getCurrentUser();
                        if(user!=null)
                        {
                            Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent=new Intent(SplashActivity.this, UserLoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseUser user=mAuth.getCurrentUser();
                        if(user!=null)
                        {
                            Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent=new Intent(SplashActivity.this, UserLoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    }
                });

            }
        },3000);
    }
}
