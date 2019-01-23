package antho.com.go4lunch.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Restaurant
{
    @Json(name="name")
    public abstract String name();
    @Json(name="place_id")
    public abstract String placeId();
    @Json(name="geometry")
    public abstract RestaurantGeometry geometry();

    public static JsonAdapter<Restaurant> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_Restaurant.MoshiJsonAdapter(moshi);
    }
}
/*    public Restaurant()
    {}

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }*/

