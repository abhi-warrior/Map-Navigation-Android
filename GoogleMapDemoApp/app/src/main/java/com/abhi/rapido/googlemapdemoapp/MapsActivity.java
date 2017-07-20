package com.abhi.rapido.googlemapdemoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.rapido.googlemapdemoapp.model.Route;
import com.abhi.rapido.googlemapdemoapp.model.Steps;
import com.abhi.rapido.googlemapdemoapp.response.GetAddressResponse;
import com.abhi.rapido.googlemapdemoapp.response.GetDrivingDirectionsResponse;
import com.abhi.rapido.googlemapdemoapp.util.RestClient;
import com.abhi.rapido.googlemapdemoapp.util.RetroTestApiEndpointInterface;
import com.abhi.rapido.googlemapdemoapp.util.RouteDecode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static  String SOURCE_PLACE_ID = "";
    private static  String DESTINATION_PLACE_ID = "";
    private static  String CURRENT_LOCATION = "";
    private static  String MAP_API_KEY = "AIzaSyDA3DLCmoa-Ut3rvYqjRuO_K7o1uqruzus";
    private TextView mStartPoint, mEndPoint;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    RetroTestApiEndpointInterface service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mStartPoint = (TextView) findViewById(R.id.start_point_view);
        mEndPoint = (TextView) findViewById(R.id.end_point_view);

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mEndPoint.setOnClickListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
         /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {

                // get user address
                service = RestClient.createService(RetroTestApiEndpointInterface.class);
            Call<GetAddressResponse> call1 = service.getAddress(""+mLastKnownLocation.getLatitude()+","+
                        mLastKnownLocation.getLongitude(),MAP_API_KEY);
            call1.enqueue(new Callback<GetAddressResponse>() {
                                                                                                                 @Override
                                                                                                                 public void onResponse(Call<GetAddressResponse> call, Response<GetAddressResponse> response) {
                                                                                                                     if (response.isSuccessful() && response!=null) {
                                                                                                                         GetAddressResponse getAddressResponse = response.body();
                                                                                                                         Log.d(TAG,"formatted_addressss:"+getAddressResponse.results.get(0).formatted_address);
                                                                                                                         SOURCE_PLACE_ID = getAddressResponse.results.get(0).place_id;
                                                                                                                         mStartPoint.setText(getAddressResponse.results.get(0).formatted_address);
                                                                                                                         CURRENT_LOCATION = getAddressResponse.results.get(0).formatted_address;
                                                                                                                         // Add a marker to current location and move the camera
                                                                                                                         mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                                                                                                                 mLastKnownLocation.getLongitude())).title(CURRENT_LOCATION));
                                                                                                                         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                                                                                                 new LatLng(mLastKnownLocation.getLatitude(),
                                                                                                                                         mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                                                                                                     }else
                                                                                                                         Toast.makeText(MapsActivity.this,R.string.generic_error,Toast.LENGTH_SHORT).show();
                                                                                                                 }

                                                                                                                 @Override
                                                                                                                 public void onFailure(Call<GetAddressResponse> call, Throwable t) {
                                                                                                                     Toast.makeText(MapsActivity.this,R.string.generic_error,Toast.LENGTH_SHORT).show();
                                                                                                                 }
                                                                                                             });

        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.end_point_view:
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Place id: " + place.getId());
                mEndPoint.setText(place.getName());
                DESTINATION_PLACE_ID = place.getId();
                Log.i(TAG, "source Place id: " + SOURCE_PLACE_ID);
                Log.i(TAG, "Destination Place id: " + place.getId());

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(""+place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        place.getLatLng(), 12));

                Call<GetDrivingDirectionsResponse> call2 = service.getDrivingDirections("place_id:" + SOURCE_PLACE_ID, "place_id:" +
                        DESTINATION_PLACE_ID, true,  MAP_API_KEY);
                call2.enqueue(new Callback<GetDrivingDirectionsResponse>() {
                    @Override
                    public void onResponse(Call<GetDrivingDirectionsResponse> call, Response<GetDrivingDirectionsResponse> response) {
                        if (response.isSuccessful() && response!=null) {
                            GetDrivingDirectionsResponse getDrivingDirectionsResponse = response.body();
                            ArrayList<LatLng> routelist = new ArrayList<LatLng>();
                            if(getDrivingDirectionsResponse.getRoutes().size()>0) {
                                for (int i = 0; i < getDrivingDirectionsResponse.getRoutes().size(); i++) {
                                    ArrayList<LatLng> decodelist;
                                    Route routeA = getDrivingDirectionsResponse.getRoutes().get(i);
                                    Log.i(TAG, "Legs length : " + routeA.getLegs().size());
                                    if (routeA.getLegs().size() > 0) {
                                        List<Steps> steps = routeA.getLegs().get(0).getSteps();
                                        Log.i(TAG, "Steps size :" + steps.size());
                                        Steps step;
                                        com.abhi.rapido.googlemapdemoapp.model.Location location;
                                        String polyline;
                                        for (int j = 0; j < steps.size(); j++) {
                                            step = steps.get(j);
                                            location = step.getStart_location();
                                            routelist.add(new LatLng(location.getLat(), location.getLng()));
                                            Log.i(TAG, "Start Location :" + location.getLat() + ", " + location.getLng());
                                            polyline = step.getPolyline().getPoints();
                                            decodelist = RouteDecode.decodePoly(polyline);
                                            routelist.addAll(decodelist);
                                            location = step.getEnd_location();
                                            routelist.add(new LatLng(location.getLat(), location.getLng()));
                                            Log.i(TAG, "End Location :" + location.getLat() + ", " + location.getLng());
                                        }
                                    }
                                }
                           }
                            Log.i(TAG,"routelist size : "+routelist.size());
                            if(routelist.size()>0){
                                PolylineOptions rectLine = new PolylineOptions().width(10).color(
                                        Color.RED);

                                for (int i = 0; i < routelist.size(); i++) {
                                    rectLine.add(routelist.get(i));
                                }
                                // Adding route on the map
                                mMap.addPolyline(rectLine);
                                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).draggable(true));
                            }
                        }else
                            Toast.makeText(MapsActivity.this,R.string.generic_error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<GetDrivingDirectionsResponse> call, Throwable t) {
                        Toast.makeText(MapsActivity.this,R.string.generic_error,Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
