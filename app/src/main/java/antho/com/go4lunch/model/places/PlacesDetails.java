package antho.com.go4lunch.model.places;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class PlacesDetails
{
    @Json(name="name")
    public abstract String name();
    @Json(name="vicinity")
    public abstract String adress();
    @Json(name="website")
    public abstract String website();
    @Json(name="formatted_phone_number")
    public abstract String phone();
    @Json(name="photos")
    public abstract List<PlacesPhotos> photos();
    // Creates a Moshi adapter for this data
    public static JsonAdapter<PlacesDetails> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_PlacesDetails.MoshiJsonAdapter(moshi);
    }
}


