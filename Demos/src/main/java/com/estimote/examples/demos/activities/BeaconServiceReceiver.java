/*
 * Copyright (c) TravelBird, 2015
 * All rights reserved
 */
package com.estimote.examples.demos.activities;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.util.Log;

        import com.estimote.sdk.Beacon;
        import com.estimote.sdk.BeaconManager;
        import com.estimote.sdk.Region;

        import java.util.ArrayList;
        import java.util.List;

public class BeaconServiceReceiver extends BroadcastReceiver {
    BeaconManager beaconManager;
    private final static String BEACON_BLUEBERRY = "blueberry";
    private final static String BEACON_ICE = "ice";
    private final static String BEACON_MINT = "mint";

    private Beacon mBlueberry = new Beacon("B9407F30-F5F8-466E-AFF9-25556B57FE6D", BEACON_BLUEBERRY, null, 27249, 35095, 0, 0);
    private Beacon mIce = new Beacon("B9407F30-F5F8-466E-AFF9-25556B57FE6D", BEACON_ICE, null, 46235, 34332, 0, 0);
    private Beacon mMint = new Beacon("B9407F30-F5F8-466E-AFF9-25556B57FE6D", BEACON_MINT, null, 33182, 7826, 0, 0);
    private List<Beacon> beaconList;


    public void onReceive(Context context, Intent intent) {
        beaconManager = new BeaconManager(context);
        beaconList = new ArrayList<>(3);
        beaconList.add(mBlueberry);
        beaconList.add(mIce);
        beaconList.add(mMint);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> rangedBeacons) {
                Beacon foundBeacon = null;
                for(Beacon rangedBeacon : rangedBeacons) {
                    for(Beacon supportedBeacon : beaconList) {
                        if(rangedBeacon.getMajor() == supportedBeacon.getMajor() && rangedBeacon.getMinor() == supportedBeacon.getMinor()) {
                            foundBeacon = rangedBeacon;

                            switch (foundBeacon.getName()) {
                                case BEACON_BLUEBERRY:
                                    Log.i("TRAVELBIRD-BEACON",BEACON_BLUEBERRY+ " found!");
                                    break;

                                case BEACON_ICE:
                                    Log.i("TRAVELBIRD-BEACON",BEACON_ICE+ " found!");

                                    break;

                                case BEACON_MINT:
                                    Log.i("TRAVELBIRD-BEACON",BEACON_MINT+ " found!");

                                    break;
                            }
                        }
                    }
                }
            }
        });

    }
}