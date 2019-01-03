package antho.com.go4lunch.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import antho.com.go4lunch.MainActivity;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapsFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    SupportMapFragment mapFragment;
    @BindView(R.id.button) Button button;
    @BindView(R.id.my_location_button) Button myLocation;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation2 = new LatLng(45.743340, 4.97210);
    private final LatLng mDefaultLocation = new LatLng(45.730518, 4.983453);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition mCameraPosition;

    private String TAG = "LOCATION";
    GoogleMap mMap;
    // Required empty public constructor
    public MapsFragment() {}
    //
    public static MapsFragment newInstance()
    {
        return new MapsFragment();
    }
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null)
        {
           mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null)
        {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        mGeoDataClient = Places.getGeoDataClient(getContext());
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                //.enableAutoManage(getActivity(), (GoogleApiClient.OnConnectionFailedListener) this)
                .build();

        return view;
    }
    //
    @Override
    public void onMapReady(GoogleMap map)
    {
        mMap = map;
        // Check for location permission
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        enableMyLocation();
        updateLocationUI();
        getDeviceLocation();

        mMap.setMyLocationEnabled(true);

        myLocation.setOnClickListener(v ->
        {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 18);
            mMap.animateCamera(cameraUpdate);

        });
        button.setOnClickListener(v ->
        {
            markRestaurants();
        });

        mMap.setOnPoiClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions().position(mDefaultLocation).title("Home Marker"));
        try
        {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
            if (!success)
            {
                Log.e(TAG, "Style parsing failed.");
            }
        }
        catch (Resources.NotFoundException e)
        {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        // Position the map's camera near default location
        map.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));

    }
    //
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,            @NonNull String permissions[],            @NonNull int[] grantResults) {
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
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null)
                        {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else
                        {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            //Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // Show toast details when points of interest are clicked // OPTIONAL FEATURE
    @Override
    public void onPoiClick(PointOfInterest poi) {
        /*Toast.makeText(getContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();*/
        Marker poiMarker = mMap.addMarker(new MarkerOptions()
                .position(poi.latLng)
                .title(poi.name));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    //
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
    //
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    // Return fragment layout
    @Override
    public int layoutRes()
    {
        return R.layout.fragment_maps;
    }

    String placeId;
    //
    private void markRestaurants()
    {
        if (mMap == null)
        {
            return;
        }
        if (mLocationPermissionGranted)
        {
            AutocompleteFilter pf = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).setTypeFilter(79).build();
            LatLngBounds defaultBounds = new LatLngBounds(mDefaultLocation, mDefaultLocation2);
            Task<AutocompletePredictionBufferResponse> placeResult = mGeoDataClient.getAutocompletePredictions("Kebab", defaultBounds, GeoDataClient.BoundsMode.STRICT, pf);

            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Tasks.await(placeResult, 60, TimeUnit.SECONDS);
                    }
                    catch (ExecutionException | InterruptedException | TimeoutException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        AutocompletePredictionBufferResponse autocompletePredictions = placeResult.getResult();
                        Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount() + " predictions." + autocompletePredictions.get(0).getPlaceId());
                        for (int i = 0; i < autocompletePredictions.getCount(); i++)
                        {
                            placeId = autocompletePredictions.get(i).getPlaceId();
                            try
                            {
                                mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<PlaceBufferResponse> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            PlaceBufferResponse places = task.getResult();
                                            Place myPlace = places.get(0);
                                            Log.i(TAG, "Place found: " + myPlace.getName() + myPlace.getAddress());

                                            LatLng placeLat = myPlace.getLatLng();
                                            mMap.addMarker(new MarkerOptions().position(placeLat).title(""));
                                            places.release();
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Place not found.");
                                        }
                                    }
                                });
                            }
                            catch (RuntimeExecutionException e)
                            {
                                // If the query did not complete successfully return null
                                Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error getting autocomplete prediction API call", e);
                                //return null;
                            }
                        }
                    }
                    catch (RuntimeExecutionException e)
                    {
                        // If the query did not complete successfully return null
                        Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error getting autocomplete prediction API call", e);
                        //return null;
                    }
                }
            });
        }
    }
}



/*
//PLACEPICKER
    // A place has been received; use requestCode to track the request.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getContext());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }*/
/*
//PERMISSION
if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
 */