package antho.com.go4lunch.view.fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import antho.com.go4lunch.R;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.view.fragments.adapter.WorkmatesAdapter;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;

import android.view.View;

import java.util.Objects;

/** Creates fragment to display workmates list **/
public class WorkmatesFragment extends BaseFragment
{
    @BindView(R.id.workmates_recyclerview) RecyclerView workmatesRecyclerView;
    private WorkmateViewModel viewModel;
    // Required empty public constructor
    public WorkmatesFragment() {}
    // Return new instance of workmates fragment
    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }
    // Set viewmodel for workmates fragment
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get("WorkmateViewModel", WorkmateViewModel.class);
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        observeViewModel();
    }
    // Observe viewmodel to update restaurant recycler view
    private void observeViewModel()
    {
        viewModel.getWorkmates().observe(Objects.requireNonNull(getActivity()), workmates ->
        {
            workmatesRecyclerView.setAdapter(new WorkmatesAdapter(((WorkmatesAdapter.OnWorkmateClickedListener) getActivity())));
            WorkmatesAdapter adapter = (WorkmatesAdapter) workmatesRecyclerView.getAdapter();
            Objects.requireNonNull(adapter).setData(workmates);
        });
    }
    // Return fragment layout
    @Override
    public int layoutRes() { return R.layout.fragment_workmates; }
}


