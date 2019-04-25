package antho.com.go4lunch.view.fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
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

import java.util.Objects;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get("RestaurantViewModel", RestaurantViewModel.class);
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
    private void observeViewModel()
    {

        viewModel.getPlaces().observe(this, places ->
        {

            for (int i = 0 ; i < places.size() ; i++)
            {
                String id = places.get(i).placeId;

                LiveData<Place> placeLiveData = viewModel.getPlaceLiveData();
                placeLiveData.observe(this, place ->
                {
                    if (place != null) {
                        Log.d("PROUTTAMERELAPUTE", place.placeId);
                        if (place.likeCount > 0)
                        {
                            Log.d("PROUTTAMERELAPUTE", place.placeId);
                            restaurantRecyclerView.setAdapter(new RestaurantsAdapter(((RestaurantsAdapter.OnRestaurantClickedListener) getActivity())));
                            RestaurantsAdapter adapter = (RestaurantsAdapter) restaurantRecyclerView.getAdapter();
                            Objects.requireNonNull(adapter).setData(places);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }


        });
    }
    // Return fragment layout
    @Override
    public int layoutRes() { return R.layout.fragment_list; }
}
