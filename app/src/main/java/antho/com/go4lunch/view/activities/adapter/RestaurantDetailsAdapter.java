package antho.com.go4lunch.view.activities.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.workmate.Workmate;
import butterknife.BindView;
import butterknife.ButterKnife;
/** Adapter to create and populate recycler view for restaurant details **/
public class RestaurantDetailsAdapter extends RecyclerView.Adapter<RestaurantDetailsAdapter.RestaurantDetailsViewHolder>
{
    private final List<Workmate> workmates = new ArrayList<>();
    // Constructor
    public RestaurantDetailsAdapter() {}
    // Creates view for recycler view
    @NonNull
    @Override
    public RestaurantDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        return new RestaurantDetailsViewHolder(itemView);
    }
    // Bind data to the view items
    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailsViewHolder holder, int position)
    {
        holder.bind(workmates.get(position));
    }
    // Return the list size
    @Override
    public int getItemCount() {
        return workmates.size();
    }
    // Add workmates list data if available
    public void setData(List<Workmate> workmateList, String restaurantId)
    {
        if (workmateList != null)
        {
            workmates.clear();
            for (int i = 0; i < workmateList.size() ; i++)
            {
                if (workmateList.get(i).restaurantId != null)
                    if (workmateList.get(i).restaurantId.equals(restaurantId))
                    {
                        workmates.add(workmateList.get(i));
                    }
            }
        }
        else
        {
            workmates.clear();
            notifyDataSetChanged();
        }
    }
    // Provides a reference to the views for each data item
    static final class RestaurantDetailsViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.name) TextView name;
        // Bind view and set on click listener
        RestaurantDetailsViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
        // Bind data entries to appropriate view items
        void bind(Workmate workmate)
        {
            name.setText(workmate.name + " will have lunch here!");
        }
    }
}
