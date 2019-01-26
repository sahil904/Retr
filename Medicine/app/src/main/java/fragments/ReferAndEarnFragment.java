package fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medi.R;

import static android.content.Context.MODE_PRIVATE;

public class ReferAndEarnFragment extends Fragment {

    SharedPreferences sharedPreferences;
    String link = "http://freshvegi.in/app/app-debug.apk";
    String description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
       // share.putExtra(Intent.EXTRA_TITLE, title + "\n" + description);

        share.putExtra(Intent.EXTRA_TEXT, "Hey, This is awesome app for fresh fruit and vegetable .\n Redeem it at " + link);
        getActivity().startActivity(Intent.createChooser(share, "Share using"));
        return view;
    }
}