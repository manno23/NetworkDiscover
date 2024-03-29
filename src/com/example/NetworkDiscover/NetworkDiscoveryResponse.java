package com.example.NetworkDiscover;

/**
 * User: jason
 * Date: 3/04/14
 */
public interface NetworkDiscoveryResponse {

    /**
     * Called when the wifi has been enabled.
     *
     */
    public void checkForTarget();

    /**
     * Called when a scan has found our target access point.
     *
     */
    public void connectToTarget();

    /**
     * Called when we have successfully connected to the target access point.
     * May be called more than once.
     *
     */
    public void connectedToTarget();

    /**
     * Called when the target access point was not found in the scan.
     * May be called more than once.
     *
     */
    public void targetNetworkNotFound();
}
