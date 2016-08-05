package utils;

import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.DeviceMarker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * Created by vaibhavbhalerao on 3/1/16.
 */
public class JsonParser {

    public static void parseDevices(JSONArray devicesJson, Context context) {
        List<Device> zenDevices = new ArrayList<>();
        List<Device> airDevices = new ArrayList<>();
        for (int i = 0; i < devicesJson.length(); i++) {
            try {
                JSONObject device = devicesJson.getJSONObject(i).getJSONObject("attributes");
                String serialNumber = device.getString("serial_number");
                if (serialNumber.startsWith("SN01")) {
                    int picture = -1;
                    try {
                        picture = device.getInt("picture");
                    } catch (JSONException e) {

                    }
                    zenDevices.add(new Device(serialNumber, devicesJson.getJSONObject(i).getInt("id"),
                            device.getString("name"), device.getString("bluetooth_uuid"),
                            device.getString("current_address"), picture));
                } else {
                    int picture = -1;
                    try {
                        picture = device.getInt("picture");
                    } catch (JSONException e) {

                    }
                    airDevices.add(new Device(serialNumber, devicesJson.getJSONObject(i).getInt("id"),
                            device.getString("name"), device.getString("bluetooth_uuid"),
                            device.getString("current_address"), picture));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PrefManager.getInstance(context).setAirDevices(airDevices);
            PrefManager.getInstance(context).setZenDevices(zenDevices);
        }


    }

    public static List<DeviceMarker> parseMarkers(JSONArray jsonArray) {

        List<DeviceMarker> markers = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i).getJSONObject("attributes");
                markers.add(new DeviceMarker(json.getString("name"),
                        json.getJSONObject("position").getDouble("latitude"),
                        json.getJSONObject("position").getDouble("longitude"),
                        json.getInt("picture"), json.getString("current_address")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return markers;
    }

}
