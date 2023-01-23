package com.bikesharedemo.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActiveActivity extends AppCompatActivity {
    EditText edtCode;
    TextView txtEmail;
    Button btnConfirm;
    String URL_ACTIVE = "/api/common/active/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_active);
        AnhXa();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Code = edtCode.getText().toString().trim();
                Active( getString(R.string.address)+URL_ACTIVE,Code);
            }
        });
    }
    private void AnhXa() {
        edtCode = (EditText) findViewById(R.id.editCode);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

    }
    private void Active(String url, String Code) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+Code,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equals("success")) {
                                Toast.makeText(RegisterActiveActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                                Intent intentMenu = new Intent(RegisterActiveActivity.this, LoginActivity.class);
                                startActivity(intentMenu);
                                finish();
                            } else {
                                Toast.makeText(RegisterActiveActivity.this, jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActiveActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Error: " + error.toString());
                    }
                });
        requestQueue.add(stringRequest);
    }
}