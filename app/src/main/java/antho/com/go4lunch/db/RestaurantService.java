package antho.com.go4lunch.db;
import antho.com.go4lunch.model.restaurant.RestaurantResponse;
import antho.com.go4lunch.model.restaurant.places.PlaceResponse;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
/** Instantiate calls to google places API **/
public interface RestaurantService
{
   @GET("nearbysearch/json?radius=1000&types=restaurant&sensor=true&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0")
   Single<RestaurantResponse> getRestaurantsId(@Query(value = "location", encoded = true) String location);
   @GET("details/json?fields=name,formatted_phone_number,vicinity,website,geometry,photos&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0")
   Call<PlaceResponse> getRestaurants(@Query(value = "placeid", encoded = true) String id);
   //@GET("photo?maxwidth=400&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0")
   //void getRestaurantPhoto(@Query(value = "photo_reference", encoded = true) String reference);
}
