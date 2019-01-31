package antho.com.go4lunch.db;

import android.media.Image;

import antho.com.go4lunch.model.RestaurantList;
import antho.com.go4lunch.model.places.PlacesPhoto;
import antho.com.go4lunch.model.places.PlacesPhotos;
import antho.com.go4lunch.model.places.PlacesResponse;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantService
{
   @GET("nearbysearch/json?radius=1000&types=restaurant&sensor=true&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0")
   Single<RestaurantList> getRestaurantsId(@Query(value = "location", encoded = true) String location);
   @GET("details/json?fields=name,formatted_phone_number,vicinity,website,photos&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0")
   Call<PlacesResponse> getRestaurants(@Query(value = "placeid", encoded = true) String id);
   @GET("photo?maxwidth=400&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0")
   void getRestaurantPhoto(@Query(value = "photo_reference", encoded = true) String reference);

}
// location=45.730518,4.983453&