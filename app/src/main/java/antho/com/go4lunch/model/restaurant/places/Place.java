package antho.com.go4lunch.model.restaurant.places;
/** **/
import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;
/** **/
@AutoValue
public abstract class Place
{
    @Json(name="name")
    public abstract String name();
    //@Json(name="place_id")
    public String placeId;
    public String lat;
    public String lng;
    /*@Json(name="geometry")
    public abstract PlaceGeometry geometry();*/
    @Json(name="vicinity")
    public abstract String address();
    @Json(name="website")
    public abstract String website();
    @Json(name="formatted_phone_number")
    public abstract String phone();
    @Json(name="photos")
    public abstract List<PlacePhotos> photos();
    public String thumb;
    public List<String> likedBy;
    public boolean like;
    public int stars;
    // Creates a Moshi adapter for this data
    public static JsonAdapter<Place> jsonAdapter(Moshi moshi)
    {
        return new AutoValue_Place.MoshiJsonAdapter(moshi);
    }
}


