package antho.com.go4lunch.view.fragments;
/** **/
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.view.activities.RestaurantDetailsActivity;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import antho.com.go4lunch.viewmodel.ViewModelFactory;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
/** **/
public class MapsFragment extends BaseFragment implements OnMapReadyCallback
{
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    private RestaurantViewModel viewModel;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(45.730518, 4.983453);
    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    private Place selectedPlace;
    private boolean mLocationPermissionGranted;

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String TAG = "LOCATION";
    private CameraPosition mCameraPosition;
    Location defaultLoc = new Location("");
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
        getDeviceLocation();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null)
        {

            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        else
        {

            defaultLoc.setLatitude(mDefaultLocation.latitude);
            defaultLoc.setLongitude(mDefaultLocation.longitude);
            mLastKnownLocation = defaultLoc;
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
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        return view;
    }
    //
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }
    //
    @Override
    public void onMapReady(GoogleMap map)
    {
        mMap = map;
        updateLocationUI();
        getDeviceLocation();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try
        {
            // Customise the styling of the base map using a JSON object defined in a raw resource file.
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
    }
    //
    private void updateLocationUI()
    {
        if (mMap == null)
        {
            return;
        }
        try
        {
            getLocationPermission();
            if (mLocationPermissionGranted)
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else
            {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                defaultLoc.setLatitude(mDefaultLocation.latitude);
                defaultLoc.setLongitude(mDefaultLocation.longitude);
                mLastKnownLocation = defaultLoc;
                mMap.setMyLocationEnabled(true);
            }
        }
        catch (SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // Request location permission, so that we can get the location of the device. The result of the permission request is handled by a callback, onRequestPermissionsResult.
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
    //
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
    public Double lat;
    private Double lng;
    // Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
    private void getDeviceLocation()
    {
        try
        {
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
                            //Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
                            mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
                        }


                        lng = mLastKnownLocation.getLongitude();
                        lat = mLastKnownLocation.getLatitude();
                        String parsedLocation = lat.toString() + "," + lng.toString();
                        viewModel = ViewModelProviders.of(getActivity(), new ViewModelFactory(parsedLocation)).get("RestaurantViewModel", RestaurantViewModel.class);

                        viewModel.getPlaces().observe(getActivity(), new Observer<List<Place>>() {
                            @Override
                            public void onChanged(List<Place> places) {
                                for (int i = 0 ; i < places.size() ; i++)
                                {
                                    Double restaurantLat = Double.parseDouble(places.get(i).lat);
                                    Double restaurantLng = Double.parseDouble(places.get(i).lng);
                                    LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(restaurantLatLng);
                                    markerOptions.title(places.get(i).name());

                                    markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_restaurant_black_24dp));
                                    // Placing a marker on the touched position
                                    Marker m = mMap.addMarker(markerOptions);
                                    m.setTag(places.get(i).placeId);
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            viewModel.getPlace((String) marker.getTag()).observe(getActivity(), place ->
                                            {
                                                 selectedPlace = place;
                                            });
                                            return false;
                                        }
                                    });

                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
                                    {
                                        @Override
                                        public void onInfoWindowClick(Marker marker)
                                        {

                                                Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
                                                intent.putExtra("id", (String) marker.getTag());
                                                intent.putExtra("photo", selectedPlace.thumb);
                                                intent.putExtra("name", selectedPlace.name());
                                                intent.putExtra("address", selectedPlace.address());
                                                intent.putExtra("phone", selectedPlace.phone());
                                                intent.putExtra("website", selectedPlace.website());
                                                intent.putExtra("like", selectedPlace.like);
                                                startActivity(intent);
                                                marker.hideInfoWindow();
                                        }
                                    });

                                }
                            }
                        });
                    }
                });
            }
        }
        catch(SecurityException e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_place_black_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0 +20, 0 +20, vectorDrawable.getIntrinsicWidth() -20, vectorDrawable.getIntrinsicHeight()-20);

        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*2, bitmap.getHeight()*2, false);
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap);
    }

    //
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
    // Show toast details when points of interest are clicked // OPTIONAL FEATURE
    /*@Override
    public void onPoiClick(PointOfInterest poi)
    {
        Toast.makeText(getContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        Marker poiMarker = mMap.addMarker(new MarkerOptions()
                .position(poi.latLng)
                .title(poi.name));
    }*/
    //
    /*@Override
    public boolean onMyLocationButtonClick()
    {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }*/
    //
    /*@Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }*/
}
