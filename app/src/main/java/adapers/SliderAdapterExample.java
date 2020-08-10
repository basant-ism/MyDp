package adapers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mydp.R;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import model.SampleImage;


public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

        private Context context;
        private List<SampleImage> mSliderItems;

        public SliderAdapterExample(Context context, List<SampleImage>list) {
            this.context = context;
            mSliderItems=list;
        }

        public void renewItems(List<SampleImage> sliderItems) {
            this.mSliderItems = sliderItems;
            notifyDataSetChanged();
        }

        public void deleteItem(int position) {
            this.mSliderItems.remove(position);
            notifyDataSetChanged();
        }

        public void addItem(SampleImage sliderItem) {
            this.mSliderItems.add(sliderItem);
            notifyDataSetChanged();
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent,false);
            return new SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

            SampleImage sliderItem = mSliderItems.get(position);



Picasso.get().load(sliderItem.getImageUrl()).into(viewHolder.imageAdv);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getCount() {
            //slider view count could be dynamic size
            return mSliderItems.size();
        }

        public class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            View itemView;
            public ImageView imageAdv;



            public SliderAdapterVH(View itemView) {
                super(itemView);
                 imageAdv=itemView.findViewById(R.id.image_sample);

                this.itemView = itemView;
            }
        }

    }

