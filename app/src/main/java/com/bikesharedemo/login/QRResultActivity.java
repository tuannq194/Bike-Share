package com.bikesharedemo.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import ua.naiksoftware.stomp.StompClient;

public class QRResultActivity extends AppCompatActivity {
    private BikeStatus bikestatus;
    MqttAndroidClient client;
    Button btnStart;
    String URL_BIKE     = "/api/common/bike/";
    String URL_BATTERY  = "/api/common/device/";
    String URL_STATION  = "/api/common/station/";
    String URL_RENTBIKE = "/api/us/rent-bike";
    String token, bikeid, contractid, paymentMethod, stationid;

    //Socket
    private CompositeDisposable compositeDisposable;
    private StompClient mStompClient;
    private static final String TAG = "SocketClientContract";
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrresult);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Select Method
        List<String> paymentMethods = Arrays.asList("5000VNĐ - 30 minutes","10000VNĐ - 60 minutes","20000VNĐ - 120 minutes");
        final Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        paymentMethod =  spinner.getSelectedItem().toString();

        //Lấy token và bike id
        Intent intent = getIntent();
        //token = (String) intent.getSerializableExtra("token");
        token = GlobalVariables.token;
        bikeid = (String) intent.getSerializableExtra("bike");
        GetBikeInfo(getString(R.string.address)+URL_BIKE,bikeid);
        GetBatteryInfo(getString(R.string.address)+URL_BATTERY,bikeid);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo Contract
                rentBike(getString(R.string.address)+URL_RENTBIKE);
//                setupMQTT();
            }
        });


        //lay thong tin account
//        Intent intentQR = getIntent();
//        bikestatus = new BikeStatus();
//        bikestatus = (BikeStatus) intentQR.getSerializableExtra("bike");
//
//        TextView txtbike_number_plate = (TextView) findViewById(R.id.txt_bike_number_plate);
//        TextView txtbike_battery = (TextView) findViewById(R.id.txt_bike_battery);
//        TextView txtbike_station = (TextView) findViewById(R.id.txt_bike_station);
//        TextView txtbike_station_add = (TextView) findViewById(R.id.txt_bike_station_add);
//
//        txtbike_number_plate.setText(bikestatus.getBikenumber());
//        txtbike_battery.setText(String.valueOf(bikestatus.getBikebattery()));
//        txtbike_station.setText(bikestatus.getBikestation());
//        txtbike_station_add.setText(bikestatus.getBikeaddress());
    }
    private void GetBikeInfo(String url, String bikeid) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+bikeid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equals("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                TextView txtbike_number_plate = (TextView) findViewById(R.id.txt_bike_number_plate);
//                                TextView txtbike_battery = (TextView) findViewById(R.id.txt_bike_battery);
//                                TextView txtbike_station = (TextView) findViewById(R.id.txt_bike_station);
//                                TextView txtbike_station_add = (TextView) findViewById(R.id.txt_bike_station_add);


                                txtbike_number_plate.setText(data.getString("frameNumber"));
//                                txtbike_battery.setText(String.valueOf(bikestatus.getBikebattery()));
//                                txtbike_station.setText(bikestatus.getBikestation());
//                                txtbike_station_add.setText(bikestatus.getBikeaddress());

                                stationid = data.getString("stationId");
                                GetStationInfo(getString(R.string.address)+URL_STATION,stationid);
                            } else {
                                Toast.makeText(QRResultActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QRResultActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Error: " + error.toString());
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void GetStationInfo(String url, String bikeid) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+bikeid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equals("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                TextView txtbike_station = (TextView) findViewById(R.id.txt_bike_station);
                                txtbike_station.setText(data.getString("name"));
                            } else {
                                Toast.makeText(QRResultActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QRResultActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Error: " + error.toString());
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void GetBatteryInfo(String url, String bikeid) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+bikeid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equals("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                TextView txtbike_battery = (TextView) findViewById(R.id.txt_bike_battery);
                                txtbike_battery.setText(data.getString("battery"));
                            } else {
                                Toast.makeText(QRResultActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QRResultActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Error: " + error.toString());
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void rentBike(String url) {
        JSONObject js = new JSONObject();
        try {
            js.put("bikeId", bikeid);
            js.put("paymentMethod", paymentMethod);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data;
                        try {
                            if (response.getString("message").equals("success")) {
                                data = response.getJSONObject("data");
                                contractid = data.getString("id");

                                Intent intentGo = new Intent(QRResultActivity.this, GoActivity.class);
                                intentGo.putExtra("bike", bikeid);
                                intentGo.putExtra("token", token);
                                intentGo.putExtra("contractid", contractid);
                                startActivity(intentGo);
                                finish();
                                Log.d("API Rent Bike","OK" );
                            } else {
                                Toast.makeText(QRResultActivity.this, response.getString("data"), Toast.LENGTH_LONG).show();
                                Log.d("API Rent Bike","Lỗi" );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QRResultActivity.this, "Volley Error!", Toast.LENGTH_SHORT).show();
                Log.d("AAA", "Lỗi" + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("token", token);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjReq);

    }
    //Setup MQTT
    void setupMQTT(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(getString(R.string.MQTTusername));
        options.setPassword(getString(R.string.MQTTpassword).toCharArray());

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(QRResultActivity.this, getString(R.string.addressMQTT),
                clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("MQTT QR Result", message.toString());
                if(message.toString().equals("op")){
                    Toast.makeText(QRResultActivity.this, "Unlock Success", Toast.LENGTH_LONG).show();
//                    Toast.makeText(QRResultActivity.this, "Contract id MQTT: "+ contractid, Toast.LENGTH_LONG).show();

                }  else {
                    Toast.makeText(QRResultActivity.this, "Unlock Fail", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        try {
            IMqttToken tokenMQTT = client.connect(options);
            tokenMQTT.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("MQTT QR Result", "onSuccess");
                    sub("downstream/"+bikeid);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MQTT QR Result", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Hàm Subcribe
    void sub(String topic){
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("MQTT QR Result", "onSuccess Sub MQTT");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Log.d("MQTT QR Result", "onFailure Sub MQTT");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}