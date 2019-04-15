package antho.com.go4lunch;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import antho.com.go4lunch.base.BaseActivity;
import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.view.activities.RestaurantDetailsActivity;
import antho.com.go4lunch.view.activities.SettingsActivity;
import antho.com.go4lunch.view.activities.SignInActivity;
import antho.com.go4lunch.view.fragments.RestaurantsFragment;
import antho.com.go4lunch.view.fragments.MapsFragment;
import antho.com.go4lunch.view.fragments.WorkmatesFragment;
import antho.com.go4lunch.view.fragments.adapter.RestaurantsAdapter;
import antho.com.go4lunch.view.fragments.adapter.WorkmatesAdapter;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
/** Load UI elements on application startup **/
public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, RestaurantsAdapter.OnRestaurantClickedListener, WorkmatesAdapter.OnWorkmateClickedListener, NavigationView.OnNavigationItemSelectedListener, PlaceSelectionListener
{
    @BindView(R.id.navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    private GoogleApiClient mGoogleApiClient;
    private FragmentManager fragmentManager;
    private WorkmateViewModel workmateViewModel;
    private RestaurantViewModel restaurantViewModel;

    private View navHeader;
    private TextView userTextView;
    private ImageView userImageView;

    private FirebaseUser user;
    private String userName;
    private String photoUrl;

    public static final String ANONYMOUS = "anonymous";
    private static final String SELECTED_INDEX_KEY = "selected_index";
    private static final String MAP_VIEW_TAG = "MAP_VIEW_TAG";
    private static final String LIST_VIEW_TAG = "LIST_VIEW_TAG";
    private static final String WORKMATES_VIEW_TAG = "WORKMATES_VIEW_TAG";
    //private String TAG = "";
    //private TextView mTextMessage;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Places.initialize(getApplicationContext(),"AIzaSyAuGzug9RNw6hQZvzetYY-VB5is8OLeS7w");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        workmateViewModel = ViewModelProviders.of(this).get(WorkmateViewModel.class);
        initFirebaseAuth();

        fragmentManager = getSupportFragmentManager();
        int selectedIndex = savedInstanceState == null ? 0 : savedInstanceState.getInt(SELECTED_INDEX_KEY);
        loadFirstFragment(selectedIndex);

        setUpBottomNavigation();

    }
    // Initialize Firebase Auth
    private void initFirebaseAuth()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Set default user as anonymous.
        userName = ANONYMOUS;
        // Write a message to the database
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        if (user == null)
        {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            if (user != null)
                workmateViewModel.writeNewUser(FirebaseAuth.getInstance().getCurrentUser());
            finish();
            return;
        }
        else
        {
            userName = user.getDisplayName();
            if (user.getPhotoUrl() != null)
            {
                photoUrl = user.getPhotoUrl().toString();
            }
            setUpNavigationDrawer();
        }
    }
    // Set up drawer layout and navigation view
    private void setUpNavigationDrawer()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar  , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
        userTextView = (TextView) navHeader.findViewById(R.id.username);
        userTextView.setText(user.getDisplayName());
        userImageView = (ImageView) navHeader.findViewById(R.id.img_profile);
    }
    // Loads fragment based on index given as parameter
    private void loadFirstFragment(int selectedIndex)
    {
        Fragment fragment = null;
        String tag = null;

        switch (selectedIndex)
        {
            case 0:
                tag = MAP_VIEW_TAG;
                fragment = fragmentManager.findFragmentByTag(MAP_VIEW_TAG);
                if (fragment == null)
                {
                    fragment = MapsFragment.newInstance();
                }
                break;
            case 1:
                tag = LIST_VIEW_TAG;
                fragment = fragmentManager.findFragmentByTag(LIST_VIEW_TAG);
                if (fragment == null)
                {
                    fragment = RestaurantsFragment.newInstance();
                }
                break;
            case 2:
                tag = WORKMATES_VIEW_TAG;
                fragment = fragmentManager.findFragmentByTag(WORKMATES_VIEW_TAG);
                if (fragment == null)
                {
                    fragment = WorkmatesFragment.newInstance();
                }
                break;
        }

        if (fragment != null)
        {
            changeFragment(fragment, tag);
        }
    }
    // Set up buttons for bottom navigation view
    private void setUpBottomNavigation()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem ->
        {
            switch (menuItem.getItemId())
            {
                case R.id.navigation_map:
                    Fragment fragmentMap = fragmentManager.findFragmentByTag(MAP_VIEW_TAG);
                    if (fragmentMap == null) fragmentMap = MapsFragment.newInstance();
                    changeFragment(fragmentMap, MAP_VIEW_TAG);
                    return true;
                case R.id.navigation_list:

                    Fragment fragmentList = fragmentManager.findFragmentByTag(LIST_VIEW_TAG);
                    if (fragmentList == null) fragmentList = RestaurantsFragment.newInstance();
                    changeFragment(fragmentList, LIST_VIEW_TAG);
                    return true;
                case R.id.navigation_workmates:

                    Fragment fragmentWorkmates = fragmentManager.findFragmentByTag(WORKMATES_VIEW_TAG);
                    if (fragmentWorkmates == null) fragmentWorkmates = WorkmatesFragment.newInstance();
                    changeFragment(fragmentWorkmates, WORKMATES_VIEW_TAG);
                    return true;

            }
            return false;
        });
    }
    // Load and unload fragments when they are changed
    public void changeFragment(Fragment fragment, String tagFragmentName)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null)
        {
            fragmentTransaction.hide(currentFragment);
        }
        Fragment fragmentTemp = fragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null)
        {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.fragment_frame, fragmentTemp, tagFragmentName);
        } else
        {
            fragmentTransaction.show(fragmentTemp);
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }
    //
    private Intent intent;
    private boolean openIntent;
    Place selectedPlace;
    private void OpenRestaurantDetailsActivity(String id)
    {
        openIntent = false;
        restaurantViewModel = ViewModelProviders.of(this).get("RestaurantViewModel", RestaurantViewModel.class);
        if (id != null)
        {
            restaurantViewModel.loadPlace(id);
            restaurantViewModel.getPlace().observe(this, place ->
            {
                if (place != selectedPlace && openIntent == false)
                {
                    selectedPlace = place;
                    intent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
                    intent.putExtra("id", place.placeId);
                    intent.putExtra("photo", place.thumb);
                    intent.putExtra("name", place.name());
                    intent.putExtra("address", place.address());
                    intent.putExtra("phone", place.phone());
                    intent.putExtra("website", place.website());
                    intent.putExtra("like", place.like);
                    intent.putExtra("selected", place.selected);
                    openIntent = true;
                    startActivity(intent);
                }
            });
        }
    }
    // Creates the options menu for action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    // Handles action bar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search_item:
                int AUTOCOMPLETE_REQUEST_CODE = 1;

// Set the fields to specify which types of place data to
// return after the user has made a selection.
                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(MainActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // Handle navigation view item clicks
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.nav_selection)
        {
            workmateViewModel.getWorkmate(user.getUid()).observe(this, workmate ->
            {
                if (workmate.restaurantId != null)
                    OpenRestaurantDetailsActivity(workmate.restaurantId);
            });
        }
        if (id == R.id.nav_settings)
        {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_sign_out)
        {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            userName = ANONYMOUS;
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    // Set selected place to null and close drawer if it is open on back button press
    @Override
    public void onBackPressed()
    {
        selectedPlace = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
    // Open restaurant details activity based on id in parameter on item click
    @Override
    public void onItemClicked(String id)
    {
        OpenRestaurantDetailsActivity(id);
    }
    //
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    // Return activity layout
    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {

    }

    @Override
    public void onError(@NonNull Status status) {

    }
}


