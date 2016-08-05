package com.bp_android.prium.beeping_android.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import com.bp_android.prium.beeping_android.R;
import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.Item;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import utils.PrefManager;

/**
 * Created by Vaibhav on 2/16/16.
 */
public class CustomGridViewAdapter extends ArrayAdapter<Device>

{
    ImageLoader imageLoader;
    Context context;
    int layoutResourceId;
    List<Device> data;

    public CustomGridViewAdapter(Context context, int layoutResourceId, List<Device> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        //this.data.add(new Device("Add more")); //change this later with needed string character
        imageLoader = ImageLoader.getInstance();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        Device item = data.get(position);

        holder.txtTitle.setText(item.getName());

        if (item.getPicture() == -2) {
            holder.imageItem.setImageResource(R.drawable.btn_ajouter_beeping);
        } else {
            if (item.getPicture() != -1) {
                imageLoader.displayImage("http://beepingsscb.devprium.com/api/pictures/" + item.getPicture() +
                        "?type=thumbnail&access_token=" + PrefManager.getInstance(context).getToken(), holder.imageItem);
            } else {
                holder.imageItem.setImageResource(R.color.black_transparent);
            }
        }

        return row;
    }

    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;
    }

}
