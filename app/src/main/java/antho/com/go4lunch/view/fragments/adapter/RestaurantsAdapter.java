package antho.com.go4lunch.view.fragments.adapter;
/** **/
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.restaurant.places.Place;
import butterknife.BindView;
import butterknife.ButterKnife;
/** **/
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>
{
    private final OnRestaurantClickedListener listener;
    private final List<Place> restaurants;// = new ArrayList<>();
    // Constructor
    public RestaurantsAdapter (List<Place> restaurants, OnRestaurantClickedListener listener)
    {
        this.listener = listener;
        this.restaurants = restaurants;
    }
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
    @Override
    public int getItemCount()
    {
        return restaurants.size();
    }
    // Add news list data if available
    public void setData(List<Place> places)
    {
        if (places != null)
        {
            //DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NewsDiffCallback(data, newsList));
            places.clear();
            places.addAll(places);
            //diffResult.dispatchUpdatesTo(this);
        }
        else
        {
            places.clear();
            notifyDataSetChanged();
        }
        notifyDataSetChanged();
    }
    //
    static final class RestaurantViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.adress) TextView address;
        @BindView(R.id.thumbnail) ImageView thumbnail;
        private Place place;
        RestaurantViewHolder(View itemView, OnRestaurantClickedListener listener)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> listener.onItemClicked(place.name(), place.adress(), place.thumb));
        }
        void bind(Place place)
        {
            this.place = place;
            name.setText(place.name());
            address.setText(place.adress());
            Picasso.Builder builder = new Picasso.Builder(thumbnail.getContext());
            builder.downloader(new OkHttp3Downloader(thumbnail.getContext()));
            builder.build().load(place.thumb)
                    .into(thumbnail);
        }
    }
    // On click listener with
    public interface OnRestaurantClickedListener
    {
        void onItemClicked(String name, String address, String photo);
    }
}

