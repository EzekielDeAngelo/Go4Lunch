package antho.com.go4lunch.db;

import com.squareup.moshi.Moshi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RestaurantApi {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private static Retrofit retrofit;
    private static RestaurantService restaurantService;
    private static Moshi moshi;

    public static RestaurantService getInstance() {
        if (restaurantService != null) {
            return restaurantService;
        }
        if (retrofit == null) {
            initMoshi();
            initRetrofit();
        }
        restaurantService = retrofit.create(RestaurantService.class);
        return restaurantService;
    }
    //
    private static void initMoshi()
    {
        moshi = new Moshi.Builder()
                .add(AdapterFactory.create())
                .build();
    }
    //
    private static void initRetrofit()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
//        double mLatitude = mLastKnownLocation.getLatitude();
  //      double mLongitude = mLastKnownLocation.getLongitude();
//        sb.append("location=" + mLatitude + "," + mLongitude);

