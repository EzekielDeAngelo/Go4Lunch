package antho.com.go4lunch.view.activities;
/** **/
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import antho.com.go4lunch.MainActivity;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.restaurant.Restaurant;
import antho.com.go4lunch.model.workmate.Workmate;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
/** **/
public class RestaurantDetailsActivity extends AppCompatActivity
{
    @BindView(R.id.photo) ImageView photo;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.address) TextView address;
    @BindView(R.id.select) Button select;
    private WorkmateViewModel viewModel;
    //
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);
        //viewModel = ViewModelProviders.of(this).get(WorkmateViewModel.class);
        viewModel = ViewModelProviders.of(this).get("WorkmateViewModel", WorkmateViewModel.class);
        select.setOnClickListener(workmates -> {
            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
            viewModel.selectPlace(mFirebaseUser, getIntent().getStringExtra("name"));
        });
        if(getIntent().hasExtra("name"))
        {
            name.setText(getIntent().getStringExtra("name"));
            address.setText(getIntent().getStringExtra("address"));
            String url = getIntent().getStringExtra("photo");
            Picasso.Builder builder = new Picasso.Builder (photo.getContext());
            builder.downloader(new OkHttp3Downloader(photo.getContext()));
            builder.build().load(url)
                    .into(photo);
        }
        select.setOnClickListener(selectRestaurant);
    }
    //
    View.OnClickListener selectRestaurant = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

            viewModel.selectPlace(mFirebaseUser, getIntent().getStringExtra("name"));


            //viewModel.selectPlace(mFirebaseUser, getIntent().getStringExtra("name"));
            /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("workmate");
            String key = databaseReference.child(mFirebaseUser.getUid()).push().getKey();
            Workmate wm = new Workmate(mFirebaseUser.getUid(), mFirebaseUser.getDisplayName() , name.getText().toString());
            Map<String, Object> postValues = wm.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(mFirebaseUser.getUid(), postValues);
            databaseReference.updateChildren(childUpdates);*/
            //viewModel.getWorkmates();
        }
    };
}
