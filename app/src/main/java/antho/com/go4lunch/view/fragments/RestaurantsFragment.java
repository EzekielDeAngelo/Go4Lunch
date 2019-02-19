package antho.com.go4lunch.view.fragments;
/** **/
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.model.restaurant.places.Place;
import antho.com.go4lunch.view.fragments.adapter.RestaurantsAdapter;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import butterknife.BindView;

import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.Marker;

import java.util.List;
/** **/
public class RestaurantsFragment extends BaseFragment
{
    @BindView(R.id.restaurant_recyclerview) RecyclerView restaurantRecyclerView;
    private RestaurantViewModel viewModel;
    // Required empty public constructor
    public RestaurantsFragment() {}
    //
    public static RestaurantsFragment newInstance() { return new RestaurantsFragment(); }
    //
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get("RestaurantViewModel", RestaurantViewModel.class);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        observeViewModel();
    }
    public void onResume()
    {
        super.onResume();
        observeViewModel();
    }

    //
    private void observeViewModel()
    {

        viewModel.getPlaces().observe(this, places ->
        {
            Log.d("rotototo", String.valueOf(places.size()));

            restaurantRecyclerView.setAdapter(new RestaurantsAdapter(((RestaurantsAdapter.OnRestaurantClickedListener) getActivity())));
                RestaurantsAdapter adapter = (RestaurantsAdapter) restaurantRecyclerView.getAdapter();
                adapter.setData(places);
         /*   viewModel.getPlace((String) marker.getTag()).observe(getActivity(), place ->
            {
                selectedPlace = place;
            });*/
        });
    }
    // Return fragment layout
    @Override
    public int layoutRes() { return R.layout.fragment_list; }
}
