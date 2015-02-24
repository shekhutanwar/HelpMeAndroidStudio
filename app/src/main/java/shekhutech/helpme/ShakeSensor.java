package shekhutech.helpme;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Samsung on 23-02-2015.
 */
public class ShakeSensor extends Service implements SensorEventListener{

    private IBinder mBinder = new LocalBinder();
    private Vibrator vibrator;
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        if (mAccel > 12)
        {
            System.out.println("Device has been shaked");
            vibrator.vibrate(500);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class LocalBinder extends Binder {
        public ShakeSensor getService(){
            return ShakeSensor.this;
        }
    }
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    public void startShakeService()
    {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        System.out.println("Shake service has been started");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(ShakeSensor.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public void stopShakeService()
    {
        if(mSensorManager!=null)
        {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ShakeSensorService","onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
