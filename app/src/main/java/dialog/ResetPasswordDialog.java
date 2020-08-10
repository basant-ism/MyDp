package dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mydp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import model.ApplicationClass;

public class ResetPasswordDialog
{
    Activity activity;
    AlertDialog dialog;
    public  ResetPasswordDialog(Activity activity)
    {
        this.activity=activity;
    }
    public  void startProgressBar()
    {
        ApplicationClass.loadLocale(activity);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setCancelable(false);

        View view=inflater.inflate(R.layout.get_email_dialog,null);
        final EditText etEmail=view.findViewById(R.id.et_user_email);
        TextView tvCancel,tvSumbit;
        tvCancel=view.findViewById(R.id.tv_cancel);
        tvSumbit=view.findViewById(R.id.tv_sumbit);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
         tvSumbit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final String email=etEmail.getText().toString();
                 if(TextUtils.isEmpty(email))
                 {
                     Toast.makeText(activity,"enter email id",Toast.LENGTH_LONG).show();

                 }
                 else
                 {
                     FirebaseAuth mAuth= FirebaseAuth.getInstance();
                     mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful())
                             {

                                 EmailVerificationDialog emailVerificationDialog=new EmailVerificationDialog(activity);
                                 emailVerificationDialog.startProgressBar("We have sent you a password reset link on your email "+email);

                             }
                             else
                             {
                                 Toast.makeText(activity,"try again",Toast.LENGTH_LONG).show();
                             }
                         }
                     });
                 }
             }
         });
        builder.setView(view);
        dialog=builder.create();
        dialog.show();
    }
    public void stopProgressBar()
    {
        if(dialog!=null)
            dialog.dismiss();
    }
}
