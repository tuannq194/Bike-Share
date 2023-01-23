package com.bikesharedemo.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class QRActivity extends AppCompatActivity {
    Button btnClick;
    String bikeid;
    ImageView imgHinh;
    String URL_BIKE = "/api/common/bike/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnClick = (Button)findViewById(R.id.btnScan2);
        imgHinh = (ImageView)findViewById(R.id.imageHinh);
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentIntegrator.initiateScan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() == null) {
                Toast.makeText(QRActivity.this, "Scan Failed", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    //Lấy token
                    Intent intent = getIntent();
                    String token = (String) intent.getSerializableExtra("token");
                    //Lấy bike id
                    bikeid=jsonObject.getString("bikeid");
                    GetBikeInfo(getString(R.string.address)+URL_BIKE,bikeid,token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
    private void GetBikeInfo(String url, String bikeid, String token) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+bikeid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equals("success")) {
                                Toast.makeText(QRActivity.this, "Scan Success", Toast.LENGTH_LONG).show();
                                JSONObject data = jsonObject.getJSONObject("data");
                                String stationId = data.getString("stationId");

                                Intent intentQR = new Intent(QRActivity.this, QRResultActivity.class);
                                intentQR.putExtra("token", token);
                                intentQR.putExtra("bike", bikeid);
                                startActivity(intentQR);
                            } else {
                                Toast.makeText(QRActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QRActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Error: " + error.toString());
                    }
                });
        requestQueue.add(stringRequest);
    }
//    private void bike(String url) {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            //Gán dữ liệu respone vào bike
//                            BikeStatus bikeStatus = new BikeStatus();
//                            bikeStatus.setBikenumber(jsonObject.getString("BikeNumber"));
//                            bikeStatus.setBikebattery(jsonObject.getInt("BikeBattery"));
//                            bikeStatus.setBikestation(jsonObject.getString("BikeStation"));
//                            bikeStatus.setBikeaddress(jsonObject.getString("BikeAddress"));
//
//                            //Chuyển cảnh kèm dữ liệu bike :D
//                            Intent intentQR = new Intent(QRActivity.this, QRResultActivity.class);
//                            intentQR.putExtra("bike", bikeStatus);
//                            startActivity(intentQR);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(QRActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
//                        Log.d("AAA", "Error: " + error.toString());
//                    }
//                }) {
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("bike_id", bikeid.toString().trim());
//                return params;
//            }
//        };
//        requestQueue.add(stringRequest);
//    }

}
