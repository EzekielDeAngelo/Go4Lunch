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

import android.view.View;

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
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        observeViewModel();
    }
    //
    private void observeViewModel()
    {
        viewModel.getPlaces().observe(this, new Observer<List<Place>>()
        {
            @Override
            public void onChanged(@Nullable List<Place> places)
            {
                restaurantRecyclerView.setAdapter(new RestaurantsAdapter(places, ((RestaurantsAdapter.OnRestaurantClickedListener) getActivity())));
            }
        });
    }
    // Return fragment layout
    @Override
    public int layoutRes() { return R.layout.fragment_list; }
}
