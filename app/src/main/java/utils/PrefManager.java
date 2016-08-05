package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.bp_android.prium.beeping_android.model.Device;
import com.bp_android.prium.beeping_android.model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public final class PrefManager {

    private static final String NAME = "prium_pref";

    private static final String IS_LOGGED_KEY = "com.vaibhav.prium.is_logged_key";
    private static final String LOGIN_KEY = "com.vaibhav.prium.login_key";
    private static final String PASSWORD_KEY = "com.vaibhav.prium.password_key";
    private static final String FIRST_NAME_KEY = "com.vaibhav.prium.first_name_key";
    private static final String LAST_NAME_KEY = "com.vaibhav.prium.last_name_key";
    private static final String MOBILE_KEY = "com.vaibhav.prium.mobile_key";
    private static final String BIRTHDAY_KEY = "com.vaibhav.prium.birthday_key";
    private static final String PICTURE_ACCOUNT_KEY = "com.vaibhav.prium.picture_account_key";
    private static final String USER_ID_KEY = "com.vaibhav.prium.user_id_key";
    private static final String TOKEN_KEY = "com.vaibhav.prium.token_key";
    private static final String ZEN_DEVICES_KEY = "com.vaibhav.prium.zen_devices_key";
    private static final String AIR_DEVICES_KEY = "com.vaibhav.prium.air_devices_key";

    private final Context context;
    private final SharedPreferences preference;

    private PrefManager(final Context context) {
        this.context = context;
        preference = obtain();

    }

    public static PrefManager getInstance(final Context context) {
        return new PrefManager(context);
    }

    private SharedPreferences obtain() {
        return context.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
    }

    private Editor editor() {
        return preference.edit();
    }

    public void setLogged(final boolean isLogged) {
        editor().putBoolean(IS_LOGGED_KEY, isLogged).commit();
    }

    public boolean isLogged() {
        return obtain().getBoolean(IS_LOGGED_KEY, false);
    }

    public void setLogin(final String login) {
        editor().putString(LOGIN_KEY, login).commit();
    }

    public String getLogin() {
        return obtain().getString(LOGIN_KEY, "");
    }

    public void setToken(final String token) {
        editor().putString(TOKEN_KEY, token).commit();
    }

    public String getToken() {
        return obtain().getString(TOKEN_KEY, "");
    }

    public void setUserId(final int id) {
        editor().putInt(USER_ID_KEY, id).commit();
    }

    public int getUserId() {
        return obtain().getInt(USER_ID_KEY, 0);
    }

    public void setPassword(final String password) {
        editor().putString(PASSWORD_KEY, password).commit();
    }

    public String getPassword() {
        return obtain().getString(PASSWORD_KEY, "");
    }

    public void setFirstNameKey(final String firstNameKey) {
        editor().putString(FIRST_NAME_KEY, firstNameKey).commit();
    }

    public String getFirstNameKey() {
        return obtain().getString(FIRST_NAME_KEY, "");
    }

    public void setLastNameKey(final String lastNameKey) {
        editor().putString(LAST_NAME_KEY, lastNameKey).commit();
    }

    public String getLastNameKey() {
        return obtain().getString(LAST_NAME_KEY, "");
    }

    public void setMobileKey(final String mobileKey) {
        editor().putString(MOBILE_KEY, mobileKey).commit();
    }

    public String getMobileKey() {
        return obtain().getString(MOBILE_KEY, "");
    }

    public void setBirthdayKey(final String birthdayKey) {
        editor().putString(BIRTHDAY_KEY, birthdayKey).commit();
    }

    public String getBirthdayKey() {
        return obtain().getString(BIRTHDAY_KEY, "");
    }

    public void setPicture_account(final int pictureAccountKey) {
        editor().putInt(PICTURE_ACCOUNT_KEY, pictureAccountKey).commit();
    }

    public int getPicture_account() {
        return obtain().getInt(PICTURE_ACCOUNT_KEY, 0);
    }

    public List<Device> getZenDevices() {
        Gson gson = new Gson();
        List<Device> devices = gson.<ArrayList<Device>>fromJson(obtain().getString(ZEN_DEVICES_KEY, ""), new TypeToken<List<Device>>() {
        }.getType());
        if (devices == null) {
            return new ArrayList<Device>();
        } else {
            return devices;
        }
    }

    public void setZenDevices(final List<Device> devices) {
        Gson gson = new Gson();
        String json;
        json = gson.toJson(devices);
        editor().putString(ZEN_DEVICES_KEY, json).commit();
    }

    public List<Device> getAirDevices() {
        Gson gson = new Gson();
        List<Device> devices = gson.<ArrayList<Device>>fromJson(obtain().getString(AIR_DEVICES_KEY, ""), new TypeToken<List<Device>>() {
        }.getType());
        if (devices == null) {
            return new ArrayList<Device>();
        } else {
            return devices;
        }
    }

    public void setAirDevices(final List<Device> devices) {
        Gson gson = new Gson();
        String json;
        json = gson.toJson(devices);
        editor().putString(AIR_DEVICES_KEY, json).commit();
    }

    public void updateAirDevice(Device device) {
        List <Device> devices = getAirDevices();
        Log.i("UPDATE DEVICE", devices.indexOf(device) + "");
        //devices.get(devices.indexOf(device)).setPicture(device.getPicture());
        devices.set(devices.indexOf(device), device);
        setAirDevices(devices);
    }

    public void updateZenDevice(Device device) {
        List <Device> devices = getZenDevices();
        Log.i("UPDATE DEVICE", devices.indexOf(device) + "");
        //devices.get(devices.indexOf(device)).setPicture(device.getPicture());
        devices.set(devices.indexOf(device), device);
        setZenDevices(devices);
    }

    public void addZenDevice(Device device) {
        List<Device> devices = getZenDevices();
        devices.add(device);
        setZenDevices(devices);
    }

    public void addAirDevice(Device device) {
        List<Device> devices = getAirDevices();
        devices.add(device);
        setAirDevices(devices);
    }

}