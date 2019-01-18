package antho.com.go4lunch.db.repository;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import antho.com.go4lunch.db.RestaurantApi;
import antho.com.go4lunch.db.mapper.FirebaseMapper;
import antho.com.go4lunch.model.Restaurant;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class FirebaseDatabaseRepository<Model>
{
    protected DatabaseReference databaseReference;
    protected FirebaseDatabaseRepositoryCallback<Model> firebaseCallback;

    private BaseValueEventListener listener;
    private FirebaseMapper mapper;

    private MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<>();
    private Disposable disposable;
    protected abstract String getRootNode();

    public FirebaseDatabaseRepository(FirebaseMapper mapper)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference(getRootNode());
        this.mapper = mapper;
    }

    public void addListener(FirebaseDatabaseRepositoryCallback<Model> firebaseCallback)
    {
        this.firebaseCallback = firebaseCallback;
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
