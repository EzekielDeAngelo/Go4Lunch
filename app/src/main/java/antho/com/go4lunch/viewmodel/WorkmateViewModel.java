package antho.com.go4lunch.viewmodel;
/** **/
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import antho.com.go4lunch.model.workmate.Workmate;
import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseQuery;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/** **/
public class WorkmateViewModel extends ViewModel
{
    private MutableLiveData<List<Workmate>> workmates;
    private MutableLiveData<Workmate> workmate;
    private MutableLiveData<List<Workmate>> workmatesPerRestaurant;
    protected DatabaseReference databaseReference;
    private Disposable disposable;
    // Constructor
    public WorkmateViewModel()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (workmates == null)
        {
            workmates = new MutableLiveData<List<Workmate>>();
            workmate = new MutableLiveData<Workmate>();
            workmatesPerRestaurant = new MutableLiveData<List<Workmate>>();
        }
        loadWorkmates();
        if (mFirebaseUser != null)
        loadWorkmate(mFirebaseUser.getUid());
    }
    //
    public LiveData<List<Workmate>> getWorkmates() {
        return workmates; }
    public LiveData<Workmate> getWorkmate(String id) {
        loadWorkmate(id); return workmate; }
    public LiveData<List<Workmate>> getWorkmatesPerRestaurants (String restaurantId){ loadWorkmatesPerRestaurants(restaurantId); return workmatesPerRestaurant;}
    //
    List<Workmate> wmList = new ArrayList<>();
    Workmate wm;
    //
    private void loadWorkmatesPerRestaurants(String restaurantId)
    {
      wmList = workmates.getValue();
    }

    private void loadWorkmate(String id)
    {
        DatabaseReference workmateDatabaseReference = FirebaseDatabase.getInstance().getReference("workmate").child(id);
        RxFirebaseDatabase.observeSingleValueEvent(workmateDatabaseReference, Workmate.class).subscribeOn(Schedulers.io()).subscribe(wm ->
        {
            workmate.postValue(wm);
        });
    }
    private void loadWorkmates() {


        databaseReference = FirebaseDatabase.getInstance().getReference("workmate");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                dataSnapshot.getValue();

                wmList.clear();
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot workmate : data)
                {
                    Workmate wm = workmate.getValue(Workmate.class);
                    wmList.add(wm);
                }
                workmates.postValue(wmList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //
    public void writeNewUser(FirebaseUser user)
    {
        databaseReference.child(user.getUid()).child("name").setValue(user.getDisplayName());
    }
    public void selectPlace(FirebaseUser user, String restaurantId)
    {
       // loadWorkmate(user.getUid());
        //if (databaseReference.child(user.getUid()).child("restaurantId").getKey() == restaurantId)

        //selectedRestaurantId = restaurantId;

        databaseReference.child(user.getUid()).child("restaurantId").setValue(restaurantId);

    }
    public void deselectPlace(FirebaseUser user, String restaurantId)
    {
        //loadWorkmate(user.getUid());
       // selectedRestaurantId = null;
        //if (databaseReference.child(user.getUid()).child("restaurantId").getKey() == restaurantId)
        //DatabaseReference workamteDatabaseReference = FirebaseDatabase.getInstance().getReference("workmate");
        databaseReference.child(user.getUid()).child("restaurantId").removeValue();

    }
    /*public void likeRestaurant(FirebaseUser user, String restaurantId)
    {
        databaseReference.child(user.getUid()).child("likedRestaurantsId").child(restaurantId).
    }
    public void dislikeRestaurant(FirebaseUser user, String restaurantId)
    {
        databaseReference.child(user.getUid()).child("likedRestaurantsId").child("id").removeValue();
    }*/
}
