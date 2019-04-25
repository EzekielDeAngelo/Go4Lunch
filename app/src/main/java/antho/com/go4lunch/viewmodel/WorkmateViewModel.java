package antho.com.go4lunch.viewmodel;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import io.reactivex.schedulers.Schedulers;

/** **/
public class WorkmateViewModel extends ViewModel
{
    private final MutableLiveData<List<Workmate>> workmates;
    private final MutableLiveData<Workmate> workmate;
    //private DatabaseReference databaseReference;
    private final String firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final DatabaseReference WORKMATES_REF = FirebaseDatabase.getInstance().getReference("workmates");
    // Constructor
    public WorkmateViewModel()
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        workmates = new MutableLiveData<>();
        workmate = new MutableLiveData<>();

        loadWorkmates();
        if (mFirebaseUser != null)
        loadWorkmate(mFirebaseUser.getUid());
    }
    // Return MutableLiveData for testing purpose
    public MutableLiveData<List<Workmate>> getMutableLiveData() { return workmates; }
    //
    public LiveData<List<Workmate>> getWorkmates() {
        return workmates; }
    public LiveData<Workmate> getWorkmate(String id) {
        loadWorkmate(id); return workmate; }
    //
    private List<Workmate> wmList = new ArrayList<>();

    //

    private void loadWorkmate(String id)
    {
        //DatabaseReference workmateDatabaseReference = FirebaseDatabase.getInstance().getReference("workmate").child(id);
        //DatabaseReference query = WORKMATES_REF.child(id);
        //RxFirebaseDatabase.observeSingleValueEvent(query, Workmate.class).subscribeOn(Schedulers.io()).subscribe(workmate::postValue);
    }
    private void loadWorkmates()
    {
        //DatabaseReference query = WORKMATES_REF;
        //RxFirebaseDatabase.observeSingleValueEvent(query, Workmate.class).subscribe(workmate -> wmList.add(workmate));

        WORKMATES_REF.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
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
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }
    //
    public void writeNewUser(FirebaseUser user)
    {
        WORKMATES_REF.child(user.getUid()).child("name").setValue(user.getDisplayName());
    }
    public void selectPlace(String restaurantId)
    {
        WORKMATES_REF.child(firebaseUserId).child("restaurantId").setValue(restaurantId);
    }

    public void deselectPlace()
    {
        WORKMATES_REF.child(firebaseUserId).child("restaurantId").removeValue();
    }
}
