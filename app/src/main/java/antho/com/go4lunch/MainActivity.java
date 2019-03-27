package antho.com.go4lunch;
/** Main Activity **/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
import antho.com.go4lunch.view.activities.SearchActivity;
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

/** **/
public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, RestaurantsAdapter.OnRestaurantClickedListener, WorkmatesAdapter.OnWorkmateClickedListener, NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.navigation)

    BottomNavigationView bottomNavigationView;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private View navHeader;
    private TextView username;
    private ImageView userImgProfile;
    private TextView mTextMessage;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private String TAG = "";
    public static final String ANONYMOUS = "anonymous";
    private GoogleApiClient mGoogleApiClient;
    private FragmentManager fragmentManager;
    private static final String SELECTED_INDEX_KEY = "selected_index";
    private static final String MAP_VIEW_TAG = "MAP_VIEW_TAG";
    private static final String LIST_VIEW_TAG = "LIST_VIEW_TAG";
    private static final String WORKMATES_VIEW_TAG = "WORKMATES_VIEW_TAG";
    private DrawerLayout drawerLayout;
    private WorkmateViewModel viewModel;
    private RestaurantViewModel rViewModel;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        // Write a message to the database
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        // Initialize Firebase Auth

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        viewModel = ViewModelProviders.of(this).get(WorkmateViewModel.class);
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            if (mFirebaseUser != null) {

                viewModel.writeNewUser(mFirebaseUser);
            }
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            viewModel.writeNewUser(mFirebaseUser);
        }


        fragmentManager = getSupportFragmentManager();
        int selectedIndex = savedInstanceState == null ? 0 : savedInstanceState.getInt(SELECTED_INDEX_KEY);
        loadFirstFragment(selectedIndex);
        setUpBottomNavigation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar  , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
        username = (TextView) navHeader.findViewById(R.id.username);
        username.setText(mFirebaseUser.getDisplayName());
        userImgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    //

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.search_item:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
       /*     case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
    //
    @Override
    public void onItemClicked(String id)
    {
        OpenRestaurantDetailsActivity(id);
/*if (rViewModel == null)
        rViewModel = ViewModelProviders.of(this).get("RestaurantViewModel", RestaurantViewModel.class);

            if (id != null)
            {
                //selectedPlace = null;

                rViewModel.getPlace(id).observe(this, place ->
                {
                    if (place != selectedPlace)
                    {  selectedPlace = place;



                        intent = new Intent(getApplication(), RestaurantDetailsActivity.class);
                        intent.putExtra("id", selectedPlace.placeId);
                        intent.putExtra("photo", selectedPlace.thumb);
                        intent.putExtra("name", selectedPlace.name());
                        intent.putExtra("address", selectedPlace.address());
                        intent.putExtra("phone", selectedPlace.phone());
                        intent.putExtra("website", selectedPlace.website());
                        intent.putExtra("like", selectedPlace.like);
                        intent.putExtra("selected", selectedPlace.selected);

                        startActivity(intent);
                    }
                });





            }
*/

        /*

        Intent intent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("photo", photo);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("phone", phone);
        intent.putExtra("website", website);
        intent.putExtra("like", like);
        intent.putExtra("selected", selected);
        startActivity(intent);*/
    }

    String restaurantId;
    Intent intent;
    boolean openIntent;
    Place selectedPlace;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.nav_selection) {

          // WorkmateViewModel wmViewModel;
           //wmViewModel = ViewModelProviders.of(this).get("WorkmateViewModel", WorkmateViewModel.class);
         //  rViewModel = ViewModelProviders.of(this).get("RestaurantViewModel", RestaurantViewModel.class);
           viewModel.getWorkmate(mFirebaseUser.getUid()).observe(this, workmate ->
           {

               if (workmate.restaurantId != null)
                   OpenRestaurantDetailsActivity(workmate.restaurantId);
                   /*rViewModel.getPlace(workmate.restaurantId).observe(this, place ->
                {

                   selectedPlace = place;

                    if (selectedPlace != null && openIntent == false) {
                        intent = new Intent(getApplication(), RestaurantDetailsActivity.class);
                        intent.putExtra("id", selectedPlace.placeId);
                        intent.putExtra("photo", selectedPlace.thumb);
                        intent.putExtra("name", selectedPlace.name());
                        intent.putExtra("address", selectedPlace.address());
                        intent.putExtra("phone", selectedPlace.phone());
                        intent.putExtra("website", selectedPlace.website());
                        intent.putExtra("like", selectedPlace.like);
                        intent.putExtra("selected", selectedPlace.selected);
                        openIntent = true;
                        startActivity(intent);

                    }

               });*/

           });
        }
        if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            // Handle the camera action
        }
        if (id == R.id.nav_sign_out) {
            // Handle the camera action
            mFirebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        selectedPlace = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void OpenRestaurantDetailsActivity(String id)
    {
        openIntent = false;
        //if (rViewModel == null)

        rViewModel = ViewModelProviders.of(this).get("RestaurantViewModel", RestaurantViewModel.class);
        if (id != null) {
            //selectedPlace = null;
            rViewModel.getPlace(id).observe(this, place ->
            {
                if (place != selectedPlace && openIntent == false) {
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
                    //}
                }
            });

        }
    }
}


