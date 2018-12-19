package antho.com.go4lunch;
/** Main Activity **/

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import antho.com.go4lunch.activities.SignInActivity;
import antho.com.go4lunch.fragments.MapsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/** **/
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
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
    private static final String WORKMATES_TAG = "WORKMATES_TAG";
    protected GeoDataClient mGeoDataClient;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        fragmentManager = getSupportFragmentManager();
        int selectedIndex = savedInstanceState == null ? 0 : savedInstanceState.getInt(SELECTED_INDEX_KEY);
        loadFirstFragment(selectedIndex);
        setUpBottomNavigation();
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mPlaceDetectionClient.getCurrentPlace(null);

        // TODO: Start using the Places API.

    }// Loads fragment based on index given as parameter
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
               /* tag = POPULAR_NEWS_TAG;
                fragment = fragmentManager.findFragmentByTag(POPULAR_NEWS_TAG);
                if (fragment == null)
                {
                    fragment = PopularFragment.newInstance();
                }*/
                break;
            case 2:
                /*tag = BUSINESS_NEWS_TAG;
                fragment = fragmentManager.findFragmentByTag(BUSINESS_NEWS_TAG);
                if (fragment == null)
                {
                    fragment = BusinessFragment.newInstance();
                }*/
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
}



        /*if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
                {/*whatever*/}
*/

