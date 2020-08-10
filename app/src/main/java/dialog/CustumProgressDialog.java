package dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.example.mydp.R;



public class CustumProgressDialog
{
    Activity activity;
    AlertDialog dialog;
    public CustumProgressDialog(Activity activity)
    {
        this.activity=activity;
    }
    public  void startProgressBar(String massage)
    {
        //ApplicationClass.loadLocale((Context)activity);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setCancelable(false);
        View view=inflater.inflate(R.layout.custum_progress_dialog,null);
        TextView tvMassage=view.findViewById(R.id.tv_progress_type);
        tvMassage.setText(massage);
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
