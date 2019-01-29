package antho.com.go4lunch.model.places;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class PlacesPhotos
{
    @Json(name="photo_reference")
    public abstract String url();

    public static JsonAdapter<PlacesPhotos> jsonAdapter(Moshi moshi) {
        return new AutoValue_PlacesPhotos.MoshiJsonAdapter(moshi);
    }
}
