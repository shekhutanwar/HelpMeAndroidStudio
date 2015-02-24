package shekhutech.helpme;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Shekhar on 23-02-2015.
 */
public class LocationTracker extends Service implements LocationListener{

    private IBinder mBinder = new LocalBinder();

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Latitude = " + location.getLatitude());
        System.out.println("Longitude = " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class LocalBinder extends Binder{
        public LocationTracker getService(){
            return LocationTracker.this;
        }
    }
    private Context mContext;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    //private boolean canGetLocation = false;

    Location location;
    //double latitude;
    //double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public Location getLocation(Context context)
    {
        mContext = context;
        try
        {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isGPSEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                return location;
            }
            else if(isNetworkEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                return location;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public void stopLocationTracker(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Location Tracker","Location Service Stopped" );
    }

    @Override
    public IBinder onBind(Intent intent)
    {
       return mBinder;
    }
}
