package antho.com.go4lunch.view.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import antho.com.go4lunch.MainActivity;
import antho.com.go4lunch.R;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;

/** Handles google, facebook and twitter sign in behavior **/
public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{
    private TwitterLoginButton twitterSignInButton;
    private FirebaseAuth mFirebaseAuth;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.sign_in_button) Button signInButton;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private WorkmateViewModel workmateViewModel;
    // Set on click listener for sign in buttons and get firebase instance
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        workmateViewModel = ViewModelProviders.of(this).get(WorkmateViewModel.class);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);
        setContentView(R.layout.activity_sign_in);
        signInButton = findViewById(R.id.sign_in_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        //signInButton.setOnClickListener(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        Button googleSignInButton = findViewById(R.id.google_login_button);
        googleSignInButton.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel()
            {
                Log.d(TAG, "facebook:onCancel");
                FirebaseAuth.getInstance().signOut();
            }
            @Override
            public void onError(FacebookException error)
            {
                Log.d(TAG, "facebook:onError", error);
            }
        });
        Button facebookSignInButton = findViewById(R.id.facebook_login_button);
        facebookSignInButton.setOnClickListener(view ->
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile", "user_friends")));
        // Configure Twitter Sign In
        twitterSignInButton = findViewById(R.id.twitter_login_button);
        twitterSignInButton.setCallback(new Callback<TwitterSession>()
        {
            @Override
            public void success(Result<TwitterSession> result)
            {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }
            @Override
            public void failure(TwitterException exception)
            {
                Log.w(TAG, "twitterLogin:failure", exception);
            }
        });
    }
    // On click listener for google sign in button
    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.google_login_button) {
            signIn();
        }
        if (v.getId() == R.id.sign_in_button)
        login();

    }
    // Launch sign in intent from google sign in API
    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // An unresolvable error has occurred and google APIs (including sign-in) will not be available
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        twitterSignInButton.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess())
            {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(Objects.requireNonNull(account));
            }
            else
            {
                // Google Sign-In failed
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }
    // Logic to sign in with google credentials
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful())
                    {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        workmateViewModel.writeNewUser(user);
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }
    // Logic to sign in with facebook credentials
    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                    {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        workmateViewModel.writeNewUser(user);
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    } else
                    {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Logic to sign in with twitter credentials
    private void handleTwitterSession(TwitterSession session)
    {
        Log.d(TAG, "handleTwitterSession:" + session);
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful())
                    {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        workmateViewModel.writeNewUser(user);
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    }
                    else
                    {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }



    // Logic to sign in user with credentials entered
    private void login()
    {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        mFirebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getEmail()).build();
                        user.updateProfile(profileUpdates);
                        Log.d(user.getDisplayName(), user.getDisplayName());

                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                      //  showToast("Authentication failed");
                        updateUI(null);
                    }
                });
    }
    // Load main activity when sign in has been successfully done
    private void updateUI(FirebaseUser user)
    {
        if (user != null)
        {
            workmateViewModel.writeNewUser(user);
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }
    }
}


