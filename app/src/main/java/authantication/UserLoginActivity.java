package authantication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydp.MainActivity;
import com.example.mydp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import dialog.CustumProgressDialog;
import dialog.ResetPasswordDialog;
import model.ApplicationClass;


public class UserLoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN =123 ;
    FirebaseAuth mAuth;
    EditText etUserEmail,etUserPassword;

    private GoogleSignInClient mGoogleSignInClient;
    CallbackManager mcallbackManager;

    CustumProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(UserLoginActivity.this);
        setContentView(R.layout.activity_user_login);
        mAuth= FirebaseAuth.getInstance();
        etUserEmail=findViewById(R.id.et_user_email);
        etUserPassword=findViewById(R.id.et_user_password);
        dialog=new CustumProgressDialog(this);
        mcallbackManager=CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    public  void goToRegister(View view)
    {
        startActivity(new Intent(UserLoginActivity.this,UserRegisterActivity.class));
    }
    public  void googleLogin(View view)
    {
        dialog.startProgressBar("signIn...");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_CANCELED)
        {

            Toast.makeText(UserLoginActivity.this,"try again...",Toast.LENGTH_LONG).show();
            dialog.stopProgressBar();
            return;
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {


                Toast.makeText(UserLoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                dialog.stopProgressBar();
            }
        }
        else {

            mcallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(UserLoginActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(UserLoginActivity.this,"try again...",Toast.LENGTH_LONG).show();
                            dialog.stopProgressBar();

                        }


                    }
                });
    }
    public  void facebookLogin(View view) {
        dialog.startProgressBar(getString(R.string.sign_in));
        LoginManager.getInstance().logInWithReadPermissions(UserLoginActivity.this, Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleAccessFacbookTocken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                 Toast.makeText(UserLoginActivity.this,"try again!...",Toast.LENGTH_LONG).show();
                 dialog.stopProgressBar();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(UserLoginActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                dialog.stopProgressBar();
            }
        });
    }


    private void handleAccessFacbookTocken(AccessToken accessToken) {
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent=new Intent(UserLoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else
                {

                    dialog.stopProgressBar();
                    Toast.makeText(UserLoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public  void userLogin(View view) {
        final String userEmail = etUserEmail.getText().toString();
        final String userPassword = etUserPassword.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(UserLoginActivity.this, "Email can't be empty", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(UserLoginActivity.this, "Password can't be empty", Toast.LENGTH_LONG).show();
        }
        else {
            dialog.startProgressBar(getString(R.string.loging_in));
            mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                      if(mAuth.getCurrentUser().isEmailVerified())

                       {
                            Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(intent);
                       }
                       else {


                           dialog.stopProgressBar();
                           Toast.makeText(UserLoginActivity.this, "verify email first", Toast.LENGTH_LONG).show();
                       }

                    }
                    else
                    {
                        dialog.stopProgressBar();
                        Toast.makeText(UserLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
    public  void resetPassword(View view)
    {
        ResetPasswordDialog dialog=new ResetPasswordDialog(this);
        dialog.startProgressBar();
    }


}




