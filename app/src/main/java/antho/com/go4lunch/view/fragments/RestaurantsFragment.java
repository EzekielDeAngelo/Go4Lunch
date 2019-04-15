package antho.com.go4lunch.view.fragments;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.view.fragments.adapter.RestaurantsAdapter;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import butterknife.BindView;

import android.view.View;
/** Creates fragment to display restaurants list **/
public class RestaurantsFragment extends BaseFragment
{
    @BindView(R.id.restaurant_recyclerview) RecyclerView restaurantRecyclerView;
    private RestaurantViewModel viewModel;
    // Required empty public constructor
    public RestaurantsFragment() {}
    // Return new instance of restaurant fragment
    public static RestaurantsFragment newInstance() { return new RestaurantsFragment(); }
    // Set viewmodel for restaurants fragment
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get("RestaurantViewModel", RestaurantViewModel.class);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        observeViewModel();
    }/*
    public void onResume()
    {
        super.onResume();
        observeViewModel();
        adapter.notifyDataSetChanged();
    }*/
    // Observe viewmodel to update restaurant recycler view
    public void observeViewModel()
    {
        viewModel.getPlaces().observe(this, places ->
        {
            restaurantRecyclerView.setAdapter(new RestaurantsAdapter(((RestaurantsAdapter.OnRestaurantClickedListener) getActivity())));
            RestaurantsAdapter adapter = (RestaurantsAdapter) restaurantRecyclerView.getAdapter();
            adapter.setData(places);
        });
    }
    // Return fragment layout
    @Override
    public int layoutRes() { return R.layout.fragment_list; }
}
