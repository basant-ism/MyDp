package com.example.mydp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapers.SliderAdapterExample;
import de.hdodenhof.circleimageview.CircleImageView;
import dialog.AboutUsDialog;
import dialog.VersionDialog;
import model.ApplicationClass;
import model.SampleImage;
import model.User;
import view_holder.SampleImageViewHolder;

public class MainActivity extends AppCompatActivity {

    private static final int  MY_CAMERA =3;
    TextView tvLogout;
    private static final int PERMISSTION =2;
    //
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ImageView imgDrawerOpen;

    TextView tvUserNavName,tvUserNavEmail;
    CircleImageView imgUserNav;

   public static FirebaseFirestore db;
    FirebaseAuth mAuth;
    SearchView searchView;


    private static final String TAG = "TAG";
    Bitmap bitmap;
    private static  SliderView sliderView;

    FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder> adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationClass.loadLocale(MainActivity.this);
        setContentView(R.layout.activity_main);

        imgDrawerOpen=findViewById(R.id.btn_three_line);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
    findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ApplicationClass.logout(MainActivity.this);
        }
    });


    if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
    {
        requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA);
    }

        View headerView=navigationView.getHeaderView(0);
        tvUserNavName=headerView.findViewById(R.id.nav_user_name);
        imgUserNav=headerView.findViewById(R.id.nav_img_user);
        tvUserNavEmail=headerView.findViewById(R.id.nav_user_email);


        searchView=findViewById(R.id.search_view);

        imgDrawerOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

        db=FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        setUserDataInNavHeader();



        searchView=findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSearchView(searchView,true);
                Intent intent=new Intent(MainActivity.this,SearchSampleActivity.class);
                EditText editText=(EditText)searchView.findViewById(R.id.search_src_text);
                editText.setEnabled(true);
                startActivityForResult(intent,1);

            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSearchView(searchView,true);
                Intent intent=new Intent(MainActivity.this,SearchSampleActivity.class);
                EditText editText=(EditText)searchView.findViewById(R.id.search_src_text);
                editText.setEnabled(true);
                startActivityForResult(intent,1);
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        recreate();
                        break;
                    case R.id.nav_settings:
                        Intent intent2=new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_logout:
                        ApplicationClass.logout(MainActivity.this);
                        break;

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });



        recyclerView=findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        sliderView=findViewById(R.id.image_slider_bottom);
        setSliderImage(MainActivity.this,sliderView);



    }

//    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
//    {
//        int width = bm.getWidth();
//        int height = bm.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        // create a matrix for the manipulation
//        Matrix matrix = new Matrix();
//        // resize the bit map
//        matrix.postScale(scaleWidth, scaleHeight);
//        // recreate the new Bitmap
//        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
//        return resizedBitmap;
//    }
//
//
//    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
//        int widthLight =200; //bitmap.getWidth();
//        int heightLight =200;// bitmap.getHeight();
//        Log.v("TAG",widthLight+"kk"+heightLight);
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(output);
//        Paint paintColor = new Paint();
//        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);
//
//        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));
//
//        canvas.drawRoundRect(rectF, widthLight / 2 ,heightLight / 2,paintColor);
//
//        Paint paintImage = new Paint();
//        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
//        canvas.drawBitmap(bitmap, 0, 0, paintImage);
//
//        return output;
//    }
//    public  Bitmap overlay(Bitmap bmp1,Bitmap bmp2)
//    {
//        Bitmap bmoverlay=Bitmap.createBitmap(bmp1.getWidth(),bmp1.getHeight(),bmp1.getConfig());
//
//        Canvas canvas=new Canvas(bmoverlay);
//        canvas.drawBitmap(bmp1,new Matrix(),null);
//        canvas.drawBitmap(bmp2,0,0,null);
//        return bmoverlay;
//    }
//    private void storeImage(Bitmap image) {
//        File pictureFile = getOutputMediaFile();
//        if (pictureFile == null) {
//            Log.d("TAG",
//                    "Error creating media file, check storage permissions: ");// e.getMessage());
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
//            fos.close();
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
//        }
//    }
//    private  File getOutputMediaFile(){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + getApplicationContext().getPackageName()
//                + "/Files");
//
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
//        File mediaFile;
//        String mImageName="MI_"+ timeStamp +".jpg";
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//        return mediaFile;
//    }
@Override
protected void onStart() {
    super.onStart();
    Query query=db.collection("wallpapers");
    FirestoreRecyclerOptions<SampleImage> options=new FirestoreRecyclerOptions.Builder<SampleImage>()
            .setQuery(query,SampleImage.class)
            .build();
    adapter=new FirestoreRecyclerAdapter<SampleImage, SampleImageViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull SampleImageViewHolder holder, int position, @NonNull final SampleImage model) {

            Picasso.get().load(model.getImageUrl()).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setWallPaper(model.getImageUrl());

                }
            });
        }



        @NonNull
        @Override
        public SampleImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);
            return new SampleImageViewHolder(view);
        }
    };
    recyclerView.setAdapter(adapter);
    adapter.startListening();

}
    private void setWallPaper(final String imageUrl) {
        if(checkCallingOrSelfPermission(Manifest.permission.SET_WALLPAPER)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.SET_WALLPAPER},PERMISSTION);

        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.do_you_want_set_wallpaper);
        builder.setTitle(R.string.set_wallpaper);
        builder.setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final WallpaperManager wallpaperManager=WallpaperManager.getInstance(getApplicationContext());

                Glide.with(MainActivity.this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    wallpaperManager.setBitmap(resource);
                                }
                                catch (Exception e)
                                {
                                    Log.v("TAG",e.getMessage());
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .show();



    }
    private void enableSearchView(View view,boolean enabled)
    {
        view.setEnabled(enabled);
        if(view instanceof ViewGroup)
        {
            ViewGroup viewGroup=(ViewGroup)view;
            for(int i=0;i<viewGroup.getChildCount();i++)
            {
                View child=viewGroup.getChildAt(i);
                enableSearchView(child,enabled);
            }
        }
    }

    private void setUserDataInNavHeader() {

        db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    User user=documentSnapshot.toObject(User.class);
                  if(user.getUimage()!=null)
                      Picasso.get().load(user.getUimage()).into(imgUserNav);
                  String name=null;
                  if(ApplicationClass.LANGUAGE_MODE.equals("hi"))
                    name=user.getHname();
                    else
                        name=user.getUname();
                    if(name!=null)
                        tvUserNavName.setText(name);
                    if(user.getUemail()!=null)
                        tvUserNavEmail.setText(user.getUemail());

                }
            }
        });

    }
    @Override

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    public  void openDrawer()
    {
        toggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.openDrawer(GravityCompat.START);
        toggle.onDrawerOpened(drawerLayout);
        toggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }


    public void  seeAboutUs(View view)
    {
        AboutUsDialog dialog=new AboutUsDialog(this);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void  contactUs(View view)
    {

    }
    public void  seeVersion(View view)
    {
        VersionDialog dialog=new VersionDialog(this);
        dialog.setCancelable(false);
        dialog.show();
    }
//    public void  logout(View view)
//    {
//ApplicationClass.logout(MainActivity.this);
//    }
    public  static void setSliderImage(final Context context, final SliderView sliderView) {
        db=FirebaseFirestore.getInstance();

        db.collection("advImage").limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<SampleImage> list=new ArrayList<>();
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    list.add(documentSnapshot.toObject(SampleImage.class));
                }

                SliderAdapterExample adapter = new SliderAdapterExample(context,list);

                sliderView.setSliderAdapter(adapter);

                //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.startAutoCycle();
                sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                //sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                // sliderView.setIndicatorSelectedColor(Color.WHITE);
                // sliderView.setIndicatorUnselectedColor(Color.GRAY);
                //sliderView.setScrollTimeInSec(2); //set scroll delay in seconds :


            }
        });
    }
   public void seeDp(View view)
   {
       startActivity(new Intent(MainActivity.this,CreatedDPactivity.class));
   }
    public void createDp(View view)
    {
        startActivity(new Intent(MainActivity.this,SampleActivity.class));
    }
}
