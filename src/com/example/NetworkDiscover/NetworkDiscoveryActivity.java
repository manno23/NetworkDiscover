package com.example.NetworkDiscover;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


/**
 * User: jason
 * Date: 3/04/14
 */
public class NetworkDiscoveryActivity extends Activity implements NetworkDiscoveryResponse, AssignTargetDialog.AssignTargetListener {

    private final String TAG = "NetworkDiscover";
    public static String ACCESS_POINT_SSID = "THESE WILL BE HARDCODED IN";
    public static String ACCESS_POINT_WPA_KEY = "THESE WILL BE HARDCODED IN";

    private WifiManager wifiManager;
    private NetworkChangeReceiver receiver;
    private WifiConfiguration wifiConfig;
    private IntentFilter filter;
    private AssignTargetDialog dialog;
    private TextView connectionStatusText;

    private boolean receiver_registered = false;



    /* Activity lifecycle handlers */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        connectionStatusText = (TextView)findViewById(R.id.connection_status);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);


        receiver = new NetworkChangeReceiver(this);
        filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, ">>>>> onResume called");
        super.onResume();

        /* Prompt for SSID and passkey */
        dialog = new AssignTargetDialog();
        dialog.show(getFragmentManager(), "String");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, ">>>>> onPause called");
        super.onPause();

        if (dialog.isAdded())
            dialog.dismiss();
        unregisterReceiverIdempotent();
    }



    /* Network Responses */

    @Override
    public void checkForTarget() {
        Log.d(TAG, ">>>>> Scanning for network...");
        connectionStatusText.setText("Scanning for network...");
        wifiManager.startScan();
    }

    @Override
    public void connectToTarget() {
        Log.d(TAG, ">>>>> Network found, attempting to connect...");
        connectionStatusText.setText("Network found, attempting to connect...");
        int networkID = wifiManager.addNetwork(wifiConfig);
        wifiManager.enableNetwork(networkID, true);
    }

    @Override
    public void connectedToTarget() {
        unregisterReceiverIdempotent();
        Log.d(TAG, ">>>>> Connected to the network. This is the endpoint, yay!");
        connectionStatusText.setText("Connected to the network " + ACCESS_POINT_SSID);
    }

    @Override
    public void targetNetworkNotFound() {
        unregisterReceiverIdempotent();
        Log.d(TAG, "    >>>>> Could not find the network " + ACCESS_POINT_SSID);
        connectionStatusText.setText("Could not find the network.");
    }



    /* Dialog Responses */

    @Override
    public void onDialogTargetAssignClick(DialogFragment dialog, String ssid, String sharedKey) {
        ACCESS_POINT_SSID = "\""+ssid+"\"";
        ACCESS_POINT_WPA_KEY = "\""+sharedKey+"\"";
        Log.d(TAG, ">>>>>   " + ACCESS_POINT_SSID + " " + ACCESS_POINT_WPA_KEY);
        dialog.dismiss();
        beginNetworkDiscovery();
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }



    /* Member functions */

    private void beginNetworkDiscovery() {

        /* Create ASUS Network configuration */
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = ACCESS_POINT_SSID;
        wifiConfig.status = WifiConfiguration.Status.DISABLED;
        wifiConfig.priority = 40;

        /* Setup security */
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.preSharedKey = ACCESS_POINT_WPA_KEY;

        /* Turn on wifi if not already */
        if(wifiManager.isWifiEnabled()) {
            /* If it is on we can attempt to connect */
            checkForTarget();
        } else {
            /* If not we will have to enable it and wait for the signal that it has connected to come back */
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            wifiManager.setWifiEnabled(true);
        }

        registerReceiverIdempotent();
    }

    private void unregisterReceiverIdempotent() {
        if (receiver_registered) {
            unregisterReceiver(receiver);
            receiver_registered = false;
            Log.d(TAG, ">>>>> Unregistered receiver.");
        }
    }

    private void registerReceiverIdempotent() {
        if (!receiver_registered) {
            registerReceiver(receiver, filter);
            receiver_registered = true;
            Log.d(TAG, ">>>>> Registered receiver.");
        }
    }


}
