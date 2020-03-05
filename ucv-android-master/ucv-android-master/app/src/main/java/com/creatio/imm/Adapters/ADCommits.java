package com.creatio.imm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.creatio.imm.Objects.OCommit;
import com.creatio.imm.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Layge on 18/12/2017.
 */

public class ADCommits extends BaseAdapter {
    Context context;
    ArrayList<OCommit> data = new ArrayList<>();


    public ADCommits(Context context, ArrayList<OCommit> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.item_commit, viewGroup, false);
        TextView txtName, txtCommit;
        final CircleImageView img;
        RatingBar rtBarSpe;

        txtName = item.findViewById(R.id.txtName);
        txtCommit = item.findViewById(R.id.txtCommit);
        img = item.findViewById(R.id.img);
        rtBarSpe = item.findViewById(R.id.rtBarSpe);

        rtBarSpe.setRating(Float.parseFloat(data.get(i).getRate()));

        txtName.setText(data.get(i).getName_user());
        txtCommit.setText(data.get(i).getCommit());
        return item;
    }
}
