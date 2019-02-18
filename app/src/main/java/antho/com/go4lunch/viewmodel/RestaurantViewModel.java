package antho.com.go4lunch.viewmodel;
/** **/

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import antho.com.go4lunch.db.RestaurantApi;
import antho.com.go4lunch.model.restaurant.Restaurant;
import antho.com.go4lunch.model.restaurant.RestaurantResponse;

import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.model.restaurant.places.PlaceResponse;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/** **/
public class RestaurantViewModel extends ViewModel
{
    private String mLocation;
    private MutableLiveData<List<Restaurant>> restaurants;
    private MutableLiveData<List<Place>> places;
    private MutableLiveData<List<LatLng>> markerLocation;
    private MutableLiveData<Place> place;
    private List<String> likedBy = new ArrayList<>();
    protected DatabaseReference databaseReference;
    private Disposable disposable;
    // Constructor
    public RestaurantViewModel(){}
    public RestaurantViewModel(String location)
    {
        //if (restaurants == null)
        {
            restaurants = new MutableLiveData<List<Restaurant>>();
            places = new MutableLiveData<List<Place>>();
            place = new MutableLiveData<Place>();
        }
        mLocation = location;
        loadRestaurants(mLocation);
    }
    //
    //public LiveData<List<Restaurant>> getRestaurants() { return restaurants; }
    public LiveData<List<Place>> getPlaces() { return places; }
    public LiveData<Place> getPlace(String id) {  loadPlace(id); return place;}
    public void loadPlace(String id)
    {
        //Place place = restaurants.getValue().
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        ChildEventListener childEventListener2 = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {
            likedBy = new ArrayList<>();
            if ((boolean) dataSnapshot.getValue() == true)
            {
                likedBy.add(dataSnapshot.getKey());
                Log.d("added", String.valueOf(likedBy.size()));
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
        databaseReference.child(id).child("likedBy").addChildEventListener(childEventListener2);
        //likedBy.add(databaseReference.child(id).child("likedBy").child(mFirebaseUser.getUid()).getKey());
         Call<PlaceResponse> restaurantCall = RestaurantApi.getInstance().getRestaurants(id);
         restaurantCall.enqueue(new Callback<PlaceResponse>() {
             @Override
             public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {

                 String url ="https://maps.googleapis.com/maps/api/place/"+"photo?maxwidth=800&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0"+"&photo_reference="+ response.body().result().photos().get(0).url();
                 response.body().result().likedBy = likedBy;
                 if (likedBy.contains(mFirebaseUser.getUid()))
                     response.body().result().like = true;
                 else
                     response.body().result().like = false;
                 response.body().result().thumb = url;
                 response.body().result().placeId = id;

                 Log.d("hihihi",databaseReference.child(id).child("likedBy").child(mFirebaseUser.getUid()).getKey());
                 databaseReference.child(id).child("likedBy").addChildEventListener(childEventListener);
                 //response.body().result().likedBy =
                 //response.body().result().likedBy.add("z");
                 place.postValue(response.body().result());

             }

             @Override
             public void onFailure(Call<PlaceResponse> call, Throwable t) {

             }
         });
    }
    //
    private void loadRestaurants(String location)
    {
        Single<RestaurantResponse> restaurantsCall;
        restaurantsCall = RestaurantApi.getInstance().getRestaurantsId(location);
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        disposable = restaurantsCall.subscribeOn(Schedulers.io())
            .subscribe(restaurantList ->
            {
                List<Place> placesList = new ArrayList<>();
                for (int i = 0; i < restaurantList.results().size(); i++)
                {
                    if (databaseReference.child(restaurantList.results().get(i).id()).getKey() == null)
                    {
                        databaseReference.child(restaurantList.results().get(i).id()).setValue(restaurantList.results().get(i).id());

                    }
                    Call<PlaceResponse> restaurantCall = RestaurantApi.getInstance().getRestaurants(restaurantList.results().get(i).id());
                    String id = restaurantList.results().get(i).id();
                    String lat = restaurantList.results().get(i).geometry().location().latitude();
                    String lng = restaurantList.results().get(i).geometry().location().longitude();
                    List<String> likedBy = new ArrayList<>();ChildEventListener childEventListener2 = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if ((boolean) dataSnapshot.getValue() == true)
                        {
                            likedBy.add(dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                    databaseReference.child(id).child("likedBy").addChildEventListener(childEventListener2);
                    restaurantCall.enqueue(new retrofit2.Callback<PlaceResponse>()
                    {
                        @Override
                        public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response)
                        {

                            String url ="https://maps.googleapis.com/maps/api/place/"+"photo?maxwidth=800&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0"+"&photo_reference="+ response.body().result().photos().get(0).url();
                            response.body().result().thumb = url;
                            response.body().result().likedBy = likedBy;
                            if (likedBy.contains(mFirebaseUser.getUid()))
                                response.body().result().like = true;
                            else
                                response.body().result().like = false;
                            response.body().result().placeId = id;
                            response.body().result().lat = lat;
                            response.body().result().lng = lng;
                            Place details = response.body().result();
                            placesList.add(details);
                            places.postValue(placesList);
                        }
                        @Override
                        public void onFailure(Call<PlaceResponse> call, Throwable t) { }
                    });
                    databaseReference.child(restaurantList.results().get(i).id()).child("name").setValue(restaurantList.results().get(i).name());

                }


                restaurants.postValue(restaurantList.results());
            });

    }
    //
    public void likePlace(FirebaseUser user, String restaurantId)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        databaseReference.child(restaurantId).child("likedBy").child(user.getUid()).setValue(true);
    }
    public void dislikePlace(FirebaseUser user, String restaurantId)
    {

        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        databaseReference.child(restaurantId).child("likedBy").child(user.getUid()).setValue(false);
        //databaseReference.child(restaurantId).child("likedBy").addChildEventListener(childEventListener);
        ;
    }

    //
    @Override
    protected void onCleared()
    {
        if (disposable != null && !disposable.isDisposed())
        {
            disposable.dispose();
            disposable = null;
        }
        super.onCleared();
    }
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();


    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//            Log.d("hohoho", String.valueOf(place.getValue().likedBy.size()));
            if ((boolean) dataSnapshot.getValue() == true)
            {//place.getValue().likedBy.add(mFirebaseUser.getUid());
                place.getValue().like = true;}
            else if ((boolean) dataSnapshot.getValue() == false)
            {//place.getValue().likedBy.remove(mFirebaseUser.getUid());
                place.getValue().like = false;}


        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //if ((boolean) dataSnapshot.getValue() == true)

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
