package antho.com.go4lunch.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;
@AutoValue
public abstract class RestaurantList
{
    @Json(name="results")
    public abstract List<Restaurant> results();
    // Creates a Moshi adapter for this data
    public static JsonAdapter<RestaurantList> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_RestaurantList.MoshiJsonAdapter(moshi);
    }
}


