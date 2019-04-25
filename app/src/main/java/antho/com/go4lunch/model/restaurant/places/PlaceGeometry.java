package antho.com.go4lunch.model.restaurant.places;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/** Data model for place geometry object nested in place data **/
@AutoValue
public abstract class PlaceGeometry
{
    @Json(name="location")
    public abstract PlaceLocation location();
    // Creates a Moshi adapter for this data model
    public static JsonAdapter<PlaceGeometry> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_PlaceGeometry.MoshiJsonAdapter(moshi);
    }
}
