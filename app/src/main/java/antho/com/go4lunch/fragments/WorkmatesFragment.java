package antho.com.go4lunch.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import antho.com.go4lunch.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {


    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workmates, container, false);
    }

}
