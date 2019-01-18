package antho.com.go4lunch.db;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.model.RestaurantList;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface RestaurantService
{
   @GET("json?location=45.730518,4.983453&radius=1000&types=restaurant&sensor=true&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0")
   Single<RestaurantList> getRestaurants();
}
