package antho.com.go4lunch.view.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.view.fragments.adapter.RestaurantsAdapter;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends BaseFragment {

    private RestaurantViewModel viewModel;
    @BindView(R.id.restaurant_recyclerview) RecyclerView restaurantRecyclerView;
    public ListFragment() {
        // Required empty public constructor
    }
    //
    public static ListFragment newInstance()
    {

        return new ListFragment();
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);


        return view;
    }*/
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //restaurantRecyclerView.setAdapter(new RestaurantsAdapter(viewModel, this));


        viewModel.getRestaurants().observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(@Nullable List<Restaurant> restaurants)
            {
                restaurantRecyclerView.setAdapter(new RestaurantsAdapter(restaurants));
            }
        });
       // observeViewModel();
    }
    private void observeViewModel()
    {
        viewModel.getRestaurants().observe(this, restaurants ->
        {
            RestaurantsAdapter adapter = (RestaurantsAdapter) restaurantRecyclerView.getAdapter();
            adapter.setData(restaurants);
        });
    }
    // Return fragment layout
    @Override
    public int layoutRes() { return R.layout.fragment_list; }
}
