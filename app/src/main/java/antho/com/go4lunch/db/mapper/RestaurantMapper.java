package antho.com.go4lunch.db.mapper;

import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.db.entity.RestaurantEntity;

public class RestaurantMapper extends FirebaseMapper<RestaurantEntity, Restaurant> {

    @Override
    public Restaurant map(RestaurantEntity restaurantEntity)
    {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantEntity.getName());
        return restaurant;
    }

}