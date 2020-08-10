package authantication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mydp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import dialog.CustumProgressDialog;
import dialog.EmailVerificationDialog;
import dialog.PrivacyPolicyDialog;
import dialog.TermsDialog;

import model.ApplicationClass;

public class UserRegisterActivity extends AppCompatActivity   {


    EditText etUserName,etUserEmail,etUserPassword;

    CustumProgressDialog custumProgressDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(UserRegisterActivity.this);
        setContentView(R.layout.activity_user_register);


        etUserName=findViewById(R.id.et_user_name);
        etUserEmail=findViewById(R.id.et_user_email);
        etUserPassword=findViewById(R.id.et_user_password);

        custumProgressDialog=new CustumProgressDialog(UserRegisterActivity.this);
        mAuth= FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();


    }

    public void userRegistration(View view)
    {
          final String userName=etUserName.getText().toString();
        final String userEmail=etUserEmail.getText().toString();
       final String userPassword=etUserPassword.getText().toString();

        if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(UserRegisterActivity.this,"Name can't be empty",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(userEmail))
        {
            Toast.makeText(UserRegisterActivity.this,"Email can't be empty",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(userPassword))
        {
            Toast.makeText(UserRegisterActivity.this,"Password can't be empty",Toast.LENGTH_LONG).show();
        }

        else
        {
            custumProgressDialog.startProgressBar(getString(R.string.create_account));
            mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ApplicationClass.translatedData = new HashMap<>();
                                    ApplicationClass.setTranslatedDataToMap("hname", userName);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("uname", userName);
                                            hashMap.put("uemail", userEmail);
                                            hashMap.putAll(ApplicationClass.translatedData);
                                            db.collection("users").document(mAuth.getUid()).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.v("SIZ",ApplicationClass.translatedData.size()+"size");
                                                    custumProgressDialog.stopProgressBar();
                                                    EmailVerificationDialog dialog=new EmailVerificationDialog(UserRegisterActivity.this);
                                                    dialog.startProgressBar(getString(R.string.verification_email)+userEmail);


                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            mAuth.getCurrentUser().delete();
                                                            custumProgressDialog.stopProgressBar();
                                                            Log.v("Error", e.getMessage());
                                                            Toast.makeText(UserRegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                        }
                                    }, 3000);


                                }
                            }
                        });
                        }
                    else{
                        custumProgressDialog.stopProgressBar();

                        Toast.makeText(UserRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void goToBack(View view)
    {
        finish();
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
}
