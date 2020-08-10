package dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.example.mydp.R;

import authantication.UserLoginActivity;
import model.ApplicationClass;

public class EmailVerificationDialog
{
    Activity activity;
    AlertDialog dialog;

    public  EmailVerificationDialog(Activity activity)
    {
        this.activity=activity;
    }
    public  void startProgressBar(String massage)
    {
        ApplicationClass.loadLocale(activity);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setCancelable(false);
        final View view=inflater.inflate(R.layout.email_verification_dialog,null);
        TextView textView=view.findViewById(R.id.tv_check_mail_line);
        textView.setText(massage);
        view.findViewById(R.id.img_cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_go_to_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, UserLoginActivity.class);
                activity.startActivity(intent);
                dialog.dismiss();
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
