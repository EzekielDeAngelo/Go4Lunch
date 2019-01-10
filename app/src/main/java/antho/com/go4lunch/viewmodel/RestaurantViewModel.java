package antho.com.go4lunch.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import antho.com.go4lunch.db.repository.FirebaseDatabaseRepository;
import antho.com.go4lunch.db.repository.RestaurantRepository;
import antho.com.go4lunch.model.Restaurant;

public class RestaurantViewModel extends ViewModel
{
    private MutableLiveData<List<Restaurant>> restaurants;
    private RestaurantRepository repository = new RestaurantRepository();
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
        repository.removeListener();
    }
    //
    private void loadRestaurants()
    {
        repository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Restaurant>()
        {
            @Override
            public void onSuccess(List<Restaurant> result)
            {
            }

            @Override
            public void onError(Exception e)
            {
                restaurants.setValue(null);
            }
        });
    }
}
