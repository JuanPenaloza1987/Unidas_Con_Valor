package com.creatio.imm.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.Objects.OGallery;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class ADGallery extends PagerAdapter {
    private Context context;
    private ArrayList<OGallery> data = new ArrayList<>();


    public ADGallery(Context context, ArrayList<OGallery> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_gallery, collection, false);
        ImageView img = item.findViewById(R.id.img);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(data.get(position).getName_server())
                .apply(options)
                .into(img);
        collection.addView(item);

        return item;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return data.get(position).getID();
    }

}
