package shekhutech.helpme;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {

    private ImageButton imgPanicBtn;
    private View relativeView;
    private ObjectAnimator objectAnimator;
    private TextView txtLocation;
    private Location location;
    Intent locationIntent;
    Intent shakeIntent;
    Geocoder geocoder;
    private Button btnStopShake;
    private Button btnStopLocation;
    //Connection to Shake Sensor Service
    ShakeSensor shakeService;
    //boolean mBoundShake;
    ServiceConnection shakeConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //mBoundShake = true;
            Log.i("MainActivity","Shake Service Connected");
            ShakeSensor.LocalBinder shakeBinder = (ShakeSensor.LocalBinder) service;
            shakeService = shakeBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //mBoundShake = false;
            Log.i("MainActivity","Shake Service Disconnected");
        }
    };

    //Connection to Location Service
    LocationTracker mServiceLocation;
    //boolean mBoundLocation;
    ServiceConnection locationConnection= new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //mBoundLocation = true;
            Log.i("MainActivity","Location Service Connected");
            LocationTracker.LocalBinder locationBinder = (LocationTracker.LocalBinder) service;
            mServiceLocation = locationBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //mBoundLocation = false;
            //mService = null;
            Log.i("MainActivity","Location Service Disconnected");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity","onCreate()");
        //initializations
        locationIntent = new Intent(this,LocationTracker.class);
        shakeIntent = new Intent(this,ShakeSensor.class);
        bindService(shakeIntent,shakeConnection,BIND_AUTO_CREATE);
        bindService(locationIntent,locationConnection,BIND_AUTO_CREATE);
        // Get reference of Layout of MainActivity
        relativeView = findViewById(R.id.mainLayout);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        objectAnimator = ObjectAnimator.ofObject(relativeView,"backgroundColor",new ArgbEvaluator(), getResources().getColor(R.color.white), getResources().getColor(R.color.red));
        objectAnimator.setDuration(1000);
        // Get Reference of ImageButton to imgPanicBtn variable
        imgPanicBtn = (ImageButton) findViewById(R.id.imgPanic);
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        //shakeService.initSensor(MainActivity.this);
        imgPanicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objectAnimator.start();
                new GetAddress().execute();

                }
        });
    }

    private class GetAddress extends AsyncTask<String,Address,Address>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            location = mServiceLocation.getLocation(MainActivity.this);
            shakeService.startShakeService();
        }

        @Override
        protected Address doInBackground(String... params) {
            //StringBuilder strLocation = new StringBuilder();
            try {
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = addressList.get(0);
                return address;
            } catch (IOException e) {
                e.printStackTrace();
                //txtLocation.setText("Can't Get Location, make sure you have a working mobile network or GPS");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address) {
            super.onPostExecute(address);
            if(address!=null) {
                txtLocation.setText("You are in " + address.getSubLocality() + " " + address.getLocality());
            }
            else
            {
                txtLocation.setText("Location Get Failed.");
            }
        }
    }
    @Override
    protected void onStart() {
        Log.i("MainActivity","onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity","onStop()");
        //unbindService(shakeConnection);
        //unbindService(locationConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity","onResume()");
        //locationIntent = new Intent(this,LocationTracker.class);
        //bindService(locationIntent,locationConnection,BIND_AUTO_CREATE);
        //shakeIntent = new Intent(this,ShakeSensor.class);
        //bindService(shakeIntent,shakeConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(shakeConnection);
        unbindService(locationConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity","onPause()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this,"Settings will be soon available",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}