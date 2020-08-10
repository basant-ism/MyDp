package com.example.mydp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.Locale;

import dialog.AboutUsDialog;
import dialog.FeedbackDialog;
import dialog.PrivacyPolicyDialog;
import dialog.RatingDialog;
import dialog.TermsDialog;
import dialog.VersionDialog;
import model.ApplicationClass;
import model.User;

public class SettingsActivity extends AppCompatActivity {
    Switch themeToggle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView tvEnglish,tvPersn,tvSetting,tvChangeLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

ApplicationClass.loadLocale(SettingsActivity.this);

        sharedPreferences=getSharedPreferences("MyAppTheme",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isDark",false))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_settings);

        tvEnglish=findViewById(R.id.tv_english);
        tvPersn=findViewById(R.id.tv_persniloze);
        tvSetting=findViewById(R.id.tv_settings);
        tvChangeLanguage=findViewById(R.id.tv_laguage_change);

        mAuth= FirebaseAuth.getInstance();
db= FirebaseFirestore.getInstance();
        editor=getSharedPreferences("MyAppTheme",MODE_PRIVATE).edit();
        themeToggle=findViewById(R.id.theme_toggle);

        sharedPreferences=getSharedPreferences("MyAppTheme",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isDark",false))
        {
            themeToggle.setChecked(true);

        }
        else
        {
            themeToggle.setChecked(false);


        }
        themeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Log.v("TAG","checked");
                    editor.putBoolean("isDark",true);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                    finish();
                }
                else

                {
                    Log.v("TAG","not checked");
                    editor.putBoolean("isDark",false);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                    finish();
                }
            }
        });


setCity();
    }

    private void setCity() {
        String path= ApplicationClass.LANGUAGE_MODE+"users";
        db.collection(path).document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user=documentSnapshot.toObject(User.class);

            }
        });

    }


    public void goToBack(View view)
    {finish();
        startActivity(new Intent(SettingsActivity.this,MainActivity.class));}
    public void clearCache(View view)
    {
        try {
            File dir = getCacheDir();
           if(deleteDir(dir))
               Toast.makeText(SettingsActivity.this,"cleared",Toast.LENGTH_LONG).show();
           else
               Toast.makeText(SettingsActivity.this,"not cleared",Toast.LENGTH_LONG).show();
        } catch (Exception e) { e.printStackTrace();}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    public void shareApp(View view)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "MyCity");
            String shareMessage = "\n This my app\n\n";
            shareMessage += "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(intent, "choose one"));
        }
        catch (Exception e)
        {
            Log.v("TAG",e.getMessage());
        }
    }
    public void sendFeedback(View view)
    {
        FeedbackDialog dialog=new FeedbackDialog(this);
        dialog.show();
    }
    public void rateUs(View view)
    {
        RatingDialog dialog=new RatingDialog(this);
        dialog.show();
    }

    public void logout(View view)
    {
        ApplicationClass.logout(SettingsActivity.this);
    }
    public void seeAboutUs(View view)
    {
        AboutUsDialog dialog=new AboutUsDialog(this);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void seeVersion(View view)
    {
        VersionDialog dialog=new VersionDialog(this);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void seeTerms(View view)
    {
        TermsDialog dialog=new TermsDialog(this);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void seePrivacyPolicy(View view)
    {
        PrivacyPolicyDialog dialog=new PrivacyPolicyDialog(this);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void changeLanguage(View view)
    {

        PopupMenu popupMenu=new PopupMenu(this,tvChangeLanguage);
        popupMenu.getMenuInflater().inflate(R.menu.language_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.hindi)
                {
                    ApplicationClass.LANGUAGE_MODE="hi";
                    SharedPreferences.Editor editor=getSharedPreferences("Setting",MODE_PRIVATE).edit();
                    editor.putString("language","hi");
                    editor.apply();
                    ApplicationClass.setLocale(SettingsActivity.this,"hi");
                    recreate();
                }
                else if(item.getItemId()==R.id.english)
                {
                    ApplicationClass.LANGUAGE_MODE="en";
                    SharedPreferences.Editor editor=getSharedPreferences("Setting",MODE_PRIVATE).edit();
                    editor.putString("language","en");
                    editor.apply();
                    ApplicationClass.setLocale(SettingsActivity.this,"en");
                    recreate();
                }
                return  true;
            }
        });
        popupMenu.show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(SettingsActivity.this,MainActivity.class));


    }
        private void setLocale(String lang) {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        //
        SharedPreferences.Editor editor=getSharedPreferences("Setting",MODE_PRIVATE).edit();
        editor.putString("language",lang);
        editor.apply();


    }
public  void loadLocale()
{
    SharedPreferences sharedPreferences=getSharedPreferences("Setting",MODE_PRIVATE);
    String language=sharedPreferences.getString("language","en");
    setLocale(language);
}
    public void changeUserCity(View view)
    {
        Intent intent=new Intent(SettingsActivity.this,SetUpProfileActivity.class);

        startActivity(intent);
    }

}
