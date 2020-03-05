package com.creatio.imm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.creatio.imm.ItemsBusiness;
import com.creatio.imm.ItemsMenu;
import com.creatio.imm.Objects.OBanner;
import com.creatio.imm.R;

import java.util.ArrayList;

/**
 * Created by gerardo on 18/06/18.
 */

public class ADBanner extends PagerAdapter {
    private Context context;
    private ArrayList<OBanner> data = new ArrayList<>();


    public ADBanner(Context context, ArrayList<OBanner> data) {
        this.context = context;
        this.data = data;
        notifyDataSetChanged();   //add here
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(final ViewGroup collection, final int position) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_banner, collection, false);
        ImageView img_offert = item.findViewById(R.id.img_offert);
        TextView txtOffert = item.findViewById(R.id.txtOffert);
        if (data.size() > 0) {
            txtOffert.setText(data.get(position).getButton_text());
            CardView cv = item.findViewById(R.id.cv);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (data.get(position).getDescription()) {

                        case "Link directo":

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(data.get(position).getParams()));
                            context.startActivity(i);
                            break;

                        case "Abrir cupón":

                            Intent intent = new Intent(context, ItemsMenu.class);
                            intent.putExtra("ID_cupon", data.get(position).getParams());
                            intent.putExtra("image", data.get(position).getIcon());
                            context.startActivity(intent);
                            break;

                        case "Abrir negocio":

                            Intent intentb = new Intent(context, ItemsBusiness.class);
                            intentb.putExtra("ID_business", data.get(position).getParams());
                            intentb.putExtra("image", data.get(position).getIcon());
                            intentb.putExtra("name", data.get(position).getName());
                            context.startActivity(intentb);
                            break;

                        default:
                            break;
                    }
                }
            });
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .priority(Priority.HIGH);
            Glide.with(context)
                    .load(data.get(position).getImage())
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            notifyDataSetChanged();
                            return false;
                        }
                    })
                    .into(img_offert);
        }
        collection.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
        notifyDataSetChanged();

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
