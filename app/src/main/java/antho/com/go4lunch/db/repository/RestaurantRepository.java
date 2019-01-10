package antho.com.go4lunch.db.repository;

import antho.com.go4lunch.db.mapper.RestaurantMapper;
import antho.com.go4lunch.model.Restaurant;

public class RestaurantRepository extends FirebaseDatabaseRepository<Restaurant>
{
    public RestaurantRepository()
    {
        super(new RestaurantMapper());
    }

    @Override
    protected String getRootNode()
    {
        return "articles";
    }
}