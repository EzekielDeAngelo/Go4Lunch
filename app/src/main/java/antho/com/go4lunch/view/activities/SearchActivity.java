package antho.com.go4lunch.view.activities;

import android.app.AppComponentFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import antho.com.go4lunch.R;
import antho.com.go4lunch.base.BaseActivity;

public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected int layoutRes() {
        return 0;
    }
}