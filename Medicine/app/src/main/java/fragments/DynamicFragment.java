package fragments;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medi.R;
import com.squareup.picasso.Picasso;

import Config.ConstValue;

public class DynamicFragment extends Fragment {

    public SharedPreferences settings;

    public DynamicFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        settings = getActivity().getSharedPreferences(ConstValue.MAIN_PREF, 0);

        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);

        TextView  textView = view.findViewById(R.id.dynamic_text);
        TextView headLine = view.findViewById(R.id.head_line_txt);
        ImageView imageView = view.findViewById(R.id.news_image);
        Picasso.get().load(ConstValue.APPOINTMENT_IMAGE+settings.getString("icon","")).into(imageView);

        headLine.setText(settings.getString("title",""));
        Log.d("IconImage",ConstValue.APPOINTMENT_IMAGE+settings.getString("icon",""));

        //textView.setText(settings.getString("desc",""));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            textView.setText(Html.fromHtml(settings.getString("desc",""),Html.FROM_HTML_MODE_LEGACY));

        } else {
            textView.setText(Html.fromHtml(settings.getString("desc","")));
        }


        return view;
    }

}
