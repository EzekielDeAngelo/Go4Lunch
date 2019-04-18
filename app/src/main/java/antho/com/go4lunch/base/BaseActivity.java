package antho.com.go4lunch.base;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;


import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import antho.com.go4lunch.R;
import butterknife.ButterKnife;
/** Implements base methods for all activities **/
public abstract class BaseActivity  extends AppCompatActivity
{
    // Set layout and support action bar
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());

        configureToolbar();
        ButterKnife.bind(this);
    }
    // Configure toolbar and set it as app action bar
    private void configureToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
    }

    // Helper method to display a toast
    protected void showToast(String message)
    {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    //
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    // Set a return value as a layout resource reference
    @LayoutRes
    protected abstract int layoutRes();
    //

}
