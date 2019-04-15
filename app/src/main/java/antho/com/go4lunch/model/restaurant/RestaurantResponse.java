package antho.com.go4lunch.model.restaurant;
import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;
/** Data model for restaurant response object from google map API **/
@AutoValue
public abstract class RestaurantResponse
{
    @Json(name="results")
    public abstract List<Restaurant> results();
    // Creates a Moshi adapter for this data model
    public static JsonAdapter<RestaurantResponse> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_RestaurantResponse.MoshiJsonAdapter(moshi);
    }
}


