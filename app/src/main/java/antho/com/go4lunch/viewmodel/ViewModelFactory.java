package antho.com.go4lunch.viewmodel;
/** **/
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
/** **/
public class ViewModelFactory implements ViewModelProvider.Factory
{
    private String mLocation;
    // Constructor
    public ViewModelFactory(@Nullable String location) {
        if (location != null) mLocation = location;
    }
    // Return viewmodel with given string as a parameter
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass)
    {
        return (T) new RestaurantViewModel(mLocation);
    }

}