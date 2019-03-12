package antho.com.go4lunch.view.fragments.adapter;
/** **/
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.model.workmate.Workmate;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
/** **/
public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder>
{
    private final List<Workmate> workmates = new ArrayList<>();
    // Constructor
    public WorkmatesAdapter (WorkmateViewModel viewModel, LifecycleOwner owner/*List<Workmate> workmates*/)
    {
        viewModel.getWorkmates().observe(owner, this::setData);
    }
    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        return new WorkmatesViewHolder(itemView);
    }
    // Bind data to the view items
    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position)
    {
        holder.bind(workmates.get(position));
    }
    @Override
    public int getItemCount()
    {
        return workmates.size();
    }
    // Add news list data if available
    public void setData(List<Workmate> workmatesList)
    {
        if (workmatesList.size() > 0)
        {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new WorkmatesDiffCallback(workmates, workmatesList));
            workmates.clear();
            workmates.addAll(workmatesList);
            diffResult.dispatchUpdatesTo(this);
        }
        else
        {
            workmates.clear();
            notifyDataSetChanged();
        }
        //notifyDataSetChanged();
    }
    static final class WorkmatesViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.address)
        TextView address;
        @BindView(R.id.thumbnail)
        ImageView thumbnail;
        private Workmate workmate;
        WorkmatesViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        void bind(Workmate workmate)
        {
            this.workmate = workmate;
            if (workmate.restaurantId == null)
                name.setText(workmate.name + " hasn't decided yet.");
            else
                name.setText(workmate.name + " is going to eat at " + workmate.restaurantId);
            /*address.setText(workmate.address());
            Picasso.Builder builder = new Picasso.Builder(thumbnail.getContext());
            builder.downloader(new OkHttp3Downloader(thumbnail.getContext()));
            builder.build().load(restaurant.thumb)
                    .into(thumbnail);*/
        }
    }
}
