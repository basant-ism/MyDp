package view_holder;


import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydp.R;

public class SampleImageViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public ImageView imgMore;
    public SampleImageViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.image_sample);
        imgMore=itemView.findViewById(R.id.img_more);
    }
}
