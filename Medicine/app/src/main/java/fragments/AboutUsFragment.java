package fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medhealth.AboutUsActivity;
import com.medi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Config.ConstValue;


@SuppressWarnings("ALL")
public class AboutUsFragment extends Fragment {

    int tag =0;
    ProgressBar progressBar;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        progressBar = view.findViewById(R.id.about_pb);
        RelativeLayout privacyLayout = view.findViewById(R.id.privacy_layout);
        RelativeLayout termsConditionLayout = view.findViewById(R.id.terms_condition_layout);
        RelativeLayout faqLayout = view.findViewById(R.id.faq_layout);
        privacyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AboutUsActivity.class);
                intent.putExtra("page",1);
                startActivity(intent);

                /*progressBar.setVisibility(View.VISIBLE);
                hitUrlForAboutUs(0);*/

            }
        });

        termsConditionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AboutUsActivity.class);
                intent.putExtra("page",2);
                startActivity(intent);

                /*progressBar.setVisibility(View.VISIBLE);
                hitUrlForAboutUs(1);*/
            }
        });
        faqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AboutUsActivity.class);
                intent.putExtra("page",3);
                startActivity(intent);

                /*progressBar.setVisibility(View.VISIBLE);
                hitUrlForAboutUs(2);*/
            }
        });

        return view;
    }
    private void hitUrlForAboutUs(final int  tag){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.ABOUT_US, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    progressBar.setVisibility(View.GONE);
                    if (!jsonObject.getBoolean("error")){

                        Intent intent = new Intent(getActivity(), AboutUsActivity.class);

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject aboutUs = jsonArray.getJSONObject(tag);
                            intent.putExtra("title",aboutUs.getString("title"));
                            intent.putExtra("description",aboutUs.getString("description"));
                            intent.putExtra("id",aboutUs.getString("id"));
                            getActivity().startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


}
