package antho.com.go4lunch.view.fragments.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.restaurant.places.Place;
import butterknife.BindView;
import butterknife.ButterKnife;
/**  Adapter to create and populate recycler view for restaurants **/
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>
{
    private final OnRestaurantClickedListener listener;
    private final List<Place> restaurants = new ArrayList<>();
    // Constructor
    public RestaurantsAdapter (OnRestaurantClickedListener listener)
    {
        this.listener = listener;
    }
    // Creates view for recycler view with on click listener parameter
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        return new RestaurantViewHolder(itemView, listener);
    }
    // Bind data to the view items
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position)
    {
        holder.bind(restaurants.get(position));
    }
    // Return the list size
    @Override
    public int getItemCount()
    {
        return restaurants.size();
    }
    // Add restaurant list data if available
    public void setData(List<Place> places)
    {
        if (places != null)
        {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RestaurantsDiffCallback(restaurants, places));
            restaurants.clear();
            restaurants.addAll(places);
            diffResult.dispatchUpdatesTo(this);
        }
        else
        {
            restaurants.clear();
            notifyDataSetChanged();
        }
    }
    // Provides a reference to the views for each data item
    static final class RestaurantViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.address) TextView address;
        @BindView(R.id.thumbnail) ImageView thumbnail;
        @BindView(R.id.stars) TextView stars;
        private Place place;
        // Bind view and set on click listener
        RestaurantViewHolder(View itemView, OnRestaurantClickedListener listener)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> listener.onItemClicked(place.placeId));
        }
        // Bind data entries to appropriate view items
        void bind(Place place)
        {
            this.place = place;
            name.setText(place.name());
            address.setText(place.address().substring(0, place.address().indexOf(",")));
            Picasso.Builder builder = new Picasso.Builder(thumbnail.getContext());
            builder.downloader(new OkHttp3Downloader(thumbnail.getContext()));
            builder.build().load(place.thumb)
                    .into(thumbnail);
        }
    }
    // On click listener with id as parameter to open restaurant details activity
    public interface OnRestaurantClickedListener
    {
        void onItemClicked(String id);
    }
}

