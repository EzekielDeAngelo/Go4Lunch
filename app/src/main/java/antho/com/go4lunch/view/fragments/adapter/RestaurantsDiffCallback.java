package antho.com.go4lunch.view.fragments.adapter;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.DiffUtil;
import antho.com.go4lunch.model.restaurant.places.Place;

/** Overrides diffUtil methods to improve software performance **/
class RestaurantsDiffCallback extends DiffUtil.Callback
{
    private final List<Place> oldList;
    private final List<Place> newList;
    //
    public RestaurantsDiffCallback(List<Place> oldList, List<Place> newList)
    {
        this.oldList = oldList;
        this.newList = newList;
    }
    //
    @Override
    public int getOldListSize()
    {
        return oldList.size();
    }
    //
    @Override
    public int getNewListSize()
    {
        return newList.size();
    }
    //
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
    {
        return Objects.equals(oldList.get(oldItemPosition).placeId, newList.get(newItemPosition).placeId);
    }
    //
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}

