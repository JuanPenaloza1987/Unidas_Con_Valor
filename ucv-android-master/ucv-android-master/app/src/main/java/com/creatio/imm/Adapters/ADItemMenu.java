package com.creatio.imm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.creatio.imm.ItemsMenu;
import com.creatio.imm.Objects.OCupons;
import com.creatio.imm.R;

import java.util.ArrayList;


/**
 * Created by Layge on 18/12/2017.
 */

public class ADItemMenu extends BaseAdapter {
    Context context;
    ArrayList<OCupons> data = new ArrayList<>();
    ItemsMenu activity;


    public ADItemMenu(Context context, ArrayList<OCupons> data, ItemsMenu activity) {
        this.context = context;
        this.data = data;
        this.activity = activity;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.ad_item_menu, viewGroup, false);

        return item;
    }
}
