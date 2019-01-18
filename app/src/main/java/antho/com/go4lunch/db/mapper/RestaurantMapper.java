package antho.com.go4lunch.db.mapper;

import android.util.Log;

import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.db.entity.RestaurantEntity;

public class RestaurantMapper extends FirebaseMapper<RestaurantEntity, Restaurant> {

    /*@Override
    public Restaurant map(RestaurantEntity restaurantEntity)
    {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantEntity.getName());
        return restaurant;
    }*/
    @Override
    public Restaurant map(RestaurantEntity restaurantEntity)
    {
        Restaurant restaurant = new Restaurant() {

            @Override
            public String name() {
Log.d("test", restaurantEntity.getName());
                return  restaurantEntity.getName();
            }

            @Override
            public String placeId() {
                return restaurantEntity.getId();
            }
        };
        //restaurant.name();
        return restaurant;
    }

}