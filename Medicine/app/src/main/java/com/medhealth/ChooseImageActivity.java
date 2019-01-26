package com.medhealth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Config.ConstValue;
import util.VolleyMultipartRequest;

@SuppressWarnings("deprecation")
public class ChooseImageActivity extends AppCompatActivity {
    RelativeLayout cameraLayout,galleryLayout ,invisibleVisibleLayout;
    static final int PICK_IMAGE_REQUEST = 1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    static final int PICK_IMAGE_CAMERA = 2;
    String filePath;
    Bitmap imageBitmap;
    String mCurrentPhotoPath;
    ImageView prescriptionImage;
    Button uploadBtn;
    String name;
    ProgressDialog progressBar;
    String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        getSupportActionBar().setTitle("Upload Prescription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cameraLayout = findViewById(R.id.camera_layout);
        galleryLayout = findViewById(R.id.gallery_layout);
        uploadBtn = findViewById(R.id.upload_btn);
       // progressBar = findViewById(R.id.progress_upload);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Uploading Please Wait...");
        progressBar.setCancelable(false);

        prescriptionImage = findViewById(R.id.image_prescription);
        name  = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageBitmap != null) {
                    progressBar.show();
                    uploadBitmap(imageBitmap);
                } else {
                    Toast.makeText(getApplicationContext(), "Image not selected!", Toast.LENGTH_LONG).show();
                }
            }
        });
        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    openCamera();
                }
            }
        });

        galleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    imageBrowse();
                }
            }
        });
    }

    private void uploadBitmap(final Bitmap bitmap) {

        Log.d("imageUrlCal","Call");

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ConstValue.PRISCRIPTION_FORM,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String parsed;
                        try {
                            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        } catch (UnsupportedEncodingException e) {
                            parsed = new String(response.data);
                        }
                        Log.d("ParsedResponse",parsed);

                        progressBar.dismiss();
                        Toast.makeText(ChooseImageActivity.this, "Successfully Uploaded....", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            if (!obj.getBoolean("error")){
                                Log.d("Response",obj.getString("name"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("mobile",mobile);
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        /*RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(volleyMultipartRequest);*/
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void openCamera()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, PICK_IMAGE_CAMERA);
    }
    private void imageBrowse() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK & data !=null) {

            if (requestCode == PICK_IMAGE_REQUEST) {

                Uri picUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    prescriptionImage.setVisibility(View.VISIBLE);
                   // uploadBitmap(imageBitmap);
                    prescriptionImage.setImageBitmap(imageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //filePath = getPath(picUri);
        }
        }
            if (requestCode == PICK_IMAGE_CAMERA & resultCode == RESULT_OK &data != null) {

                    if (data.getExtras() != null) {
                        imageBitmap = (Bitmap) data.getExtras().get("data");
                        //uploadBitmap(imageBitmap);
                        prescriptionImage.setVisibility(View.VISIBLE);
                        prescriptionImage.setImageBitmap(imageBitmap);
                        Log.d("Uri", String.valueOf(imageBitmap));

                        String partFilename = currentDateFormat();

                        storeCameraPhotoInSDCard(imageBitmap, partFilename);

                        String storeFilename = "photo_" + partFilename + ".jpg";

                        Bitmap mBitmap = getImageFileFromSDCard(storeFilename);

                        galleryAddPic();
                }
            }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private String getPath(Uri contentUri) {
        if (contentUri!=null) {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        else {
            return null;
        }
    }

    private String currentDateFormat(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }
    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate){

        File outputFile = new File(Environment.getExternalStorageDirectory(),"photo_" + currentDate + ".jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageFileFromSDCard(String filename){
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory()+ "/"+ filename);
        filePath = imageFile.getAbsolutePath();
        Log.d("AbsFile",imageFile.getAbsolutePath());
        BitmapFactory.Options options=new BitmapFactory.Options();

        options.inSampleSize = 3;

        try {
            FileInputStream fis = new FileInputStream(imageFile);

            bitmap  =  BitmapFactory.decodeStream(fis, null, options);
            mCurrentPhotoPath = imageFile.getAbsolutePath();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    private  boolean checkAndRequestPermissions() {

        int cameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

}
