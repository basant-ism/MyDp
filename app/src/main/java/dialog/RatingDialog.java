package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mydp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


import model.AppRatingClass;
import model.ApplicationClass;
import model.RatingClass;


public class RatingDialog extends Dialog {
    public Context context;
   RatingBar ratingBar;
    private Button btnCancel,btnUpdate;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String SUID=null;

    public RatingDialog(@NonNull Context context) {
        super(context);
        this.context=context;


    }
    public RatingDialog(@NonNull Context context,String SUID) {
        super(context);
        this.context=context;
        this.SUID=SUID;


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rating_dialog_layout);

        ratingBar=findViewById(R.id.rating_bar);
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
                 sendRating();
            }


        });



    }
    private void   sendRating() {
        final float ratings =ratingBar.getRating();;

        if(ratings<0.5)
        {
            Toast.makeText(context,"rating can't be zero",Toast.LENGTH_LONG).show();
        }

        else{

            final RatingClass ratingClass=new RatingClass();
            ratingClass.setStar((double) ratings);
            ratingClass.setUid(mAuth.getUid());

                db.collection("app").document("appRatings").collection("ratings")
                        .document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            db.collection("app").document("appRatings").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess( DocumentSnapshot documentSnapshot1) {
                                    AppRatingClass appRatingClass = documentSnapshot1.toObject(AppRatingClass.class);

                                    RatingClass ratingClass1 = documentSnapshot.toObject(RatingClass.class);
                                     appRatingClass.setTotalStar(appRatingClass.getTotalStar() - ratingClass1.getStar() + ratings);


                                    db.collection("app").document("appRatings").set(appRatingClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("app").document("appRatings").collection("ratings")
                                                    .document(mAuth.getUid()).set(ratingClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Thanks again!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else {
                            db.collection("app").document("appRatings").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(final DocumentSnapshot documentSnapshot1) {

                                    if(documentSnapshot.exists()) {
                                        AppRatingClass appRatingClass = documentSnapshot1.toObject(AppRatingClass.class);


                                        appRatingClass.setTotalStar(appRatingClass.getTotalStar() + ratings);
                                        appRatingClass.setNoOfRatings(appRatingClass.getNoOfRatings() + 1);
                                        db.collection("app").document("appRatings").set(appRatingClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                db.collection("app").document("appRatings").collection("ratings")
                                                        .document(mAuth.getUid()).set(ratingClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(context, "Thanks!", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("TAG",e.getMessage());
                    }
                });


          dismiss();

        }

    }
}
