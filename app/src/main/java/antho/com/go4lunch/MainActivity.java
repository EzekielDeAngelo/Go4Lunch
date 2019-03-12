package antho.com.go4lunch;
/** Main Activity **/

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import antho.com.go4lunch.view.activities.RestaurantDetailsActivity;
import antho.com.go4lunch.view.activities.SearchActivity;
import antho.com.go4lunch.view.activities.SignInActivity;
import antho.com.go4lunch.view.fragments.RestaurantsFragment;
import antho.com.go4lunch.view.fragments.MapsFragment;
import antho.com.go4lunch.view.fragments.WorkmatesFragment;
import antho.com.go4lunch.view.fragments.adapter.RestaurantsAdapter;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

/** **/
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, RestaurantsAdapter.OnRestaurantClickedListener {
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
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

    private WorkmateViewModel viewModel;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        // Write a message to the database
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        // Initialize Firebase Auth
        viewModel = ViewModelProviders.of(this).get(WorkmateViewModel.class);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            if (mFirebaseUser != null)
            viewModel.writeNewUser(mFirebaseUser);

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
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
    public void onItemClicked(String id, String name, String address, String photo, String phone, String website, boolean like, boolean selected)
    {
        Intent intent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("photo", photo);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("phone", phone);
        intent.putExtra("website", website);
        intent.putExtra("like", like);
        intent.putExtra("selected", selected);
        startActivity(intent);
    }
}


