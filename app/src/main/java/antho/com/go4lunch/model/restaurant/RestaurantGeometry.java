package antho.com.go4lunch.model.restaurant;
import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
/** Data model for restaurant geometry objects nested in restaurant data **/
@AutoValue
public abstract class RestaurantGeometry
{
    @Json(name="location")
    public abstract RestaurantLocation location();
    // Creates a Moshi adapter for this data model
    public static JsonAdapter<RestaurantGeometry> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_RestaurantGeometry.MoshiJsonAdapter(moshi);
    }
}
