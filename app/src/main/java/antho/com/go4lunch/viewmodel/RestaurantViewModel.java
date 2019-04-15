package antho.com.go4lunch.viewmodel;
import android.app.Application;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import antho.com.go4lunch.model.workmate.Workmate;
import antho.com.go4lunch.view.fragments.MapsFragment;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/** Viewmodel for restaurant data **/
public class RestaurantViewModel extends ViewModel
{
    private String mLocation;
    private MutableLiveData<List<Restaurant>> restaurants;
    private MutableLiveData<List<Place>> places;
    private MutableLiveData<List<LatLng>> markerLocation;
    private MutableLiveData<Place> place;
    private List<String> likedBy = new ArrayList<>();
    private List<String> selectedBy = new ArrayList<>();
    protected DatabaseReference databaseReference;
    private Disposable disposable;
    // Constructor
    public RestaurantViewModel(){}
    public RestaurantViewModel(String location)
    {
        restaurants = new MutableLiveData<List<Restaurant>>();
        place = new MutableLiveData<Place>();
        mLocation = location;
        loadRestaurants(mLocation);
    }
    // Return places from google places API
    public LiveData<List<Place>> getPlaces() { return places; }
    // Return a place from google places API based on id
    public LiveData<Place> getPlace()
    {

        return place;
    }
    //
    public void loadPlace(String id)
    {

        selectedBy = new ArrayList<>();
        likedBy = new ArrayList<>();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        databaseReference.child(id).child("likedBy").addChildEventListener(childEventListenerLikeSwitch);
        databaseReference.child(id).child("selectedBy").addChildEventListener(childEventListenerSelectSwitch);

        Call<PlaceResponse> restaurantCall = RestaurantApi.getInstance().getRestaurants(id);
        restaurantCall.enqueue(new Callback<PlaceResponse>()
        {
             @Override
             public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response)
             {
                 String photoUrl;
                 for (int i = 0; i < response.body().result().photos().size(); i++)
                     if (response.body().result().photos().get(i).height() > 3000 & response.body().result().photos().get(i).width() > 4000)
                     {
                         photoUrl = response.body().result().photos().get(i).url();
                         String url ="https://maps.googleapis.com/maps/api/place/"+"photo?maxwidth=800&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0"+"&photo_reference="+ photoUrl;//response.body().result().photos().get(0).url();
                         response.body().result().thumb = url;
                         break;
                     }

                 response.body().result().likedBy = likedBy;
                 response.body().result().selectedBy = selectedBy;
                 if (response.body().result().selectedBy.contains(mFirebaseUser.getUid()))
                     response.body().result().selected = true;
                 else
                     response.body().result().selected = false;
                 if (likedBy.contains(mFirebaseUser.getUid()))
                     response.body().result().like = true;
                 else
                     response.body().result().like = false;

                 response.body().result().placeId = id;
                 place.postValue(response.body().result());

             }
             @Override
             public void onFailure(Call<PlaceResponse> call, Throwable t)
             {
             }
         });
    }
    //
    private void loadRestaurants(String location)
    {
        places = new MutableLiveData<List<Place>>();
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

                    databaseReference.child(id).child("likedBy").addChildEventListener(childEventListenerLikeSwitch);
                    databaseReference.child(id).child("selectedBy").addChildEventListener(childEventListenerSelectSwitch);
                    restaurantCall.enqueue(new retrofit2.Callback<PlaceResponse>()
                    {
                        @Override
                        public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response)
                        {
                            String photoUrl;
                            for (int i = 0; i < response.body().result().photos().size(); i++)
                                if (response.body().result().photos().get(i).height() > 3000 & response.body().result().photos().get(i).width() > 4000)
                                {
                                    photoUrl = response.body().result().photos().get(i).url();
                                    String url ="https://maps.googleapis.com/maps/api/place/"+"photo?maxwidth=800&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0"+"&photo_reference="+ photoUrl;//response.body().result().photos().get(0).url();
                                    response.body().result().thumb = url;
                                    break;
                                }
                            response.body().result().likedBy = likedBy;
                            if (likedBy.contains(mFirebaseUser.getUid()))
                                response.body().result().like = true;
                            else
                                response.body().result().like = false;
                            if (selectedBy.contains(mFirebaseUser.getUid()))
                                response.body().result().selected = true;
                            else
                                response.body().result().selected = false;
                            response.body().result().placeId = id;
                            response.body().result().lat = lat;
                            response.body().result().lng = lng;
                            Place details = response.body().result();
                            placesList.add(details);
                            places.postValue(placesList);
                        }
                        @Override
                        public void onFailure(Call<PlaceResponse> call, Throwable t) {}
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
    }
    public void selectPlace(FirebaseUser user, String restaurantId)
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("workmate").child(user.getUid()).child("restaurantId");

        db.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String previousSelectedRestaurant = (String) dataSnapshot.getValue();
                    if (previousSelectedRestaurant != restaurantId)
                    {
                        databaseReference.child(previousSelectedRestaurant).child("selectedBy").child(user.getUid()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        databaseReference.child(restaurantId).child("selectedBy").child(user.getUid()).setValue(true);

        //databaseReference.child(restaurantId).child("selectedBy").child(user.getUid()).push();
    }
    public void deselectPlace(FirebaseUser user, String restaurantId)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        databaseReference.child(restaurantId).child("selectedBy").child(user.getUid()).removeValue();//.setValue(false);
        //databaseReference.child(restaurantId).child("selectedBy").removeValue();
    }

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    Place placeData;
    ChildEventListener childEventListenerLikeSwitch = new ChildEventListener()
    {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {
            likedBy = new ArrayList<>();
            if ((boolean) dataSnapshot.getValue() == true)
            {
                likedBy.add(dataSnapshot.getKey());
            }
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {

        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
        {
            likedBy = new ArrayList<>();
            if ((boolean) dataSnapshot.getValue() == true)
            {
                likedBy.add(dataSnapshot.getKey());
            }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };
    ChildEventListener childEventListenerSelectSwitch = new ChildEventListener()
    {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {
            selectedBy = new ArrayList<>();
            if ((boolean) dataSnapshot.getValue() == true)
            {
                Log.d("onChildAddedSelection:" , dataSnapshot.getKey());
                selectedBy.add(dataSnapshot.getKey());
            }
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };
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
}
