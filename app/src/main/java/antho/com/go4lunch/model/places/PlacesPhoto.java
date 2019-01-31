package antho.com.go4lunch.model.places;


import android.media.Image;
import android.widget.ImageView;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

//@AutoValue
public abstract class PlacesPhoto
{
    @Json(name="image")
    public abstract Image[] photo();

   /* public static JsonAdapter<PlacesPhoto> jsonAdapter(Moshi moshi) {
        return new AutoValue_PlacesPhoto.MoshiJsonAdapter(moshi);
    }*/
}
