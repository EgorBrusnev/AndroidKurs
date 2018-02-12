package com.itrans.kurs.model;


import android.Manifest;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.itrans.kurs.OnLocationChangedListener;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class CurrentLocation implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Location lastLocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private OnLocationChangedListener onLocationChangedListener;

    public CurrentLocation(OnLocationChangedListener onLocationChangedListener){
        this.onLocationChangedListener = onLocationChangedListener;
    }

    public synchronized void buildGoogleApiClient(Context context){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3*1000)
                .setFastestInterval(1*1000);
    }

    public void start(){
        googleApiClient.connect();
    }
    public void stop(){
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                onLocationChangedListener.onLocationChanged(lastLocation);
            }
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation!=null){
            onLocationChangedListener.onLocationChanged(lastLocation);
        }
    }
}
