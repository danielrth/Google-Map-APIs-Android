package utils;

import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import java.util.List;

/**
 * Created by Vaibhav on 3/11/16.
 */
public class LocationHelper {

    public static Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location l = mLocationManager.getLastKnownLocation(provider);

                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            } catch (Exception e) {

            }
        }
            return bestLocation;
        }

}
