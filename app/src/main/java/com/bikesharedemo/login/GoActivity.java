package com.bikesharedemo.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.reactivex.disposables.CompositeDisposable;
import ua.naiksoftware.stomp.StompClient;

public class GoActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback{
    private GoogleMap map;
    MqttAndroidClient client;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    Button btnLock;
    String URL_BIKE = "/api/common/bike/";
    String token, bikeid, contractid;

    //Socket
    private CompositeDisposable compositeDisposable;
    private StompClient mStompClient;
    private static final String TAG = "SocketClientContract";

    SocketClientContract socketClientContract = new SocketClientContract();
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Hien thi ban do
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.myMapGo);
        mapFragment.getMapAsync(this);
        //Lấy token và bike id
        Intent intent = getIntent();
        //token = (String) intent.getSerializableExtra("token");
        token = GlobalVariables.token;
        bikeid = (String) intent.getSerializableExtra("bike");
        contractid = (String) intent.getSerializableExtra("contractid");
        Toast.makeText(GoActivity.this, "Contract id: "+contractid, Toast.LENGTH_LONG).show();

        //Tạo contract socket
//        SocketClientContract();
        socketClientContract.subscriberStomp(bikeid);

        //Nút kết nối lại MQTT hoặc socket trong trường hợp time out khi chạy nền
        btnLock = (Button) findViewById(R.id.btnLock) ;
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(socketClientContract.subscriberStomp(bikeid)).equals("cl")){
                    Intent intentTrip = new Intent(GoActivity.this, TripActivity.class);
                    intentTrip.putExtra("bike", bikeid);
                    intentTrip.putExtra("token", token);
                    intentTrip.putExtra("contractid", contractid);
                    intentTrip.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentTrip);
                    GoActivity.this.finish();
                    socketClientContract.unSubscribe();
                }else{
                    Toast.makeText(GoActivity.this, "Lock The Bike To End Ride", Toast.LENGTH_LONG).show();

                }
            }
        });

//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setUserName("sanslab");
//        options.setPassword("1".toCharArray());
//
//        String clientId = MqttClient.generateClientId();
//        client = new MqttAndroidClient(GoActivity.this, "tcp://155.248.164.224:1883",
//                clientId);
//        client.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                Log.d("MQTT", message.toString());
//                Toast.makeText(GoActivity.this, message.toString(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//        try {
//            IMqttToken tokenMQTT = client.connect(options);
//            tokenMQTT.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    // We are connected
//                    Log.d("MQTT", "onSuccess");
//                    sub("upstream/"+bikeid);
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    // Something went wrong e.g. connection timeout or firewall problems
//                    Log.d("MQTT", "onFailure");
//
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
    }
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

    //Setup MQTT
    private void setupMQTT(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(getString(R.string.MQTTusername));
        options.setPassword(getString(R.string.MQTTpassword).toCharArray());

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(GoActivity.this, getString(R.string.addressMQTT),
                clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("MQTT Go", message.toString());
                if(message.toString().equals("cl")){
                    Toast.makeText(GoActivity.this, "Lock Success", Toast.LENGTH_LONG).show();

                    Intent intentTrip = new Intent(GoActivity.this, TripActivity.class);
                    intentTrip.putExtra("bike", bikeid);
                    intentTrip.putExtra("token", token);
                    intentTrip.putExtra("contractid", contractid);
                    intentTrip.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentTrip);
                    GoActivity.this.finish();
                }else{
                    Toast.makeText(GoActivity.this, "Lock Fail", Toast.LENGTH_LONG).show();
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
                    Log.d("MQTT", "onSuccess");
                    sub("upstream/"+bikeid);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MQTT", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Hàm Subcribe MQTT
    void sub(String topic){
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("MQTT", "onSuccess Sub MQTT");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Log.d("MQTT", "onFailure Sub MQTT");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /*private void SocketClientContract(){
        this.mStompClient = Stomp.over(Stomp.ConnectionProvider.JWS, "ws://" + Common.DOMAIN
                + ":" + Common.SERVER_PORT + "/example-endpoint/websocket");
        mStompClient.connect();
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void subscriberStomp(int bikeId) {
        resetSubscriptions();
        Disposable disposable = mStompClient.topic("/topic/greetings/" + bikeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    System.out.println("aaaa " + topicMessage.getPayload());
                    if(topicMessage.getPayload().equals("cl")){
                        Toast.makeText(GoActivity.this, "Lock Success", Toast.LENGTH_LONG).show();
                        Intent intentTrip = new Intent(GoActivity.this, MyTripActivity.class);
                        intentTrip.putExtra("bike", bikeid);
                        intentTrip.putExtra("token", token);
                        intentTrip.putExtra("contractid", contractid);
                        intentTrip.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentTrip);
                        GoActivity.this.finish();


                    }
                    else{
                        Toast.makeText(GoActivity.this, "Lock Fail", Toast.LENGTH_LONG).show();
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        compositeDisposable.add(disposable);
    }

    public void unSubscribe(){
        if(mStompClient.isConnected()){
            mStompClient.disconnect();
        }
    }*/

}