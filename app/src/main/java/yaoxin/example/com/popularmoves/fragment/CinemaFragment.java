package yaoxin.example.com.popularmoves.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yaoxin.example.com.popularmoves.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CinemaFragment extends Fragment {


    public CinemaFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cinema, container, false);
    }

}
