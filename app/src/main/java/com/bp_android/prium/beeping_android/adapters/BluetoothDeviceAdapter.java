package com.bp_android.prium.beeping_android.adapters;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bp_android.prium.beeping_android.R;

import java.util.ArrayList;

/**
 * Created by Admin on 02.03.2016.
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private final Activity context;
    private final ArrayList<BluetoothDevice> devices;

    public BluetoothDeviceAdapter(Activity context, ArrayList<BluetoothDevice> devices) {
        super(context, R.layout.list_bluetooth_devices_row, devices);
        this.context = context;
        this.devices = devices;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_bluetooth_devices_row, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.bluetoothDevice);
            holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.textView.setText(devices.get(position).getName());
        holder.imageView.setImageResource(R.drawable.picto_bluetooth_air);

        return rowView;
    }
}
