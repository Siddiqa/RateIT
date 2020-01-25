package com.smsol.rateit.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class CurrentLocationListener extends LiveData<Location> implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static CurrentLocationListener instance;




    private static Context context;
    private String TAG = "CurrentLocationListener";
    private double userlat = 0.0;
    private double userlng = 0.0;
    private float bearing = 0f;
    private Location location;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 10 * 1000;

    public static CurrentLocationListener getInstance(Context appContext) {
        if (instance == null) {
            context = appContext;
            instance = new CurrentLocationListener(appContext);
        }

        return instance;
    }

    @SuppressLint("MissingPermission")
    private CurrentLocationListener(Context appContext) {

        //CreateLocRequest();
        intialize(context);

    }

    @SuppressLint("MissingPermission")
    public void intialize(final Context context) {
        Log.e(TAG, "intialize: ");
        try {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
                googleApiClient.registerConnectionCallbacks(this);
                googleApiClient.connect();
            }

            if (locationRequest == null) {
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                locationRequest.setInterval(UPDATE_INTERVAL);
                locationRequest.setFastestInterval(FASTEST_INTERVAL);

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

                // Create LocationSettingsRequest object using location request
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);
                builder.setNeedBle(true);
                LocationSettingsRequest locationSettingsRequest = builder.build();
                SettingsClient settingsClient = LocationServices.getSettingsClient(context);
                Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);
                task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                            Log.e(TAG, "onComplete: " + response.getLocationSettingsStates().isLocationPresent());
                            // All location settings are satisfied. The client can initialize location
                            // requests here.

                            setcallback();
                            requestLocationUpdate();

                        } catch (ApiException exception) {
                            Log.e(TAG, "onComplete: Exception :  " + exception.getMessage());
                            switch (exception.getStatusCode()) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied. But could be fixed by showing the
                                    // user a dialog.
                                    try {
                                        // Cast to a resolvable exception.
                                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().

                                        resolvable.startResolutionForResult(((AppCompatActivity) context), 1000);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    } catch (ClassCastException e) {
                                        // Ignore, should be an impossible error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.

                                    break;
                                case LocationSettingsStatusCodes.SUCCESS:
                                    Log.e(TAG, "onResult: " + "Success");

                                    setcallback();
                                    requestLocationUpdate();
                                    break;
                            }
                        }
                    }
                });


            }
        } catch (Exception e) {
            Log.e(TAG, "intialize: " + e.getMessage());
        }


    }
   /* @SuppressLint("MissingPermission")
    public void CreateLocRequest() {
        if (userlat == 0.0 || userlng == 0.0) {
            LocationEngineProvider locationEngineProvider = new LocationEngineProvider(context);
            locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
            locationEngine.setPriority(LocationEnginePriority.BALANCED_POWER_ACCURACY);
            locationEngine.setFastestInterval(1000);
            locationEngine.setInterval(1000);
            locationEngine.addLocationEngineListener(this);
            locationEngine.activate();


            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                Log.e(TAG, "CurrentLocationListener: " + lastLocation);
                setValue(lastLocation);
                userlat = lastLocation.getLatitude();
                userlng = lastLocation.getLongitude();
                bearing = lastLocation.getBearing();

            } else {
                locationEngine.addLocationEngineListener(this);
            }

            locationEngine.requestLocationUpdates();

        }
    }*/


    @Override
    protected void onActive() {
        super.onActive();
        Log.e(TAG, "onActive: ");
       /* if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationEngine.requestLocationUpdates();*/
        intialize(context);

    }


    @Override
    protected void onInactive() {
        super.onInactive();
        Log.e(TAG, "onInactive: ");
        // instance = null;
      /*  if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
            locationEngine.deactivate();
        }*/


      /*  if (googleApiClient != null && googleApiClient.isConnected()) {
            mFusedLocationClient.flushLocations();
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            googleApiClient.disconnect();
        }*/
    }

    public void onDestroy() {
        instance = null;
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        if (mFusedLocationClient != null && mLocationCallback!=null) {
            mFusedLocationClient.flushLocations();
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

    }

    @Override
    public void onConnected(@NonNull Bundle bundle) {
        Log.e(TAG, "onConnected: ");
        // Permissions ok, we get last location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            onLocationChanged(location);
        }
        setcallback();
        requestLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended: ");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ");

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d(TAG, "onLocationResult: " + location);
            userlat = location.getLatitude();
            userlng = location.getLongitude();
            bearing = location.getBearing();
            this.location = location;
            setValue(location);
        }

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


    public double getUserlat() {
        return userlat;
    }

    public double getUserlng() {

        return userlng;
    }

    public float getBearing() {
        return bearing;
    }

    public Location getLocation() {
        return location;
    }

    public void setcallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    //   Log.d(TAG, "onLocationResult: " + location.getLatitude() + " : " + location.getLongitude() +" Accuracy "+location.getAccuracy()+" Speed "+location.getSpeed());
                    onLocationChanged(location);
                }

            }


        };

    }


    public void requestLocationUpdate() {
        Log.e(TAG, "requestLocationUpdate: ");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }


}
