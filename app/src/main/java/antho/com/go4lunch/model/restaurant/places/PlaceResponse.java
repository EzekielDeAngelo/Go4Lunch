package antho.com.go4lunch.model.restaurant.places;
import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
/** Data model for place response object from google places API **/
@AutoValue
public abstract class PlaceResponse
{
    @Json(name="result")
    public abstract Place result();
    // Creates a Moshi adapter for this data model
    public static JsonAdapter<PlaceResponse> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_PlaceResponse.MoshiJsonAdapter(moshi);
    }
}


