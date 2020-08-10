package com.example.mydp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import dialog.EditPhotoDialog;
import model.ApplicationClass;

import static android.app.Activity.RESULT_OK;



public class CreateDpFragment extends Fragment {
    private static final int THUMBNAIL_SIZE =100 ;
    private static final int MY_CAMERA =3 ;
    private static final int CAMERA =2 ;
    private static String imageUrl=null;
    private Button btnCapture,btnUpload;
    private ImageView imageView1;
   private ImageView imageView;
    private static int GALLEY=1;
    private Button btnAdd;
    private TextView tvUpload;
    Bitmap finalBitmap=null;
    Uri finalUri=null;
    private ImageView imgCancel;
    TextView tvChangePhoto;
    public CreateDpFragment() {

    }
    public CreateDpFragment(String Url) {
        imageUrl=Url;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ApplicationClass.loadLocale(getContext());
        View view=inflater.inflate(R.layout.fragment_create_dp, container, false);
        imageView=view.findViewById(R.id.image_view);
        btnCapture=view.findViewById(R.id.btn_capture_image);
        btnUpload=view.findViewById(R.id.btn_upload);
        imageView1=view.findViewById(R.id.image_view_2);
        tvUpload=view.findViewById(R.id.tv_upload);
        btnAdd=view.findViewById(R.id.btn_add);
        imgCancel=view.findViewById(R.id.img_cancel);
        tvChangePhoto=view.findViewById(R.id.tv_change_photo);

        tvChangePhoto.setVisibility(View.GONE);
        btnAdd.setVisibility(View.GONE);
        btnUpload.setVisibility(View.VISIBLE);
        tvUpload.setText(R.string.upload_a_photo_to_this_frame);
        imageView.setBackgroundResource(R.drawable.profile);


        Picasso.get().load(imageUrl).into(imageView);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFrame();
            }
        });
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        tvChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        return  view;
    }

    private void captureImage() {
        if(getContext().checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA);
        }
        else
        {
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,CAMERA);
        }
    }

    private void addFrame() {
         finalUri=getImageUri(finalBitmap);
         if(finalUri!=null)
         {
             FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
             transaction.replace(R.id.fragment,new EditPhotoFragment(finalUri));
             transaction.addToBackStack(null);
             transaction.commit();
            // EditPhotoDialog dialog=new EditPhotoDialog(getContext(),finalUri);
         }
         else{
             Log.v("TAG","finalUriNUL");
         }
//        Bitmap bit=getCroppedBitmap(bitmap);
//        imageView1.setImageURI(newUri);
//
//        imageView1.setImageBitmap(bit);
    }

    private void openGallery() {
        if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_CAMERA);
        }
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,GALLEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==GALLEY )
            {
                final Uri uri=data.getData();
                Glide.with(getContext())
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                try {

                                    Bitmap bitmap2= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),uri);//getThumbnail(uri);
                                    final Bitmap sbmp1=getResizedBitmap(resource,resource.getHeight(),resource.getWidth());
                                    final Bitmap sbmp2=getResizedBitmap(bitmap2,resource.getHeight(),resource.getWidth());

                                    finalBitmap= overlay(sbmp2,sbmp1);
                                    imageView.setImageBitmap(finalBitmap);

                                  btnUpload.setVisibility(View.GONE);
                                  btnAdd.setVisibility(View.VISIBLE);
                                  tvChangePhoto.setVisibility(View.VISIBLE);
                                  tvUpload.setText(R.string.add_frame);

                                }
                                catch (Exception e)
                                {
                                    Log.v("TAG","Exception:"+e.getMessage());
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });



            }
            else if(requestCode==CAMERA)
            {

                Glide.with(getContext())
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                try {

                                    Bitmap bitmap2= (Bitmap)data.getExtras().get("data");
                                    //MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),uri);//getThumbnail(uri);
                                    final Bitmap sbmp1=getResizedBitmap(resource,resource.getHeight(),resource.getWidth());
                                    final Bitmap sbmp2=getResizedBitmap(bitmap2,resource.getHeight(),resource.getWidth());

                                    finalBitmap= overlay(sbmp2,sbmp1);
                                    imageView.setImageBitmap(finalBitmap);

                                    btnUpload.setVisibility(View.GONE);
                                    btnAdd.setVisibility(View.VISIBLE);
                                    tvChangePhoto.setVisibility(View.VISIBLE);
                                    tvUpload.setText(R.string.add_frame);

                                }
                                catch (Exception e)
                                {
                                    Log.v("TAG","Exception:"+e.getMessage());
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });



            }


        }
        else
        {
            Log.v("TAG","result cancel");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_CAMERA)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA);
            }
            else
            {
                Toast.makeText(getContext(),"Camera permission denied",Toast.LENGTH_LONG).show();
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
        String path=MediaStore.Images.Media.insertImage(getContext().getContentResolver(),bitmap,"Tittle","null");
        return  Uri.parse(path);
    }

    public  Bitmap overlay(Bitmap bmp1,Bitmap bmp2)
    {
        Bitmap bmoverlay=Bitmap.createBitmap(bmp1.getWidth(),bmp1.getHeight(),bmp1.getConfig());

        Canvas canvas=new Canvas(bmoverlay);
        canvas.drawBitmap(bmp1,new Matrix(),null);
        canvas.drawBitmap(bmp2,new Matrix(),null);
        return bmoverlay;
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
//    public  Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
//        InputStream input = getContext().getContentResolver().openInputStream(uri);
//
//        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
//        onlyBoundsOptions.inJustDecodeBounds = true;
//        onlyBoundsOptions.inDither=true;//optional
//        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
//        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
//        input.close();
//
//        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
//            return null;
//        }
//
//        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
//
//        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
//
//        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
//        bitmapOptions.inDither = true; //optional
//        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
//        input = getContext().getContentResolver().openInputStream(uri);
//        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
//        input.close();
//        return bitmap;
//    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
    //    public Bitmap getCroppedBitmap(Bitmap bitmap) {
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
//                bitmap.getWidth() / 2, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
//        //return _bmp;
//        return output;
//    }
//    public Bitmap getCroppedBitmap(Bitmap bitmap) {
//        int width=800;//bitmap.getWidth();
//        int height=800;//bitmap.getHeight();
//        Log.v("TAG",bitmap.getWidth()+"kk"+bitmap.getHeight());
//        Bitmap output = Bitmap.createBitmap(width,
//                height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, width, height);
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//        canvas.drawCircle(width / 2, height / 2,
//                width / 2, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
//        //return _bmp;
//        return output;
//    }
}
