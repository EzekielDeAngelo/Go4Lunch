package antho.com.go4lunch.viewmodel;
/** **/
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import antho.com.go4lunch.model.workmate.Workmate;
import io.reactivex.disposables.Disposable;
/** **/
public class WorkmateViewModel extends ViewModel
{
    private MutableLiveData<List<Workmate>> workmates;

    protected DatabaseReference databaseReference;
    private Disposable disposable;
    // Constructor
    public WorkmateViewModel()
    {
        if (workmates == null)
        {
            workmates = new MutableLiveData<List<Workmate>>();
        }
        loadWorkmates();
    }
    //
    public LiveData<List<Workmate>> getWorkmates() {
        return workmates; }
    //
    List<Workmate> wmList = new ArrayList<>();
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
    //
    public void writeNewUser(FirebaseUser user)
    {
        databaseReference.child(user.getUid()).child("name").setValue(user.getDisplayName());
    }
    public void selectPlace(FirebaseUser user, String restaurantId)
    {
        databaseReference.child(user.getUid()).child("restaurantId").setValue(restaurantId);

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
