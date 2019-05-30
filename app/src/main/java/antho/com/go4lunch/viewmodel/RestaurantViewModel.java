package antho.com.go4lunch.viewmodel;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import antho.com.go4lunch.db.RestaurantApi;
import antho.com.go4lunch.db.utilities.FirebaseQueryLiveData;
import antho.com.go4lunch.model.restaurant.Restaurant;
import antho.com.go4lunch.model.restaurant.RestaurantResponse;

import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.model.restaurant.places.PlaceResponse;
import durdinapps.rxfirebase2.RxFirebaseChildEvent;
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
 //   private MutableLiveData<List<Restaurant>> restaurants;
    private MutableLiveData<List<Place>> mPlaces;
    private MutableLiveData<Place> mPlace;
    private String mLocation;
    private Disposable disposable;
    private static final String firebaseUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private static final DatabaseReference RESTAURANTS_REF = FirebaseDatabase.getInstance().getReference("restaurants");
    private static final int photoMinWidth = 4000;
    private static final int photoMinHeight = 3000;

    private final FirebaseQueryLiveData restaurantLiveData = new FirebaseQueryLiveData(RESTAURANTS_REF);
    /*private final LiveData<Place> placeLiveData = Transformations.map(restaurantLiveData, new Deserializer());

    private class Deserializer implements Function<DataSnapshot, Place>
    {
        @Override
        public Place apply(DataSnapshot dataSnapshot)
        {
            return dataSnapshot.getValue(Place.class);
        }
    }

    @NonNull
    public LiveData<Place> getPlaceLiveData()
    {
        return placeLiveData;
    }*/
    //
    @NonNull
    public LiveData<DataSnapshot> getRestaurantDataSnapshotLiveData()
    {
        return restaurantLiveData;
    }
    List<String> selectedBy = new ArrayList<>();
    List<String> likedBy = new ArrayList<>();
    // Constructor
    public RestaurantViewModel(){}
    public RestaurantViewModel(String location)
    {
        //restaurants = new MutableLiveData<>();
        mPlace = new MutableLiveData<Place>();
        mLocation = location;
    }
    // Return MutableLiveData for testing purpose
    public MutableLiveData<List<Place>> getMutableLiveData() { return mPlaces; }
    // Return places from google places API
    public LiveData<List<Place>> getPlaces()
    {
        if (mPlaces == null)
        {
            mPlaces = new MutableLiveData<List<Place>>();
            loadRestaurants(mLocation);
        }
        return mPlaces;
    }
    // Return a place from google places API based on id
    public LiveData<Place> getPlace(String id)
    {
        loadPlace(id, place -> mPlace.postValue(place));
        return mPlace;
    }
    //
    public void loadPlace(String id, PlaceCallback placeCallback)
    {
        selectedBy = new ArrayList<>();
        likedBy = new ArrayList<>();

        RESTAURANTS_REF.child(id).child("likedBy").addChildEventListener(childEventListenerLikeSwitch);
        RESTAURANTS_REF.child(id).child("selectedBy").addChildEventListener(childEventListenerSelectSwitch);

        Call<PlaceResponse> restaurantCall = RestaurantApi.getInstance().getRestaurants(id);

        restaurantCall.enqueue(new Callback<PlaceResponse>()
        {
             @Override
             public void onResponse(@NonNull Call<PlaceResponse> call, @NonNull Response<PlaceResponse> response)
             {
                 Place place = response.body().result();
                 for (int i = 0; i < Objects.requireNonNull(place.photos().size()); i++)
                     if (place.photos().get(i).height() > photoMinHeight & place.photos().get(i).width() > photoMinWidth)
                     {
                         String photoUrl = place.photos().get(i).url();
                         place.photoUrl = "https://maps.googleapis.com/maps/api/place/"+"photo?maxwidth=800&key=AIzaSyCqjpzrT9vnrz1BPfgloK1CsGTR9q7-sX0"+"&photo_reference="+ photoUrl;
                         break;
                     }
                 place.placeId = id;
                 place.likedBy = likedBy;
                 place.selectedBy = selectedBy;
                 place.selected = selectedBy.contains(Objects.requireNonNull(firebaseUserId));
                 place.like = likedBy.contains(firebaseUserId);
                 place.likeCount = likedBy.size();
                 placeCallback.getPlace(place);
                 mPlace.postValue(place);
             }
             @Override
             public void onFailure(@NonNull Call<PlaceResponse> call, @NonNull Throwable t)
             {

             }
         });
    }
    //
    private void loadRestaurants(String location)
    {
        Single<RestaurantResponse> restaurantsCall;
        restaurantsCall = RestaurantApi.getInstance().getRestaurantsId(location);
        disposable = restaurantsCall.subscribeOn(Schedulers.io())
                .subscribe(restaurantList ->
                {
                    List<Place> placesList = new ArrayList<>();
                    for (int i = 0; i < restaurantList.results().size(); i++)
                    {
                        String restaurantId = restaurantList.results().get(i).id();
                        loadPlace(restaurantId, place ->
                        {
                            setFirebasePlaceData(place);
                            placesList.add(place);
                            mPlaces.postValue(placesList);
                        });
                    }
                });
    }
    private void setFirebasePlaceData(Place place)
    {
        RESTAURANTS_REF.child(place.placeId).child("name").setValue(place.name());
    }
    //
    public void likePlace(String restaurantId)
    {
        RESTAURANTS_REF.child(restaurantId).child("likedBy").child(firebaseUserId).setValue(true);
    }
    public void dislikePlace(String restaurantId)
    {
        RESTAURANTS_REF.child(restaurantId).child("likedBy").child(firebaseUserId).removeValue();
    }
    public void selectPlace(String restaurantId)
    {
        RESTAURANTS_REF.child(restaurantId).child("selectedBy").child(firebaseUserId).setValue(true);
    }
    public void deselectPlace(String restaurantId)
    {
        RESTAURANTS_REF.child(restaurantId).child("selectedBy").child(firebaseUserId).removeValue();
    }



    private final ChildEventListener childEventListenerLikeSwitch = new ChildEventListener()
    {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {
            likedBy = new ArrayList<>();
            if ((boolean) dataSnapshot.getValue())
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
            if ((boolean) dataSnapshot.getValue())
            {
                likedBy.add(dataSnapshot.getKey());
            }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };
    private final ChildEventListener childEventListenerSelectSwitch = new ChildEventListener()
    {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {
            selectedBy = new ArrayList<>();
            if ((boolean) dataSnapshot.getValue())
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

    public interface PlaceCallback
    {
        void getPlace(Place place);
    }
}




  /*  public void retrieveLikedRestaurants(String restaurantId)
    {
        DatabaseReference query = RESTAURANTS_REF.child(restaurantId).child("likedBy");

        Disposable childEventDisposable = RxFirebaseDatabase.observeChildEvent(query, Place.class).subscribe(dataSnapshot ->
        {
            switch (dataSnapshot.getEventType())
            {
                case ADDED:
                    manageAddedRestaurant(dataSnapshot);
                case CHANGED:
                    manageAddedRestaurant(dataSnapshot);
                case REMOVED:
                case MOVED:
            }
        }, throwable -> manageError(throwable));
        childEventDisposable.dispose();
    }

    private void manageAddedRestaurant(RxFirebaseChildEvent<Place> dataSnapshot)
    {
        likedBy = new ArrayList<>();
        if ((boolean) dataSnapshot.getValue())
        {
            likedBy.add(dataSnapshot.getKey());
        }
    }

    private void manageError(Throwable throwable)
    {

    }*/
