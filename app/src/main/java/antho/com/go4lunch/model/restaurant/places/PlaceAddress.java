package antho.com.go4lunch.model.restaurant.places;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class PlaceAddress
{
    @Json(name="long_name")
    public abstract String longName ();
    /*@Json(name="short_name")
    public abstract String shortName();
    /*@Json(name="types")
    public abstract String types();*/
    public static JsonAdapter<PlaceAddress> jsonAdapter(Moshi moshi) {
        return new AutoValue_PlaceAddress.MoshiJsonAdapter(moshi);
    }
}
