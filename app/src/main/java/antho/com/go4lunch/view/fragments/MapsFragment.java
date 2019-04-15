package antho.com.go4lunch.view.fragments;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.view.activities.RestaurantDetailsActivity;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import antho.com.go4lunch.viewmodel.factory.ViewModelFactory;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/** Creates fragment to display map from google API **/
public class MapsFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
{
    GoogleMap mMap;
    SupportMapFragment mapFragment;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(45.730518, 4.983453);
    private CameraPosition mCameraPosition;
    private Location defaultLocation = new Location("");

    private Place selectedPlace;
    private RestaurantViewModel restaurantViewModel;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String TAG = "LOCATION";
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    // Required empty public constructor
    public MapsFragment() {}
    // Return new instance of map fragment
    public static MapsFragment newInstance()
        {
        return new MapsFragment();
    }
    // Inflate the layout for this fragment and set up map
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);
        getLocationPermission();
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        else
        {
            defaultLocation.setLatitude(mDefaultLocation.latitude);
            defaultLocation.setLongitude(mDefaultLocation.longitude);
            mLastKnownLocation = defaultLocation;
        }
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        return view;
    }
    // Turn on my location layer, get the current location of the device and set info window click listener
    @Override
    public void onMapReady(GoogleMap map)
    {
        mMap = map;
        updateLocationUI();
        getDeviceLocation();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try
        {
            // Customise the styling of the base map using a JSON object defined in a raw resource file
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
        mMap.setOnInfoWindowClickListener(this);
    }

    // Request location permission to get location of the device
    private void getLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // Handle the result of the location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    // Set the location controls on the map
    private void updateLocationUI()
    {
        if (mMap == null)
        {
            return;
        }
        try
        {
            if (mLocationPermissionGranted)
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else
            {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        }
        catch (SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // Get the best and most recent location of the device, which may be null in rare cases when a location is not available
    private void getDeviceLocation()
    {
        try {
            if (mLocationPermissionGranted)
            {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location)
                    {
                        if (location != null)
                        {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                        else
                        {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                        String parsedLocation = mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
                        restaurantViewModel = ViewModelProviders.of(getActivity(), new ViewModelFactory(parsedLocation)).get("RestaurantViewModel", RestaurantViewModel.class);
                        observeViewModel();
                    }
                });
            }
        }
        catch(SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // Observe viewmodel to set the markers on the map
    private void observeViewModel()
    {

        restaurantViewModel.getPlaces().observe(getActivity(), places ->
        {
            for (int i = 0; i < places.size(); i++)
            {
                Double restaurantLat = Double.parseDouble(places.get(i).lat);
                Double restaurantLng = Double.parseDouble(places.get(i).lng);
                LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(restaurantLatLng);
                markerOptions.title(places.get(i).name());

                if (places.get(i).selectedBy != null && places.get(i).selectedBy.size() > 0)
                    markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_content_enabled));
                else
                    markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_content));
                // Placing a marker on the touched position
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(places.get(i).placeId);

            }
            mMap.setOnMarkerClickListener(marker ->
            {
                restaurantViewModel.loadPlace((String) marker.getTag());
                restaurantViewModel.getPlace().observe(getActivity(), place ->
                {
                    if (selectedPlace != place)
                        selectedPlace = place;
                });
                return false;
            });
        });

    }
    // Create marker bitmap from drawable content
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId)
    {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_marker_frame);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0 +20, 0 +15, vectorDrawable.getIntrinsicWidth() -20, vectorDrawable.getIntrinsicHeight()-25);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*2, bitmap.getHeight()*2, false);
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap);
    }
    // Launch restaurant details activity on marker info window click
    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
        intent.putExtra("id", (selectedPlace.placeId));
        intent.putExtra("photo", selectedPlace.thumb);
        intent.putExtra("name", selectedPlace.name());
        intent.putExtra("address", selectedPlace.address());
        intent.putExtra("phone", selectedPlace.phone());
        intent.putExtra("website", selectedPlace.website());
        intent.putExtra("like", selectedPlace.like);
        intent.putExtra("selected", selectedPlace.selected);
        startActivity(intent);
        marker.hideInfoWindow();
    }
    // Save the map's camera position and the device location
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (mMap != null)
        {
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
}
