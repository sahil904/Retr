package fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrganicFruits extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.activity_products, container, false);
        return view;
    }

}
