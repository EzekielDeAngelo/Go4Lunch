package antho.com.go4lunch.view.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import antho.com.go4lunch.R;
import antho.com.go4lunch.base.BaseActivity;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);

    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_settings;
    }
}