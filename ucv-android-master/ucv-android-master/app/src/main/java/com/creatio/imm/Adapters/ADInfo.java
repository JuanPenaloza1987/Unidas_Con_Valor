package com.creatio.imm.Adapters;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creatio.imm.Objects.OCommit;
import com.creatio.imm.Objects.OInfo;
import com.creatio.imm.R;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gerardo on 20/06/18.
 */

public class ADInfo extends BaseAdapter {
    Context context;
    ArrayList<OInfo> data = new ArrayList<>();
    ArrayList<OCommit> data_commit = new ArrayList<>();

    public ADInfo(Context context, ArrayList<OInfo> data, ArrayList<OCommit> data_commit) {
        this.context = context;
        this.data = data;
        this.data_commit = data_commit;
    }


    @Override
    public int getCount() {
        return data.size() + data_commit.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (position < data.size()) {
            final View item = inflater.inflate(R.layout.item_info, parent, false);
            TextView txtName = item.findViewById(R.id.txtName);
            TextView txtDesc = item.findViewById(R.id.txtDesc);
            TextView txtPhone = item.findViewById(R.id.txtPhone);
            TextView txtHorario = item.findViewById(R.id.txtHorario);
            ImageView img = item.findViewById(R.id.img);

            img.setImageResource(data.get(position).getIcon());
            String phone = data.get(position).getPhone();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                String formattedNumber = PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry());
                txtPhone.setText(formattedNumber);
            } else {
                String formattedNumber = PhoneNumberUtils.formatNumber(phone);
                txtPhone.setText(formattedNumber);
            }

            txtHorario.setText(data.get(position).getHorario());

            txtName.setText(data.get(position).getName());
            txtDesc.setText(data.get(position).getDescription());

            return item;

        } else {
            final View item = inflater.inflate(R.layout.item_commit, parent, false);
            TextView txtName, txtCommit, txtCom;
            final CircleImageView img;
            RatingBar rtBarSpe;

            txtName = item.findViewById(R.id.txtName);
            txtCom = item.findViewById(R.id.txtCom);
            txtCommit = item.findViewById(R.id.txtCommit);
            img = item.findViewById(R.id.img);
            rtBarSpe = item.findViewById(R.id.rtBarSpe);
            txtCom.setVisibility(View.GONE);
            if (position == data.size()){
                txtCom.setVisibility(View.VISIBLE);
            }
            rtBarSpe.setRating(Float.parseFloat(data_commit.get(position - data.size()).getRate()));

            txtName.setText(data_commit.get(position - data.size()).getName_user());
            txtCommit.setText(data_commit.get(position - data.size()).getCommit());
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override(1000, 1000)
                    .priority(Priority.HIGH);
            Glide.with(context)
                    .load("https://icupon.app/Data/" + data_commit.get(position - data.size()).getImage_user())
                    .apply(options)
                    .into(img);
            return item;
        }
    }
}
