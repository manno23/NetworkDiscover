package com.example.NetworkDiscover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import static android.net.wifi.WifiManager.*;

/**
 * User: jason
 * Date: 3/04/14
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private final String TAG = "NetworkDiscover";
    private final NetworkDiscoveryActivity mActivity;


    public NetworkChangeReceiver(NetworkDiscoveryActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        Log.d(TAG, "> "+action);
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d(TAG, ">     "+key+": "+extras.get(key).toString());

            }
        }
        WifiManager wifiManager = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE));

        if (action.equals(WIFI_STATE_CHANGED_ACTION)) {
            if (extras.getInt(EXTRA_WIFI_STATE) == WIFI_STATE_ENABLED)
                wifiManager.startScan();
        }

        if (action.equals(SCAN_RESULTS_AVAILABLE_ACTION)) {

            Log.d(TAG, "> Scan Results:");
            boolean targetNetworkFound = false;
            for (ScanResult scanResult: wifiManager.getScanResults())  {
                String result = "\""+scanResult.SSID+"\"";
                Log.d(TAG, "> Scan Results: " + scanResult.SSID);
                if (result.equals(NetworkDiscoveryActivity.ACCESS_POINT_SSID)) {
                    mActivity.connectToTarget();
                    targetNetworkFound = true;
                }
            }

            if (!targetNetworkFound) {
                mActivity.targetNetworkNotFound();
            }
        }

        if (action.equals(SUPPLICANT_STATE_CHANGED_ACTION)) {
            if (extras.getParcelable(EXTRA_NEW_STATE).equals(SupplicantState.COMPLETED)) {

                String connectedSSID = "\""+wifiManager.getConnectionInfo().getSSID()+"\"";
                Log.d(TAG, ">>>>> Supplicant connected to: " + connectedSSID);
                if(connectedSSID.equals(NetworkDiscoveryActivity.ACCESS_POINT_SSID)) {
                    mActivity.connectedToTarget();
                } else {
                    wifiManager.startScan();
                }
            }
        }

    }
}
