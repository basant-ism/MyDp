package com.example.mydp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import dialog.SavePhoto;
import model.ApplicationClass;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditPhotoFragment extends Fragment {
    ImageView imageView;
    CircleImageView circleImageView;
    private static Uri imageUri;
    Uri resultUri=null;
    Bitmap resultBitmap=null;
    public EditPhotoFragment(Uri uri) {
        imageUri=uri;

    }
public EditPhotoFragment()
{

}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ApplicationClass.loadLocale(getContext());
        View view=inflater.inflate(R.layout.fragment_edit_photo, container, false);
        imageView=view.findViewById(R.id.image_view);
        imageView.setImageURI(imageUri);
        circleImageView=view.findViewById(R.id.circluer_image_view);
        circleImageView.setVisibility(View.VISIBLE);

        startCroping();
        view.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        view.findViewById(R.id.confirm_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resultBitmap==null)
                {
                    Toast.makeText(getContext(),"crop first",Toast.LENGTH_LONG).show();
                }
                else {
                    SavePhoto dialog=new SavePhoto(getContext(),resultBitmap);
                    dialog.setCancelable(false);
                    dialog.show();
                }

            }
        });
        return view;
    }
    private void startCroping() {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getContext(),this);


//        CropImage.activity(imageUri)
//                .start(this);
//
//// for fragment (DO NOT use `getActivity()`)
//        CropImage.activity()
//                .start(getContext(), this);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 resultUri = result.getUri();
                imageView.setVisibility(View.GONE);
                imageView.setBackgroundColor(getResources().getColor(R.color.black));
                circleImageView.setImageURI(resultUri);
                BitmapDrawable drawable=(BitmapDrawable)circleImageView.getDrawable();
                resultBitmap=getCroppedBitmap(drawable.getBitmap());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.v("TAG",error.getMessage()+"erroe");
            }
        }
    }
        public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
