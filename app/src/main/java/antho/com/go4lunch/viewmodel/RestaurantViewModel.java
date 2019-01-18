package antho.com.go4lunch.viewmodel;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import antho.com.go4lunch.db.RestaurantApi;
import antho.com.go4lunch.db.repository.FirebaseDatabaseRepository;
import antho.com.go4lunch.db.repository.RestaurantRepository;
import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.model.RestaurantList;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RestaurantViewModel extends ViewModel
{
    private MutableLiveData<List<Restaurant>> restaurants;
    private RestaurantRepository repository = new RestaurantRepository();
    protected DatabaseReference databaseReference;
    private Disposable disposable;
    //
    public LiveData<List<Restaurant>> getRestaurants()
    {
        if (restaurants == null)
        {
            restaurants = new MutableLiveData<List<Restaurant>>();
            loadRestaurants();
        }
        return restaurants;
    }
    //
    @Override
    protected void onCleared()
    {
        //repository.removeListener();
    }
    //
    private void loadRestaurants()
    {
        Single<RestaurantList> restaurantCall;
        restaurantCall = RestaurantApi.getInstance().getRestaurants();

        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");

        disposable = restaurantCall.subscribeOn(Schedulers.io())
                .subscribe(restaurantList ->
                {
                    for (int i = 0; i < restaurantList.results().size(); i++) {

                        databaseReference.child(restaurantList.results().get(i).placeId()).setValue(restaurantList.results().get(i).placeId());
                        databaseReference.child(restaurantList.results().get(i).placeId()).child("name").setValue(restaurantList.results().get(i).name());
                    }
                    restaurants.postValue(restaurantList.results());
            /*        repository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Restaurant>()
                    {
                        @Override
                        public void onSuccess(List<Restaurant> result) {
             /*   Restaurant restaurant = new Restaurant();
                restaurant.setName("Restaurant1");
                result.add(restaurant);
                restaurant = new Restaurant();
                restaurant.setName("Restaurant2");
                result.add(restaurant);
                restaurant = new Restaurant();
                restaurant.setName("Restaurant3");
                result.add(restaurant);*/


                    //restaurants.setValue(result);
  /*                      }

                        @Override
                        public void onError(Exception e)
                        {
                            restaurants.setValue(null);
                        }
                    });*/
                });


    }
}
