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
    @BindView(R.id.select) ToggleButton select;
    @BindView(R.id.call) Button call;
    @BindView(R.id.website) Button website;
    @BindView(R.id.like) ToggleButton like;
    private WorkmateViewModel viewModel;
    private RestaurantViewModel restaurantViewModel;
    //
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("id"))
        {
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

        select.setChecked(getIntent().getBooleanExtra("selected", false));
        select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    viewModel.selectPlace(mFirebaseUser, getIntent().getStringExtra("id"));
                    viewModel.getWorkmate(mFirebaseUser.getUid()).observe(RestaurantDetailsActivity.this, workmate ->
                    {
                        workmate.restaurantId = getIntent().getStringExtra("name");
                    });
                    restaurantViewModel.selectPlace(mFirebaseUser, getIntent().getStringExtra("id"));
                    restaurantViewModel.getPlace(getIntent().getStringExtra("id")).observe(RestaurantDetailsActivity.this, place ->
                    {
                        place.selectedBy.add(mFirebaseUser.getUid());
                        Log.d("onSelectCheckedChanged", String.valueOf(place.name() + " : " + String.valueOf(place.selected)));
                    });
                }
                else
                {

                    viewModel.deselectPlace(mFirebaseUser, getIntent().getStringExtra("name"));
                    viewModel.getWorkmate(mFirebaseUser.getUid()).observe(RestaurantDetailsActivity.this, workmate ->
                    {
                    });
                    restaurantViewModel.deselectPlace(mFirebaseUser, getIntent().getStringExtra("id"));
                    restaurantViewModel.getPlace(getIntent().getStringExtra("id")).observe(RestaurantDetailsActivity.this, place ->
                    {
                        place.selectedBy.remove(mFirebaseUser.getUid());
                    });
                }
            }
        });
        call.setOnClickListener(workmates ->
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + getIntent().getStringExtra("phone")));
            startActivity(intent);
        });
        website.setOnClickListener(workmates ->
        {
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
                        Log.d("onLikedCheckedChanged", String.valueOf(place.name() + " : " + String.valueOf(place.like)));
                    });
                }
                else
                {
                    restaurantViewModel.dislikePlace(mFirebaseUser, getIntent().getStringExtra("id"));
                    restaurantViewModel.getPlace(getIntent().getStringExtra("id")).observe(RestaurantDetailsActivity.this, place ->
                    {
                        place.likedBy.remove(mFirebaseUser.getUid());
                    });
                }
            }
        });
    }
}
