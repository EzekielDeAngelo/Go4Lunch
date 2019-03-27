package antho.com.go4lunch.viewmodel;
/** **/
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
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
      Log.d("rototo", String.valueOf(wmList.size()));


    }


    private void loadWorkmate(String id)
    {

        DatabaseReference workmateDatabaseReference = FirebaseDatabase.getInstance().getReference("workmate").child(id);
        RxFirebaseDatabase.observeSingleValueEvent(workmateDatabaseReference, Workmate.class).subscribeOn(Schedulers.io()).subscribe(wm ->
        {
            workmate.postValue(wm);


        });/*
        DatabaseReference workmateDatabaseReference = FirebaseDatabase.getInstance().getReference("workmate");

        RxFirebaseDatabase.observeSingleValueEvent(workmateDatabaseReference, DataSnapshotMapper.listOf(Workmate.class)).subscribe(wm ->
        {
            Log.d("patatie", String.valueOf(wm.size()));
            //workmate.postValue(wm);
        });
        /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference from = reference.child("workmate");
        Query where = reference.child("workmate").child(id).child("restaurantId");
        Log.d("patati",((DatabaseReference) where).getKey());
        RxFirebaseQuery.getInstance().filterByRefs(from, where).asList().subscribe(dataSnapshots ->
        {  Log.d("patataoes", String.valueOf(dataSnapshots.size()));
           for (DataSnapshot dataSnapshot : dataSnapshots)
           {
               Workmate wm = dataSnapshot.getValue(Workmate.class);

           }
        });/*
        RxFirebaseDatabase.observeChildEvent(query).subscribeOn(Schedulers.io())
                .subscribe(workmate ->
                {wm = (Workmate) workmate.getValue();
                        Log.d("patatoes",workmate.getKey());
                });
        /*RxFirebaseDatabase.observeSingleValueEvent(query, Workmate.class)

                .subscribe(userData -> (userData),

                        throwable -> manageError(throwable));

*/
  /*      workmateDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //Workmate wm = dataSnapshot.getValue(Workmate.class);
                //if (wm != null)
                //dataSnapshot.getValue();
                //wm=null;
                //wmList.clear();
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                //wm = dataSnapshot.getValue(Workmate.class);

                for (DataSnapshot workmateData : data)
                {



                    //wm = workmateData.getValue(Workmate.class);


                }
                //workmates.postValue(wmList);
                if (wm != null)
                {  workmate.postValue(wm);

                }
                //Log.d("wm", wm.name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }



        });*/
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
        /*
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // Get Post object and use the values to update the UI
                dataSnapshot.getValue();
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot workmate : data)
                {
                    Workmate wm = workmate.getValue(Workmate.class);
                    wmList.add(wm);
                }
                workmates.postValue(wmList);

            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Getting Post failed, log a message
               // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });*/
    }
    private String selectedRestaurantId;
    //
    public void writeNewUser(FirebaseUser user)
    {
        databaseReference.child(user.getUid()).child("name").setValue(user.getDisplayName());
    }
    public void selectPlace(FirebaseUser user, String restaurantId)
    {
        loadWorkmate(user.getUid());
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
        databaseReference.child(user.getUid()).child("restaurantId").setValue(null);

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
