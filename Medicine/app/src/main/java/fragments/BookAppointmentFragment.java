package fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medhealth.AppointmentForm;
import com.medi.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import Config.ConstValue;
import models.AppointmentModel;
import util.ConnectionDetector;


public class BookAppointmentFragment extends Fragment {

    ArrayList<AppointmentModel> al;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ConnectionDetector connectionDetector;
    public BookAppointmentFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_appointment, container, false);

        recyclerView = view.findViewById(R.id.appointment_recycler);
        connectionDetector = new ConnectionDetector(getActivity());
        progressBar = view.findViewById(R.id.progress_appointment);
        progressBar.setVisibility(View.VISIBLE);
        if (connectionDetector.isConnectingToInternet()){
            progressBar.setVisibility(View.GONE);
        }else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        al = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        hitUrlForAppointment();

        return view;
    }

    private void hitUrlForAppointment()
    {
        StringRequest   stringRequest = new StringRequest(Request.Method.POST, ConstValue.APPOINTMENT_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AppointMent",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    progressBar.setVisibility(View.GONE);
                    String respons = jsonObject.getString("responce");
                    if (respons.equals("error")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i =0; i<jsonArray.length(); i++){
                            AppointmentModel appointmentModel = new AppointmentModel();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            appointmentModel.setName(jsonObject1.getString("name"));
                            appointmentModel.setDescription(jsonObject1.getString("description"));
                            appointmentModel.setBanner(jsonObject1.getString("banner"));
                            appointmentModel.setId(jsonObject1.getString("id"));
                            appointmentModel.setStatus(jsonObject1.getString("status"));
                            al.add(appointmentModel);
                        }
                        BookAppointMentAdapter  bookAppointMentAdapter = new BookAppointMentAdapter(al,getActivity());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(bookAppointMentAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("Error",String.valueOf(error));
            }
        }){

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    class BookAppointMentAdapter extends RecyclerView.Adapter<BookAppointMentAdapter.AppointMentHolder>{
        ArrayList<AppointmentModel> al;
        Context context;

        public BookAppointMentAdapter(ArrayList<AppointmentModel> al, Context context) {
            this.al = al;
            this.context = context;
        }

        @NonNull
        @Override
        public BookAppointMentAdapter.AppointMentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.appointment_single_item,parent,false);
            return new AppointMentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookAppointMentAdapter.AppointMentHolder holder, int position) {
            AppointmentModel appointmentModel = al.get(position);
            if (Build.VERSION.SDK_INT >= 24)
            {
                holder.description.setText(Html.fromHtml(appointmentModel.getDescription(),Html.FROM_HTML_MODE_LEGACY));
            }
            else
            {
                holder.description.setText(Html.fromHtml(appointmentModel.getDescription()));
            }
           // Spanned result = Html.fromHtml();
            //holder.name.setText(result);
            holder.name.setText(appointmentModel.getName());
            Log.d("imgdsd",ConstValue.APPOINTMENT_IMAGE+appointmentModel.getBanner());
            Picasso.get().load(ConstValue.APPOINTMENT_IMAGE+appointmentModel.getBanner()).into(holder.profileImg);
        }

        @Override
        public int getItemCount() {
            return al.size();
        }

        public class AppointMentHolder extends RecyclerView.ViewHolder {
            ImageView profileImg;
            TextView name,description;
            RelativeLayout bookNowLayout;
            public AppointMentHolder(View itemView) {
                super(itemView);
                profileImg = itemView.findViewById(R.id.profile_img);
                name = itemView.findViewById(R.id.name_tv);
                description = itemView.findViewById(R.id.description_tv);
                bookNowLayout = itemView.findViewById(R.id.book_now_layout);
                bookNowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =  new Intent(context,AppointmentForm.class);
                        intent.putExtra("DoctorId",al.get(getAdapterPosition()).getId());
                        context.startActivity(intent);
                    }
                });
               // TextView bookNow = itemView.findViewById(R.id.booknow);

            }
        }
    }


}
