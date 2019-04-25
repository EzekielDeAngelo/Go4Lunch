package antho.com.go4lunch.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class FirebaseDatabaseRepository<Workmate>
{
   /* protected DatabaseReference databaseReference;
    protected  FirebaseDatabaseRepositoryCallback<Workmate> firebaseCallback;
    private BaseValueEventListener listener;
    private FirebaseMapper mapper;
    protected abstract String getRootNode();



    public FirebaseDatabaseRepository(FirebaseMapper mapper) {

        databaseReference = FirebaseDatabase.getInstance().getReference(getRootNode());

        this.mapper = mapper;

    }



    public void addListener(FirebaseDatabaseRepositoryCallback<Model> firebaseCallback) {

        this.firebaseCallback = firebaseCallback;

        listener = new BaseValueEventListener(mapper, firebaseCallback);

        databaseReference.addValueEventListener(listener);

    }



    public void removeListener() {

        databaseReference.removeEventListener(listener);

    }



    public interface FirebaseDatabaseRepositoryCallback<T> {

        void onSuccess(List<T> result);



        void onError(Exception e);

    }
*/
}