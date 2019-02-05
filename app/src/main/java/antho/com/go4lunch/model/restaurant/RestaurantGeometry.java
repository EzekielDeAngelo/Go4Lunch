package antho.com.go4lunch.model.restaurant;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;



@AutoValue
public abstract class RestaurantGeometry
{
    @Json(name="location")
    public abstract RestaurantLocation location();

    public static JsonAdapter<RestaurantGeometry> jsonAdapter(Moshi moshi) {
        return new AutoValue_RestaurantGeometry.MoshiJsonAdapter(moshi);
    }
}
