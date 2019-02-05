package antho.com.go4lunch.model.restaurant;


import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class RestaurantLocation
{
    @Json(name="lat")
    public abstract String latitude();
    @Json(name="lng")
    public abstract String longitude();
    public static JsonAdapter<RestaurantLocation> jsonAdapter(Moshi moshi) {
        return new AutoValue_RestaurantLocation.MoshiJsonAdapter(moshi);
    }
}
