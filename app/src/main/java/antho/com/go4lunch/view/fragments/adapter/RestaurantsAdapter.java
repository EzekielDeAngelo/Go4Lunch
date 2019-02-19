package antho.com.go4lunch.view.fragments.adapter;
/** **/
import android.util.Log;
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
/** **/
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>
{
    private final OnRestaurantClickedListener listener;
    private final List<Place> restaurants = new ArrayList<>();
    // Constructor
    public RestaurantsAdapter (OnRestaurantClickedListener listener)
    {
        this.listener = listener;
        //this.restaurants = restaurants;
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
        Log.d("onbindviewholder places", String.valueOf(position));
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
            for (int i = 0 ; i < places.size(); i++)
            {
                Log.d("setdata places", places.get(i).name() + " : " + String.valueOf(places.get(i).like));
            }
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RestaurantDiffCallback(restaurants, places));
            restaurants.clear();
            restaurants.addAll(places);
            diffResult.dispatchUpdatesTo(this);
            notifyDataSetChanged();
        }
        else
        {
            restaurants.clear();
            notifyDataSetChanged();
        }
        //notifyDataSetChanged();
    }
    //
    static final class RestaurantViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.address) TextView address;
        @BindView(R.id.thumbnail) ImageView thumbnail;
        @BindView(R.id.stars) TextView stars;
        private Place place;
        RestaurantViewHolder(View itemView, OnRestaurantClickedListener listener)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> listener.onItemClicked(place.placeId, place.name(), place.address(), place.thumb, place.phone(), place.website(), place.like));
        }
        void bind(Place place)
        {
            this.place = place;
            name.setText(place.name());
            address.setText(place.address());
            Log.d("hohohohuhuohihohio", String.valueOf(place.like));
            Picasso.Builder builder = new Picasso.Builder(thumbnail.getContext());
            builder.downloader(new OkHttp3Downloader(thumbnail.getContext()));
            builder.build().load(place.thumb)
                    .into(thumbnail);
        }
    }

    // On click listener with
    public interface OnRestaurantClickedListener
    {
        void onItemClicked(String id, String name, String address, String photo, String phone, String website, boolean like);
    }
}

