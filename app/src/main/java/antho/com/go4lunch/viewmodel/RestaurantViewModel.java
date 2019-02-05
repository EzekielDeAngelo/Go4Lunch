package antho.com.go4lunch.viewmodel;
/** **/
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import antho.com.go4lunch.db.RestaurantApi;
import antho.com.go4lunch.model.restaurant.Restaurant;
import antho.com.go4lunch.model.restaurant.RestaurantResponse;

import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.model.restaurant.places.PlaceResponse;
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
    private MutableLiveData<List<Place>> places;
    private MutableLiveData<List<LatLng>> markerLocation;
    protected DatabaseReference databaseReference;
    private Disposable disposable;
    // Constructor
    public RestaurantViewModel(String location)
    {
        if (restaurants == null)
        {
            restaurants = new MutableLiveData<List<Restaurant>>();
            places = new MutableLiveData<List<Place>>();
        }
        mLocation = location;
        loadRestaurants(mLocation);
    }
    //
    public LiveData<List<Restaurant>> getRestaurants() { return restaurants; }
    public LiveData<List<Place>> getPlaces() { return places; }
    //
    private void loadRestaurants(String location)
    {
        Single<RestaurantResponse> restaurantsCall;
        restaurantsCall = RestaurantApi.getInstance().getRestaurantsId(location);
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        disposable = restaurantsCall.subscribeOn(Schedulers.io())
            .subscribe(restaurantList ->
            {
                List<Place> places = new ArrayList<>();
                for (int i = 0; i < restaurantList.results().size(); i++)
                {
                    databaseReference.child(restaurantList.results().get(i).placeId()).setValue(restaurantList.results().get(i).placeId());
                    Call<PlaceResponse> restaurantCall = RestaurantApi.getInstance().getRestaurants(restaurantList.results().get(i).placeId());
                    restaurantCall.enqueue(new retrofit2.Callback<PlaceResponse>()
                    {
                        @Override
                        public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response)
                        {
                            String url ="https://maps.googleapis.com/maps/api/place/"+"photo?maxwidth=800&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0"+"&photo_reference="+ response.body().result().photos().get(0).url();
                            response.body().result().thumb = url;
                            Place details = response.body().result();
                            places.add(details);
                        }
                        @Override
                        public void onFailure(Call<PlaceResponse> call, Throwable t) { }
                    });
                        databaseReference.child(restaurantList.results().get(i).placeId()).child("name").setValue(restaurantList.results().get(i).name());
                }
                this.places.postValue(places);
                restaurants.postValue(restaurantList.results());
            });
    }
    //
    @Override
    protected void onCleared()
    {
        if (disposable != null && !disposable.isDisposed())
        {
            disposable.dispose();
            disposable = null;
        }
        super.onCleared();
    }
}
