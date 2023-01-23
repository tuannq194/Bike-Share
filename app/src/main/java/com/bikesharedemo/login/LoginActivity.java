package com.bikesharedemo.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassWord;
    Button btnLogin, btnRegister;
    String URL_LOGIN = "/api/common/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        SocketClientContract socketClientContract = new SocketClientContract();
//        socketClientContract.subscriberStomp(1);


        AnhXa();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtEmail.getText().toString().trim();
                String password = edtPassWord.getText().toString().trim();

                if(username.isEmpty()||password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter full information", Toast.LENGTH_SHORT).show();
                }else{

                    loginAccount(getString(R.string.address)+URL_LOGIN);
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AnhXa() {
        edtEmail = (EditText) findViewById(R.id.editEmail);
        edtPassWord = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnConfirm);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

//    private void loginAccount(String url) {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        String message;
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if (jsonObject.getString("message").equals("success")) {
////                                Account account = new Account();
////                                account.setUserName(jsonObject.getString("user_name"));
////                                account.setEmail(jsonObject.getString("email"));
////                                message = jsonObject.getString("message");
////                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
//
//                                Intent intentMenu = new Intent(LoginActivity.this, MapActivity.class);
////                                intentMenu.putExtra("login", account);
//                                startActivity(intentMenu);
//                            } else {
//                                message = jsonObject.getString("message");
//                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(LoginActivity.this, "onErrorResponse!", Toast.LENGTH_SHORT).show();
//                        Log.d("AAA", "Error: " + error.toString());
//                    }
//                }) {
////            @Nullable
////            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////                Map<String, String> params = new HashMap<>();
////                params.put("email", edtEmail.getText().toString().trim());
////                params.put("password", edtPassWord.getText().toString().trim());
////                return params;
////            }
//
//        };
//        requestQueue.add(stringRequest);
//    }

    private void loginAccount(String url) {
        JSONObject js = new JSONObject();
        try {
            js.put("email", edtEmail.getText().toString().trim());
            js.put("password", edtPassWord.getText().toString().trim());
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
                                //Tạo Account chứa role và token người dùng
                                AccountToken account = new AccountToken();
                                account.setRole(data.getString("role"));
                                account.setToken(data.getString("token"));
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                                GlobalVariables.token = data.getString("token");
                                Intent intentMenu = new Intent(LoginActivity.this, MapActivity.class);
                                intentMenu.putExtra("token", data.getString("token"));
                                startActivity(intentMenu);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, response.getString("data"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Volley Error!", Toast.LENGTH_SHORT).show();
                Log.d("AAA", "Lỗi" + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjReq);
    }
}
