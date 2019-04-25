package antho.com.go4lunch.view.activities;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import antho.com.go4lunch.R;
import antho.com.go4lunch.view.activities.adapter.RestaurantDetailsAdapter;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
/** **/
public class RestaurantDetailsActivity extends AppCompatActivity
{
    @BindView(R.id.name) TextView nameTextView;
    @BindView(R.id.address) TextView addressTextView;
    @BindView(R.id.photo) ImageView photoImageView;
    @BindView(R.id.call) Button callButton;
    @BindView(R.id.website) Button websiteButton;
    @BindView(R.id.select) ToggleButton selectToggleButton;
    @BindView(R.id.like) ToggleButton likeToggleButton;
    @BindView(R.id.restaurant_details_recyclerview) RecyclerView recyclerView;
    private WorkmateViewModel workmateViewModel;
    private RestaurantViewModel restaurantViewModel;
    private final String firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String restaurantId;
    //
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        workmateViewModel = ViewModelProviders.of(this).get("WorkmateViewModel", WorkmateViewModel.class);
        restaurantViewModel = ViewModelProviders.of(this).get("RestaurantViewModel", RestaurantViewModel.class);
        restaurantId = getIntent().getStringExtra("id");

        initUi();
        observeViewModel();
    }
    //
    private void initUi()
    {
        String restaurantName = getIntent().getStringExtra("name");
        String restaurantPhoto = getIntent().getStringExtra("photo");
        String restaurantAddress = getIntent().getStringExtra("address");

        nameTextView.setText(restaurantName);
        addressTextView.setText(restaurantAddress);
        Picasso.Builder builder = new Picasso.Builder (photoImageView.getContext());
        builder.downloader(new OkHttp3Downloader(photoImageView.getContext()));
        builder.build().load(restaurantPhoto)
                .into(photoImageView);

        setOnClickListeners();
        setOnCheckedChangeListeners();
    }
    //
    private void setOnClickListeners()
    {
        callButton.setOnClickListener(workmates ->
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + getIntent().getStringExtra("phone")));
            startActivity(intent);
        });
        websiteButton.setOnClickListener(workmates ->
        {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getIntent().getStringExtra("websiteButton"));
            startActivity(intent);
        });
    }
    //
    private void setOnCheckedChangeListeners()
    {
        selectToggleButton.setChecked(getIntent().getBooleanExtra("selected", false));
        selectToggleButton.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                restaurantViewModel.selectPlace(restaurantId);
                workmateViewModel.selectPlace(restaurantId);
            }
            else
            {
                restaurantViewModel.deselectPlace(restaurantId);
                workmateViewModel.deselectPlace();
            }
        });
        likeToggleButton.setChecked(getIntent().getBooleanExtra("likeToggleButton", false));
        likeToggleButton.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                restaurantViewModel.likePlace(getIntent().getStringExtra("id"));
            }
            else
            {
                restaurantViewModel.dislikePlace(getIntent().getStringExtra("id"));
            }
        });
    }
    //
    private void observeViewModel()
    {
        workmateViewModel.getWorkmates().observe(this, workmates ->
        {
            recyclerView.setAdapter(new RestaurantDetailsAdapter());
            RestaurantDetailsAdapter adapter = (RestaurantDetailsAdapter) recyclerView.getAdapter();
            Objects.requireNonNull(adapter).setData(workmates, getIntent().getStringExtra("id"));
        });
        /** **/
        LiveData<DataSnapshot> liveData = restaurantViewModel.getRestaurantDataSnapshotLiveData();
        liveData.observe(this, dataSnapshot ->
        {
            if (dataSnapshot != null) {
                String ticker = dataSnapshot.child("ticker").getValue(String.class);
                Log.d("prouta", String.valueOf(dataSnapshot.child(getIntent().getStringExtra("id")).child("selectedBy").child(firebaseUserId).getValue()));
                //tvTicker.setText(ticker);
                Float price = dataSnapshot.child("price").getValue(Float.class);
                //tvPrice.setText(String.format(Locale.getDefault(), "%.2f", price));
            }

        });
        /** **/
    }
}
