package dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.example.mydp.CreatedDPactivity;
import com.example.mydp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import model.SampleImage;

public class SavePhoto extends Dialog {
    TextView tvFileSize,tvPercentage,tvQuality,tvImageType;
    ImageView imgDownWard;
    SeekBar seekBar;
    StorageReference mStore;
    FirebaseFirestore db;
    Context context;
    private static Bitmap resultBitmap=null;
    FirebaseAuth mAuth;
    
    //private  static Uri imageUri;
    public SavePhoto(@NonNull Context context, Bitmap bitmap) {
        super(context);
       // imageUri=uri;
        resultBitmap=bitmap;
        this.context=context;
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailog_save);
        tvFileSize=findViewById(R.id.tv_file_size);
        tvPercentage=findViewById(R.id.tv_percentage);
        tvQuality=findViewById(R.id.tv_quailty);
        tvImageType=findViewById(R.id.tv_image_type);
        imgDownWard=findViewById(R.id.img_downward);
        seekBar=findViewById(R.id.seek_bar);

        db=FirebaseFirestore.getInstance();
        mStore= FirebaseStorage.getInstance().getReference();
mAuth=FirebaseAuth.getInstance();

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
        imgDownWard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,imgDownWard);
        popupMenu.getMenuInflater().inflate(R.menu.image_type_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
              tvImageType.setText(item.getTitle());
                if(item.getItemId()==R.id.nav_jpeg)
                {
                    tvImageType.setText("JPEG");
                }
                else
                {
                    tvImageType.setText("PNG");
                }
                return true;
            }
        });
        popupMenu.show();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPercentage.setText(progress+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void saveImage() {
        String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        final String DPid="MyDp_"+date;
        final StorageReference mChildRef=mStore.child("image").child("DPs").child(mAuth.getUid()).child(DPid);
        try{
            final CustumProgressDialog dialog=new CustumProgressDialog((Activity) context);
            dialog.startProgressBar(context.getString(R.string.saving));

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            Bitmap.CompressFormat format;
            if(tvImageType.getText().toString().equals("JPEG"))
                format=Bitmap.CompressFormat.JPEG;
            else
                format=Bitmap.CompressFormat.PNG;
            int quality=25;
            quality=seekBar.getProgress();
            resultBitmap.compress(format,quality,baos);
            byte[]bytes=baos.toByteArray();
Log.v("TAG","1kkkkkk");
            mChildRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.v("TAG","2kkkkkk");
                    mChildRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.v("TAG","3kkkkkk");
                            SampleImage sampleImage=new SampleImage();
                            sampleImage.setImageUrl(uri.toString());
                            sampleImage.setUid(mAuth.getUid());
                            sampleImage.setDpid(DPid);
                                    db.collection("DPs").document(DPid).set(sampleImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.stopProgressBar();
                                            dismiss();
                                            ((Activity) context).finish();
                                            context.startActivity(new Intent(context, CreatedDPactivity.class));

                                            Toast.makeText(context,"saved",Toast.LENGTH_LONG).show();;
                                        }





                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.stopProgressBar();
                                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                                            Log.v("TAG",e.getMessage());
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.stopProgressBar();
                    Log.v("TAG","k"+e.getMessage());
                }
            });
        }
        catch (Exception e)
        {


            Log.v("TAG","kkll"+e.getMessage());
        }


    }
}
