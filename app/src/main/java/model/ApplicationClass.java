package model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.FileProvider;


import com.example.mydp.BuildConfig;
import com.example.mydp.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;

//import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
//import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
//import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import authantication.UserLoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class ApplicationClass
{
    public static FirebaseFirestore db;

    public  static String LANGUAGE_MODE="en";
    public static int TARGET_LANGUAGE_CODE= FirebaseTranslateLanguage.EN;
    public static HashMap<String,Object>translatedData;
    public static void logout(final Context context)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(R.string.do_you_want_lagout);
        builder.setTitle(context.getString(R.string.logout)+" MyDp");
        builder.setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth mAuth= FirebaseAuth.getInstance();
                        mAuth.signOut();
                        if(LoginManager.getInstance()!=null)
                            LoginManager.getInstance().logOut();
                        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(context,gso);
                        if(googleSignInClient!=null)
                            googleSignInClient.signOut();
                        Intent intent1=new Intent(context, UserLoginActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent1);
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    public static void shareImage(String url,final String text,final Context context) {

        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.v("FFF","in");
                Intent i = new Intent(Intent.ACTION_SEND);
                //text
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,text);


                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap,context));
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                context.startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.v("FFF",e.getMessage());

            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.v("FFF","intryin");
            }
        });
    }
    public static Uri getLocalBitmapUri(Bitmap bmp,Context context) {
        Uri bmpUri = null;
        try {
            Log.v("FFF","intry");
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri= FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".provider",file);
            //bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.v("FFF","exc"+e.getMessage());
            e.printStackTrace();
        }
        Log.v("URI","uri"+bmpUri);
        return bmpUri;
    }




    public static void setLocale(Context context,String lang) {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        context.getResources().updateConfiguration(configuration,context.getResources().getDisplayMetrics());
    }
    public static void loadLocale(Context context)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences("Setting",MODE_PRIVATE);
        String language=sharedPreferences.getString("language","en");
        setLocale(context,language);
    }


    public static void setTranslatedDataToMap(final String key,final String text)
   {
        if(text!=null&&!text.equals("")) {
            identifyLanguage(text);
            FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(TARGET_LANGUAGE_CODE)
                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                    .build();
            final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                    .build();

            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            ApplicationClass.translatedData.put(key,s);

                        }
                    });
                }
            });
        }

    }

    private static void identifyLanguage(String text) {
        FirebaseLanguageIdentification languageIdentification=FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentification.identifyLanguage(text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(!s.equals("uid"))
                {
                    getLanguageCode(s);
                }
            }
        });

    }

    private static void getLanguageCode(String s) {
        if(s.equals("hi"))
        {
            TARGET_LANGUAGE_CODE=FirebaseTranslateLanguage.HI;
        }
        else {
            TARGET_LANGUAGE_CODE= FirebaseTranslateLanguage.EN;
        }

    }


    public  static String getLocaleStringResource(Locale requestLocale,int resourceId,Context context)
    {
        String result;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            Configuration configuration=new Configuration(context.getResources().getConfiguration());
            configuration.setLocale(requestLocale);
            result=context.createConfigurationContext(configuration).getText(resourceId).toString();

        }
        else
        {
            Resources resource=context.getResources();
            Configuration configuration=resource.getConfiguration();
            Locale savedLocale=configuration.locale;
            configuration.locale=requestLocale;
            resource.updateConfiguration(configuration,null);
            result=resource.getString(resourceId);
            configuration.locale=savedLocale;
            resource.updateConfiguration(configuration,null);
        }
        return  result;
    }

}
