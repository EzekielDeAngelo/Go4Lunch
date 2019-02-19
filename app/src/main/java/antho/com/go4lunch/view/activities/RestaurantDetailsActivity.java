package antho.com.go4lunch.view.activities;
/** **/
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import antho.com.go4lunch.R;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import antho.com.go4lunch.viewmodel.ViewModelFactory;
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
    @BindView(R.id.call) Button call;
    @BindView(R.id.website) Button website;
    @BindView(R.id.like)
    ToggleButton like;
    private WorkmateViewModel viewModel;
    private RestaurantViewModel restaurantViewModel;
    //
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);
        //viewModel = ViewModelProviders.of(this).get(WorkmateViewModel.class);

        if(getIntent().hasExtra("id"))
        {
            //restaurantViewModel = ViewModelProviders.of(this).get("RestaurantViewModel", RestaurantViewModel.class);
         /*   restaurantViewModel.getPlace(getIntent().getStringExtra("id")).observe(this, new Observer<Place>() {
                        @Override
                        public void onChanged(Place place) {
                       Log.d("hihihi", place.name());
                        }
                    });*/
                    name.setText(getIntent().getStringExtra("name"));
            address.setText(getIntent().getStringExtra("address"));
            String url = getIntent().getStringExtra("photo");
            Picasso.Builder builder = new Picasso.Builder (photo.getContext());
            builder.downloader(new OkHttp3Downloader(photo.getContext()));
            builder.build().load(url)
                    .into(photo);
        }
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        viewModel = ViewModelProviders.of(this).get("WorkmateViewModel", WorkmateViewModel.class);
        select.setOnClickListener(workmates -> {

            viewModel.selectPlace(mFirebaseUser, getIntent().getStringExtra("name"));
        });
        call.setOnClickListener(workmates -> {
            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse("tel:" + getIntent().getStringExtra("phone")));
            startActivity(intent);
        });
        website.setOnClickListener(workmates -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getIntent().getStringExtra("website"));
            startActivity(intent);
        });
        restaurantViewModel = ViewModelProviders.of(this, new ViewModelFactory(null)).get("RestaurantViewModel", RestaurantViewModel.class);
        like.setChecked(getIntent().getBooleanExtra("like", false));
        like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    restaurantViewModel.likePlace(mFirebaseUser, getIntent().getStringExtra("id"));
                    restaurantViewModel.getPlace(getIntent().getStringExtra("id")).observe(RestaurantDetailsActivity.this, place ->
                    {
                        place.likedBy.add(mFirebaseUser.getUid());
                        Log.d("onCheckedChanged", String.valueOf(place.name() + " : " + String.valueOf(place.like)));
                    });
                }
                else
                {
                    restaurantViewModel.dislikePlace(mFirebaseUser, getIntent().getStringExtra("id"));
                    restaurantViewModel.getPlace(getIntent().getStringExtra("id")).observe(RestaurantDetailsActivity.this, place ->
                    {
                        place.likedBy.remove(mFirebaseUser.getUid());
                        //Log.d("rototo", String.valueOf(place.likedBy.size()));
                    });
                }
            }
        });


        like.setOnClickListener(workmates -> {


        });

            //place.likedBy.add(getIntent().getStringExtra("id"));
        /*website.setOnClickListener(workmates -> {
      restaurantViewModel.dislikePlace(mFirebaseUser, getIntent().getStringExtra("id"));
        });*/
        //select.setOnClickListener(selectRestaurant);
    }
    //
    /*View.OnClickListener selectRestaurant = new View.OnClickListener()
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
      /*  }
    };*/
}
