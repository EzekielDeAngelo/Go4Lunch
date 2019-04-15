package antho.com.go4lunch.model.restaurant;
import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
/** Data model for restaurant objects nested in restaurant response data **/
@AutoValue
public abstract class Restaurant
{
    @Json(name="name")
    public abstract String name();
    @Json(name="place_id")
    public abstract String id();
    @Json(name="geometry")
    public abstract RestaurantGeometry geometry();
    // Creates a Moshi adapter for this data model
    public static JsonAdapter<Restaurant> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_Restaurant.MoshiJsonAdapter(moshi);
    }
}
