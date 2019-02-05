package antho.com.go4lunch.model.restaurant.places;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class PlacePhotos
{
    @Json(name="photo_reference")
    public abstract String url();

    public static JsonAdapter<PlacePhotos> jsonAdapter(Moshi moshi) {
        return new AutoValue_PlacePhotos.MoshiJsonAdapter(moshi);
    }
}
