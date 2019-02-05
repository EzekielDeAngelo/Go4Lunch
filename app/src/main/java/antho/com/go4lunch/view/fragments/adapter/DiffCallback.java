package antho.com.go4lunch.view.fragments.adapter;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import antho.com.go4lunch.model.workmate.Workmate;

public class DiffCallback extends DiffUtil.Callback
{
    private final List<Workmate> oldList;
    private final List<Workmate> newList;
    //
    public DiffCallback(List<Workmate> oldList, List<Workmate> newList)
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
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }
    //
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}