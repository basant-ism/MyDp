package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.mydp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import model.ApplicationClass;
import model.Feedback;

public class FeedbackDialog extends Dialog {
    public Context context;
    private EditText etFeedbackMessage;
    private Button btnCancel,btnUpdate;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public FeedbackDialog(@NonNull Context context) {
        super(context);
        this.context=context;


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback_dialog_layout);

         etFeedbackMessage=findViewById(R.id.et_feedback_message);
        btnCancel=findViewById(R.id.btn_cancel);
        btnUpdate=findViewById(R.id.btn_send);


        mAuth= FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 sendFeedback();
            }


        });



    }
    private void  sendFeedback() {

        String feedbackMessage=etFeedbackMessage.getText().toString();
        if(TextUtils.isEmpty(feedbackMessage))
            Toast.makeText(context,"message can't be empty",Toast.LENGTH_LONG).show();
        else{
            Feedback feedback=new Feedback();
            feedback.setMessage(feedbackMessage);
            feedback.setUid(mAuth.getUid());
            if(mAuth.getCurrentUser().getEmail()!=null)
                feedback.setEmail(mAuth.getCurrentUser().getEmail());
          db.collection("feedbacks").document(mAuth.getUid()).collection("appFeedbacks").add(feedback).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
              @Override
              public void onSuccess(DocumentReference documentReference) {

                  Toast.makeText(context,"Feedback sent",Toast.LENGTH_LONG).show();
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(context,"try again",Toast.LENGTH_LONG).show();
              }
          });
          dismiss();

        }

    }
}
