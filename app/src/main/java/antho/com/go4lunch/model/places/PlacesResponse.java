package antho.com.go4lunch.model.places;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class PlacesResponse
{
    @Json(name="result")
    public abstract PlacesDetails result();
    // Creates a Moshi adapter for this data
    public static JsonAdapter<PlacesResponse> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_PlacesResponse.MoshiJsonAdapter(moshi);
    }
}


