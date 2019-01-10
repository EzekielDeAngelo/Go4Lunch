package antho.com.go4lunch.db.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import antho.com.go4lunch.model.Restaurant;

@IgnoreExtraProperties
public class RestaurantEntity
{
    private String name;

    public RestaurantEntity() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}