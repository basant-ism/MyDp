package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;


import com.example.mydp.R;

import model.ApplicationClass;

public class AboutUsDialog extends Dialog {
    ImageView imgCross;
    Context context;
    public AboutUsDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(context);
        setContentView(R.layout.about_us_dialog_layout);
        imgCross = findViewById(R.id.img_cross);
        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
