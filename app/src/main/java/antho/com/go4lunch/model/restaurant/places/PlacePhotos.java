package antho.com.go4lunch.model.restaurant.places;
import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
/** Data model for place photo object nested in place data **/
@AutoValue
public abstract class PlacePhotos
{
    @Json(name="photo_reference")
    public abstract String url();
    public abstract int height();
    public abstract int width();
    // Creates a Moshi adapter for this data model
    public static JsonAdapter<PlacePhotos> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_PlacePhotos.MoshiJsonAdapter(moshi);
    }
}
