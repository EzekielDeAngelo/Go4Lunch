package antho.com.go4lunch.db;
import com.squareup.moshi.Moshi;

import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
/** Initialize database **/
public class RestaurantApi
{
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static Retrofit retrofit;
    private static RestaurantService restaurantService;
    private static Moshi moshi;
    // Creates an instance of restaurant service
    public static RestaurantService getInstance()
    {
        if (restaurantService != null)
        {
            return restaurantService;
        }
        if (retrofit == null)
        {
            initMoshi();
            initRetrofit();
        }
        restaurantService = retrofit.create(RestaurantService.class);
        return restaurantService;
    }
    // Initialize Moshi
    private static void initMoshi()
    {
        moshi = new Moshi.Builder()
                .add(AdapterFactory.create())
                .build();
    }
    // Initialize Retrofit
    private static void initRetrofit()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}