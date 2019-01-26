package fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Config.ConstValue;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("deprecation")
public class Contact extends Fragment {


    ImageView contactIv;
    TextView mailTv ,numberTv;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us, container, false);
        hitUrlForContactUs();
        contactIv = view.findViewById(R.id.wallet_iv);
        mailTv = view.findViewById(R.id.mail_tv);
        numberTv = view.findViewById(R.id.pre_num_tv);
        progressBar = view.findViewById(R.id.contact_progress);
        progressBar.setVisibility(View.VISIBLE);
        mailTv.setClickable(true);
       // textView.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
    private void hitUrlForContactUs()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.CONTACT_US, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject contact = jsonArray.getJSONObject(0);
                        mailTv.setText(contact.getString("email"));
                        numberTv.setText(contact.getString("phone"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("Error", String.valueOf(error));
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }
}
