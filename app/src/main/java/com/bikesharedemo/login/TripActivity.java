package com.bikesharedemo.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener{
//        String URL_PATH = "https://bikesharedemo1.000webhostapp.com/coor.txt";
        String URL_PATH = "/api/us/path";
        String URL_STR = "https://roads.googleapis.com/v1/snapToRoads?";
        String token;
        Button btnBack;
        GoogleMap googleMap;

        // Mảng chứa tọa độ
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.polymap);
        mapFragment.getMapAsync(this);

        //Lấy token và bike id
        Intent intent = getIntent();
        //token = (String) intent.getSerializableExtra("token");
        token = GlobalVariables.token;

        btnBack = (Button) findViewById(R.id.btnBackToHomeScreen);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHome = new Intent(TripActivity.this, MapActivity.class);
                intentHome.putExtra("token", token);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                TripActivity.this.finish();
            }
        });
    }

    /*@Override
    protected void onResume(){
        super.onResume();
        getString(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                System.out.println("abc out"+ testClass.a);
            }
        });
    }*/

    private interface VolleyCallback{
        void onSuccess(String result);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        //Dùng GET để nhận respone từ server, file txt -> string
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_TXT,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        //Tạo mảng chứa các tọa độ
//                        ArrayList<LatLng> coordList = new ArrayList<LatLng>();
//                        //Tách String thành từng dòng
//                        String[] lines = response.split("\n");
//                        for (String line : lines) {
//                            //Tách mỗi dòng thành 2 phần Lat và Lng, thêm tọa độ vào mảng
//                            String[] LatLng = line.split(" ");
//                            coordList.add(new LatLng(Float.parseFloat(LatLng[0]),Float.parseFloat(LatLng[1])));
//                        }
//                        //Hiển thị Polylines gồm các điểm vừa thêm
//                        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                                .clickable(true)
//                                .addAll(coordList)
//                        );
//                        polyline1.setTag("B");
//                        stylePolyline(polyline1);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MyTripActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
//                        Log.d("AAA", "Error: " + error.toString());
//                    }
//                });
//        requestQueue.add(stringRequest);
//
//        LatLng HanoiCenter = new LatLng(21.018882087790026, 105.82220651143497);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.9908663,105.8578748), 17));
//
//        googleMap.setOnPolylineClickListener(this);
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Lấy token
        String token = (String) getIntent().getSerializableExtra("token");
        String contractid = (String) getIntent().getSerializableExtra("contractid");
        //String contractid = "182";

        Toast.makeText(TripActivity.this, "Contract id: "+contractid, Toast.LENGTH_LONG).show();

        //Tạo mảng chứa các tọa độ
//        ArrayList<LatLng> coordList = new ArrayList<LatLng>();
//        MyTripActivity ob = new MyTripActivity();

        //Dùng GET để nhận respone từ server
        getCoor(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                System.out.println("abc out coor"+ GlobalVariables.coor);
                try {
                    for (int j = 0; j < GlobalVariables.coor.length(); j++) {
                        JSONObject latlng = GlobalVariables.coor.getJSONObject(j);
                        LatLng locate = new LatLng(Double.parseDouble(latlng.getString("latitude")),Double.parseDouble(latlng.getString("longitude")));
                        System.out.println("abc LatLng "+ Double.parseDouble(latlng.getString("latitude"))+" "+Double.parseDouble(latlng.getString("longitude")));
                        coordList.add(locate);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate,17));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Hiển thị Polylines gồm các điểm vừa thêm
                Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .addAll(coordList)
                );
                polyline1.setTag("B");
                stylePolyline(polyline1);
            }
        }, contractid);

        //Dùng GET để nhận respone từ server
        /*RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.address)+URL_PATH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            testClass.a = 5;
                            System.out.println("abc in"+ testClass.a);

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equals("success")) {
                                Log.d("MyTrip", "GET được rồi nhé");
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    if(data.getJSONObject(i).getString("id").equals(contractid)){
                                        JSONObject data1 = data.getJSONObject(i);
                                        JSONArray coor = data1.getJSONArray("coordinates");
//                                        Toast.makeText(MyTripActivity.this, "GET data Success: "+ coor, Toast.LENGTH_LONG).show();
                                        for (int j = 0; j < coor.length(); j++) {
                                            JSONObject latlng = coor.getJSONObject(j);
                                            LatLng locate = new LatLng(Float.parseFloat(latlng.getString("latitude")),Float.parseFloat(latlng.getString("longitude")));
                                            coordList.add(locate);
//                                            testToancuc = "chuoi thay doi";
                                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate,17));

//                                            snaptoroad(ob);
//                                            String URL_str = URL_STR + "interpolate=true&path=";
//                                            JSONObject latlng1 = coor.getJSONObject(j);
//                                            JSONObject latlng2 = coor.getJSONObject(j+1);
//                                            URL_str = URL_str + latlng1.getString("latitude") + "," + latlng1.getString("longitude")
//                                                    +"|" + latlng2.getString("latitude") + "," + latlng2.getString("longitude")
//                                                    + "&key=" + getString(R.string.map_api_key);
//
//                                            Log.d("AAA", "URL là: " + URL_str);
//                                            URL_str = URL_STR;
                                        }
                                    }
                                }
                            } else {
                                Log.d("MyTrip", "GET lỗi nhé");
                                Toast.makeText(MyTripActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


//                        //Hiển thị Polylines gồm các điểm vừa thêm
                        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                                .addAll(coordList)
                        );
                        polyline1.setTag("B");
                        stylePolyline(polyline1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyTripActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Error: " + error.toString());
                    }
                }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("token", token);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
        System.out.println("abc out"+testClass.a);*/
//        Toast.makeText(MyTripActivity.this, String.valueOf(testClass.a), Toast.LENGTH_LONG).show();

        //Hiển thị Polylines gồm các điểm vừa thêm
//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .addAll(ob.coordList)
//        );
//        polyline1.setTag("B");
//        stylePolyline(polyline1);

        LatLng HanoiCenter = new LatLng(21.018882087790026, 105.82220651143497);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.9908663,105.8578748), 17));

        googleMap.setOnPolylineClickListener(this);
    }
    // [END maps_poly_activity_on_map_ready]

    private void getCoor(final VolleyCallback callback, String contractid){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.address)+URL_PATH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("abc in coor"+ GlobalVariables.coor);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equals("success")) {
                                Log.d("MyTrip", "GET được rồi nhé");
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    if(data.getJSONObject(i).getString("contractId").equals(contractid)){
                                        JSONObject data1 = data.getJSONObject(i);
                                        JSONArray coor = data1.getJSONArray("coordinates");
                                        GlobalVariables.coor = coor;
                                        Toast.makeText(TripActivity.this, "GET data Success: " + GlobalVariables.coor, Toast.LENGTH_LONG).show();
                                    }
//                                    else{
//                                        Toast.makeText(TripActivity.this, "GET data Fail: " + GlobalVariables.coor, Toast.LENGTH_LONG).show();
//                                    }
                                }
                            } else {
                                Log.d("MyTrip", "GET lỗi nhé");
                                Toast.makeText(TripActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TripActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Error: " + error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("token", token);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

     void snaptoroad(){
//         System.out.println("df"+ checkSocket.checksocket);
    }

    // [START maps_poly_activity_style_polyline]
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    // [END maps_poly_activity_style_polyline]
    // [START maps_poly_activity_on_polyline_click]
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
            Toast.makeText(this, "Đường nét đứt",
                    Toast.LENGTH_SHORT).show();
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
            Toast.makeText(this, "Đường nét liền",
                    Toast.LENGTH_SHORT).show();
        }
    }
    // [END maps_poly_activity_on_polyline_click]

    // [START maps_poly_activity_style_polygon]
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_DARK_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_LIGHT_GREEN_ARGB = 0xff81C784;
    private static final int COLOR_DARK_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_LIGHT_ORANGE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);
}