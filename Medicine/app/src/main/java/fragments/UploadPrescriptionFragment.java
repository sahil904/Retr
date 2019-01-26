package fragments;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medhealth.ChooseImageActivity;
import com.medi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Config.ConstValue;

@SuppressWarnings("deprecation")
public class UploadPrescriptionFragment extends Fragment {
    EditText nameEt,mobileEt,precsEt;
    RelativeLayout submitLayout;
    ProgressDialog dialog ;
    Button submitBtn;


    public UploadPrescriptionFragment() {

    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_prescription, container, false);
        Button textView = view.findViewById(R.id.choose_txt);
        nameEt = view.findViewById(R.id.name_et);
        dialog = new ProgressDialog(getActivity());
        mobileEt = view.findViewById(R.id.mobile_et);
        submitLayout = view.findViewById(R.id.submit_layout);
        precsEt = view.findViewById(R.id.precs_et);
        submitBtn = view.findViewById(R.id.submit_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEt.getText().toString().length()<1){
                    nameEt.setError("can't be empty");

                }else if (mobileEt.getText().toString().length()<10){

                    mobileEt.setError("can't be less than 10 digits");
                }
                else {
                    Intent intent = new Intent(getActivity(), ChooseImageActivity.class);
                    intent.putExtra("name", nameEt.getText().toString());
                    intent.putExtra("mobile", mobileEt.getText().toString());
                    getActivity().startActivity(intent);
                    nameEt.getText().clear();
                    mobileEt.getText().clear();
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEt.getText().toString().length()<1){
                    nameEt.setError("can't be empty");

                }else if (mobileEt.getText().toString().length()<10 & mobileEt.getText().toString().length()>10){
                    mobileEt.setError("can't be less than 10 digits");
                }
                else {
                    hitUrlForUploadPrescriptionWithoutImage();
                }
            }
        });

        return view;
    }
    private void hitUrlForUploadPrescriptionWithoutImage()
    {
       final ProgressDialog dialog1 =  ProgressDialog.show(getActivity(), "", "Loading....");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.PRISCRIPTION_FORM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                precsEt.getText().clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("responce").equalsIgnoreCase("false")){
                        Toast.makeText(getActivity(), ""+jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("ImageREs",response);
                dialog1.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Log.d("Error",String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name",nameEt.getText().toString());
                params.put("mobile",mobileEt.getText().toString());
                params.put("medicineName",precsEt.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue  = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
}
