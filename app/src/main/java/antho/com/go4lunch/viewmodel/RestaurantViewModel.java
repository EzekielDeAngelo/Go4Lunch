package antho.com.go4lunch.viewmodel;
/** **/

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import antho.com.go4lunch.db.RestaurantApi;
import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.model.RestaurantList;

import antho.com.go4lunch.model.places.PlacesResponse;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
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
        Single<RestaurantList> restaurantsCall;
        restaurantsCall = RestaurantApi.getInstance().getRestaurantsId(location);
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        disposable = restaurantsCall.subscribeOn(Schedulers.io())
                .subscribe(restaurantList ->
                {
                    for (int i = 0; i < restaurantList.results().size(); i++)
                    {
                        databaseReference.child(restaurantList.results().get(i).placeId()).setValue(restaurantList.results().get(i).placeId());
                        databaseReference.child(restaurantList.results().get(i).placeId()).child("name").setValue(restaurantList.results().get(i).name());
                    }
                    restaurants.postValue(restaurantList.results());

                    Call<PlacesResponse> restaurantCall = RestaurantApi.getInstance().getRestaurants(restaurantList.results().get(0).placeId());

                    restaurantCall.enqueue(new retrofit2.Callback<PlacesResponse>() {
                        @Override
                        public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response)
                        {
                            /*Call restaurantPhoto = RestaurantApi.getInstance().getRestaurantPhoto(response.body().result().photos().get(0).url());
                            restaurantPhoto.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    
                                   // Bitmap pic = BitmapFactory.decodeStream()
                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {

                                }
                            });*/
                        }

                        @Override
                        public void onFailure(Call<PlacesResponse> call, Throwable t) {

                        }
                    });
                });
    }
        /*restaurantCall.enqueue(new retrofit2.Callback<RestaurantList>() {
            @Override
            public void onResponse(Call<RestaurantList> call, Response<RestaurantList> response)
            {
                for (int i = 0; i < response.body().results().size(); i++)
                {

                    databaseReference.child(response.body().results().get(i).placeId()).setValue(response.body().results().get(i).placeId());
                    databaseReference.child(response.body().results().get(i).placeId()).child("name").setValue(response.body().results().get(i).name());
                }
                restaurants.postValue(response.body().results());
            }
            @Override
            public void onFailure(Call<RestaurantList> call, Throwable t)
            {

            }
        });*/

    //
    @Override
    protected void onCleared()
    {
        /*if(disposable != null && !disposable.isDisposed())
        {
            disposable.dispose();
            disposable = null;
        }*/
        super.onCleared();
    }
}
