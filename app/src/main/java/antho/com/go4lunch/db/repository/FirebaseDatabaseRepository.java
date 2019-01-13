package antho.com.go4lunch.db.repository;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import antho.com.go4lunch.db.mapper.FirebaseMapper;

public abstract class FirebaseDatabaseRepository<Model>
{
    protected DatabaseReference databaseReference;
    protected FirebaseDatabaseRepositoryCallback<Model> firebaseCallback;

    private BaseValueEventListener listener;
    private FirebaseMapper mapper;

    protected abstract String getRootNode();

    public FirebaseDatabaseRepository(FirebaseMapper mapper)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference(getRootNode());
        databaseReference.child("restaurants").child("name").setValue("Mcdo");
        this.mapper = mapper;
    }

    public void addListener(FirebaseDatabaseRepositoryCallback<Model> firebaseCallback)
    {
        this.firebaseCallback = firebaseCallback;
        Log.d("TEST", "azerty");
        listener = new BaseValueEventListener(mapper, firebaseCallback);
        databaseReference.addValueEventListener(listener);
    }

    public void removeListener()
    {
        databaseReference.removeEventListener(listener);
    }

    public interface FirebaseDatabaseRepositoryCallback<T>
    {
        void onSuccess(List<T> result);
        void onError(Exception e);
    }
}
