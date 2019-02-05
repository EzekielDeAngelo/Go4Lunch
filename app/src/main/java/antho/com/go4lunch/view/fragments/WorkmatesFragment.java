package antho.com.go4lunch.view.fragments;
/** **/
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import antho.com.go4lunch.R;
import antho.com.go4lunch.base.BaseFragment;
import antho.com.go4lunch.model.workmate.Workmate;
import antho.com.go4lunch.view.fragments.adapter.WorkmatesAdapter;
import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import butterknife.BindView;

import android.util.Log;
import android.view.View;

import java.util.List;
/** **/
public class WorkmatesFragment extends BaseFragment {

    private WorkmateViewModel viewModel;
    @BindView(R.id.workmates_recyclerview)
    RecyclerView workmatesRecyclerView;

    // Required empty public constructor
    public WorkmatesFragment() {
    }

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    //
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //viewModel = ViewModelProviders.of(getActivity()).get(WorkmateViewModel.class);
        viewModel = ViewModelProviders.of(getActivity()).get("WorkmateViewModel", WorkmateViewModel.class);
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      //
        workmatesRecyclerView.setAdapter(new WorkmatesAdapter(viewModel, this));
        observeViewModel();
    }
public void onResume(){super.onResume();  }
    //
    public void observeViewModel()
    {
        viewModel.getWorkmates().observe(getActivity(), workmates ->
        {

            WorkmatesAdapter adapter = (WorkmatesAdapter) workmatesRecyclerView.getAdapter();
            Log.d("SELECTED_RESTAURANT", workmates.get(0).restaurantId);
            Log.d("NUMBER", String.valueOf(workmates.size()));
            adapter.setData(workmates);


        });
        /*
        viewModel.getWorkmates().observe(this, new Observer<List<Workmate>>() {
            @Override
            public void onChanged(@Nullable List<Workmate> workmates) {
                workmatesRecyclerView.setAdapter(new WorkmatesAdapter(workmates));
                //WorkmatesAdapter adapter = (WorkmatesAdapter) workmatesRecyclerView.getAdapter();
                //adapter.setData(workmates);
            }
        });*/
    }

    // Return fragment layout

    @Override
    public int layoutRes() { return R.layout.fragment_workmates; }
}


