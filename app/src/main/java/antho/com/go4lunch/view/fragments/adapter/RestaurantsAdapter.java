package antho.com.go4lunch.view.fragments.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.Restaurant;
import antho.com.go4lunch.viewmodel.RestaurantViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>
{
    //private final OnRestaurantClickListener listener;
    private final List<Restaurant> restaurants;// = new ArrayList<>();
    // Constructor
    public RestaurantsAdapter (/*RestaurantViewModel viewModel, LifecycleOwner owner*/List<Restaurant> restaurants/*OnRestaurantClickListener listener*/) {
        //this.listener = listener;
        //viewModel.getRestaurants().observe(owner, this::setData);
        this.restaurants = restaurants;

    }
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        return new RestaurantViewHolder(itemView/*, listener*/);
    }
    // Bind data to the view items
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position)
    {

        holder.bind(restaurants.get(position));
    }
    @Override
    public int getItemCount()
    {
        return restaurants.size();
    }
    // Add news list data if available
    /*public interface OnRestaurantClickListener
    {
        void onItemClicked();
    }*/
    public void setData(List<Restaurant> restaurants)
    {

        if (restaurants != null)
        {
            //DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NewsDiffCallback(data, newsList));
            restaurants.clear();
            restaurants.addAll(restaurants);
            //diffResult.dispatchUpdatesTo(this);
        }
        else
        {
            restaurants.clear();
            notifyDataSetChanged();
        }
        notifyDataSetChanged();
    }
    static final class RestaurantViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.section)
        TextView name;
        private Restaurant restaurant;
        RestaurantViewHolder(View itemView/*, OnRestaurantClickListener listener*/)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //itemView.setOnClickListener(v -> listener.onItemClicked());
        }
        void bind(Restaurant restaurant)
        {
            this.restaurant = restaurant;
            name.setText(restaurant.name());

        }
    }
}

