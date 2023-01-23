package com.bikesharedemo.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private GoogleMap map;
    private Button btnMokhoa;
    private Account account;
    private DrawerLayout drawer;

    String LOGOUT_URL = "/api/common/logout/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Hien thi ban do
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.myMap);
        mapFragment.getMapAsync(this);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Lấy token
        //Intent intent = getIntent();
        //String token = (String) intent.getSerializableExtra("token");
        String token = GlobalVariables.token;

        btnMokhoa = (Button)findViewById(R.id.btn_moKhoa);
        btnMokhoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentQR = new Intent(MapActivity.this, QRActivity.class);
                intentQR.putExtra("token", token);
                startActivity(intentQR);
            }
        });

//        //lay thong tin account
//        Intent intentMenu = getIntent();
//        account = new Account();
//        account = (Account) intentMenu.getSerializableExtra("login");
//
//        // Lay thong tin Account hien thi len Nav_Header
//        View headerView = navigationView.getHeaderView(0);
//        TextView txtEmailMenu = (TextView) headerView.findViewById(R.id.txtEmail);
//        TextView txtUserNameMenu = (TextView) headerView.findViewById(R.id.txtUserName);
//
//        txtUserNameMenu.setText(account.getUserName());
//        txtEmailMenu.setText(account.getEmail());

        //Hien thi nut dieu huong Menu tren Toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //Start select Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:

                break;
            case R.id.nav_profile:
                Intent intentProfile = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_wallet:
                Intent intentWallet = new Intent(MapActivity.this, WalletActivity.class);
                //startActivity(intentWallet);
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_buy:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_trip:
                //Gửi token qua MyTrip
                Intent intentMyTrip = new Intent(MapActivity.this, TripActivity.class);
                //intentMyTrip.putExtra("token", GlobalVariables.token);
                startActivity(intentMyTrip);
                break;
            case R.id.nav_message:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new MapFragment()).commit();
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                logoutAccount(getString(R.string.address)+LOGOUT_URL,GlobalVariables.token);
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_introduction:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutAccount(String url, String token){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("message").equals("success")){
                                Toast.makeText(MapActivity.this, "Logout Success", Toast.LENGTH_LONG).show();
                                Intent intentLogout = new Intent(MapActivity.this, LoginActivity.class);
                                startActivity(intentLogout);
                                finish();
                            }else{
                                Toast.makeText(MapActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MapActivity.this, "Logout onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("Logout", "Error: " + error.toString());
                    }
                });
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //End Select Menu
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng HanoiCenter = new LatLng(21.018882087790026, 105.82220651143497);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HanoiCenter, 13));

        LatLng BachKhoa = new LatLng(21.00501508824734, 105.84418941670367);
        map.addMarker(new MarkerOptions()
                .position(BachKhoa)
                .title("Trạm Bách khoa")
                .snippet("1 xe sẵn sàng sử dụng \n Còn 9 chỗ đỗ xe"));

        LatLng Lotte = new LatLng(21.031782162487815, 105.8122186440731);
        map.addMarker(new MarkerOptions()
                .position(Lotte)
                .title("Trạm Lotte Center")
                .snippet("2 xe sẵn sàng sử dụng \n Còn 8 chỗ đỗ xe"));

        LatLng HoTay = new LatLng(21.04355158699972, 105.8350713228811);
        map.addMarker(new MarkerOptions()
                .position(HoTay)
                .title("Trạm Hồ Tây")
                .snippet("1 xe sẵn sàng sử dụng \n Còn 9 chỗ đỗ xe"));

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Get Current Location", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current Location", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}
