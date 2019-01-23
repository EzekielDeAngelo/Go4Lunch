package antho.com.go4lunch.viewmodel;
/** **/
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import antho.com.go4lunch.db.RestaurantApi;
import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.model.RestaurantList;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/** **/
public class RestaurantViewModel extends ViewModel
{
    private String mLocation;
    private MutableLiveData<List<Restaurant>> restaurants;
    private MutableLiveData<List<LatLng>> markerLocation;
    protected DatabaseReference databaseReference;
    private Disposable disposable;
    // Constructor
    public RestaurantViewModel(String location)
    {
        if (restaurants == null)
        {
            restaurants = new MutableLiveData<List<Restaurant>>();
        }
        mLocation = location;
        loadRestaurants(mLocation);
    }
    //
    public LiveData<List<Restaurant>> getRestaurants() { return restaurants; }
    //
    private void loadRestaurants(String location)
    {
        Single<RestaurantList> restaurantCall;
        restaurantCall = RestaurantApi.getInstance().getRestaurants(location);

        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");

        disposable = restaurantCall.subscribeOn(Schedulers.io())
                .subscribe(restaurantList ->
                {
                    for (int i = 0; i < restaurantList.results().size(); i++)
                    {
                        databaseReference.child(restaurantList.results().get(i).placeId()).setValue(restaurantList.results().get(i).placeId());
                        databaseReference.child(restaurantList.results().get(i).placeId()).child("name").setValue(restaurantList.results().get(i).name());
                        Log.d("bonjour",restaurantList.results().get(i).geometry().location().latitude());
                        //LatLng latLng = new LatLng(Double.parseDouble(restaurantList.results().get(i).geometry().location().latitude()), Double.parseDouble(restaurantList.results().get(i).geometry().location().longitude()));
                        //ArrayList<LatLng> restaurants = new ArrayList<LatLng>();
                        //restaurants.add(latLng);
                        // Setting the position for the marker
                        /*MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(restaurantList.results().get(i).name());
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        Marker m = mGoogleMap.addMarker(markerOptions);*/
                    }
                    restaurants.postValue(restaurantList.results());

                });
    }
    //
    @Override
    protected void onCleared()
    {
        if(disposable != null && !disposable.isDisposed())
        {
            disposable.dispose();
            disposable = null;
        }
        super.onCleared();
    }
}
