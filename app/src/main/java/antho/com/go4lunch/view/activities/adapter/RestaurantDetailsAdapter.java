package antho.com.go4lunch.view.activities.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.workmate.Workmate;
import antho.com.go4lunch.view.fragments.adapter.RestaurantsAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantDetailsAdapter extends RecyclerView.Adapter<RestaurantDetailsAdapter.RestaurantDetailsViewHolder>
{

    private List<Workmate> workmates = new ArrayList<>();
    public RestaurantDetailsAdapter()
    {

    }
    @NonNull
    @Override
    public RestaurantDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        return new RestaurantDetailsViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailsViewHolder holder, int position)
    {
        holder.bind(workmates.get(position));

    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

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
                    workmates.add(workmateList.get(i));}
            }
            //workmates.addAll(workmateList);
            notifyDataSetChanged();
        }
        else {
            workmates.clear();
            notifyDataSetChanged();
        }

    }
    static final class RestaurantDetailsViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.name) TextView name;
        //private Workmate workmate;
        RestaurantDetailsViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
        void bind(Workmate workmate)
        {

          //  this.workmate = workmate;
            name.setText(workmate.name + " will have lunch here!");
        }
    }

    public interface OnRestaurantDetailsClickedListener
    {
        void onItemClicked(String id);
    }
}
