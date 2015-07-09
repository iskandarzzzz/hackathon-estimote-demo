/*
 * Copyright (c) TravelBird, 2015
 * All rights reserved
 */
package com.estimote.examples.demos.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.examples.demos.R;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

public class WelcomeBird extends Activity {
    private static final String TAG = WelcomeBird.class.getSimpleName();

    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager;
    private RelativeLayout background;
    private TextView accuracy;
    private TextView title;
    private ImageView bird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_bird);
        background = (RelativeLayout) findViewById(R.id.background);
        accuracy = (TextView) findViewById(R.id.accuracy);
        title = (TextView) findViewById(R.id.info_text);
        bird = (ImageView) findViewById(R.id.bird);
        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        getActionBar().setSubtitle("Found beacons: " + beacons.size());
                        // Check if our beacon is in range.
                        boolean found = false;
                        double distance = 10000;
                        bird.clearAnimation();
                        bird.setImageResource(R.drawable.palermo);

                        for ( int i = 0 ; i < beacons.size(); i++) {
                            Beacon beacon = beacons.get(i);
                            if (beacon.getMajor()==46235 && beacon.getMinor()==34332 ) {
                                // found!
                                distance = Utils.computeAccuracy(beacon);
                                String t = String.format(getString(R.string.antonio_close),distance );
                                accuracy.setText(t);
                                found = true;
                            }
                        }
                        if ( found) {
                            //background.setBackgroundColor(0xff369ecc);
                            title.setText(getString(R.string.antonio_in_range));
                            bird.setImageResource(R.drawable.antonio);

                            if (distance<2) {
                                AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                                animation.getFillAfter();
                                animation.setDuration(1000);
                                animation.setRepeatCount(-1);
                                bird.startAnimation(animation);
                            }
                            else {
                            }
                        }
                        else {
                            //background.setBackgroundColor(0xffffffff);
                            accuracy.setText("");
                            title.setText(getString(R.string.welcome_jane));
                            bird.clearAnimation();
                            bird.setImageResource(R.drawable.palermo);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_bird, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    @Override
    protected void onStop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
                getActionBar().setSubtitle("Bluetooth not enabled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {
        getActionBar().setSubtitle("Scanning...");
        //clear values ...
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(WelcomeBird.this, "Cannot start ranging, something terrible happened",
                                   Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }
}
