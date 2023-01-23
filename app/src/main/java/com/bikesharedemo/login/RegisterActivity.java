package com.bikesharedemo.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText edtEmail, edtPassWord, edtRePassWord, edtFirstName, edtLastName,
            edtBirth, edtGender, edtPhone, edtCreditCard, edtAddress,
            edtCity, edtDistrict, edtWard;
    private Button btnRegister;
    private Button btnLogin;
//    private ProgressDialog pDialog;

    String REGISTER_URL = "/api/common/register";
    String CITY_URL = "/api/common/city";
    String email, password,repassword,firstname,lastname,gender,phone, creditcard,address, district,ward;
    Integer city;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        selectCity();
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get data input
                email = edtEmail.getText().toString().trim();
                password = edtPassWord.getText().toString().trim();
                repassword = edtRePassWord.getText().toString().trim();
                firstname = edtFirstName.getText().toString().trim();
                lastname = edtLastName.getText().toString().trim();
                Date birth = Date.valueOf(edtBirth.getText().toString());
                gender = edtGender.getText().toString().trim();
                phone = edtPhone.getText().toString().trim();
                creditcard = edtCreditCard.getText().toString().trim();
                address = edtAddress.getText().toString().trim();
                //city = edtCity.getText().toString().trim();
                district = edtDistrict.getText().toString().trim();
                ward = edtWard.getText().toString().trim();

                //Call method register
                registerUser(email, password, repassword, firstname,lastname, birth, gender, phone, creditcard, address, city, district, ward);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addControls() {

        edtEmail = findViewById(R.id.editEmail);
        edtPassWord = findViewById(R.id.editPassword);
        edtRePassWord = findViewById(R.id.editRePassword);
        edtFirstName = findViewById(R.id.editFirstName);
        edtLastName = findViewById(R.id.editLastName);
        edtBirth = findViewById(R.id.editBirth);
        edtGender = findViewById(R.id.editGender);
        edtPhone = findViewById(R.id.editPhone);
        edtCreditCard = findViewById(R.id.editCreditCard);
        edtAddress = findViewById(R.id.editAddress);
//        edtCity = findViewById(R.id.editCity);
        edtDistrict = findViewById(R.id.editDistrict);
        edtWard = findViewById(R.id.editWard);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnConfirm);

//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Đang đăng ký...");
//        pDialog.setCanceledOnTouchOutside(false);
    }

    private void selectCity(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getString(R.string.address)+CITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("message").equals("success")){
                                JSONArray data = jsonObject.getJSONArray("data");
                                //Toast.makeText(RegisterActivity.this, "Data city: "+data.getJSONObject(0).getString("name"), Toast.LENGTH_SHORT).show();
                                //Select City
                                List<String> selectcity = new ArrayList<String>();
                                for (int i = 0; i < data.length();i++){
                                    String namecity = data.getJSONObject(i).getString("name");
                                    //Decode thành UTF-8
                                    try {
                                        namecity = new String(namecity.getBytes("ISO-8859-1"), "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    String decodedStr = namecity.toString();
                                    Log.d("City: ", decodedStr);
                                    selectcity.add(decodedStr);
                                }
                                final Spinner spinnercity = findViewById(R.id.spinnerCity);
                                ArrayAdapter adaptercity = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, selectcity);
                                adaptercity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnercity.setAdapter(adaptercity);

                                //String cityString =  spinner.getSelectedItem().toString();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);

    }


//    private void registerUser(final String username, final String password, final String email) {
//
//        if (checkEditText(edtUserName) && checkEditText(edtPassWord) && checkEditText(edtEmail) && isValidEmail(email)) {
////            pDialog.show();
//            StringRequest registerRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.d(TAG, response);
//                            String message = "";
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                if (jsonObject.getInt("success") == 1) {
//                                    Account account = new Account();
//                                    account.setUserName(jsonObject.getString("user_name"));
//                                    account.setEmail(jsonObject.getString("email"));
//                                    message = jsonObject.getString("message");
//                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
//                                    //Start LoginActivity
//                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                } else {
//                                    message = jsonObject.getString("message");
//                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
//                                }
//                            } catch (JSONException error) {
//                                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                            }
////                            pDialog.dismiss();
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            VolleyLog.d(TAG, "Error: " + error.getMessage());
////                            pDialog.dismiss();
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put(KEY_USERNAME, username);
//                    params.put(KEY_PASSWORD, password);
//                    params.put(KEY_EMAIL, email);
//                    return params;
//                }
//
//            };
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(registerRequest);
//        }
//    }

//    private void registerUser(String url) {
    private void registerUser(final String email, final String password, final String repassword,
                              final String firstname, final String lastname, final Date birth, final String gender,
                              final String phone, final String creditcard, final String address,
                              final Integer city, final String district, final String ward
    ) {
        if (checkEditText(edtEmail) && isValidEmail(email) && checkEditText(edtPassWord) && checkEditText(edtRePassWord) &&
                checkRePassword(edtPassWord,edtRePassWord) && checkEditText(edtFirstName) && checkEditText(edtLastName) && checkEditText(edtBirth) &&
                checkEditText(edtGender) && checkEditText(edtPhone) && checkEditText(edtCreditCard) && checkEditText(edtAddress)) {
//            pDialog.show();
            JSONObject js = new JSONObject();
            try {
                js.put("email", email);
                js.put("password", password);
                js.put("rePassword", repassword);
                js.put("firstname", firstname);
                js.put("lastname", lastname);
                js.put("birthday", birth);
                js.put("gender", gender);
                js.put("phone", phone);
                js.put("creditCard", creditcard);
                js.put("address", address);

                js.put("city", city);
//                js.put("district", district);
//                js.put("district", ward);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,  getString(R.string.address)+REGISTER_URL, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("message").equals("success")) {
                                    Toast.makeText(RegisterActivity.this, response.getString("data"), Toast.LENGTH_SHORT).show();

                                    Intent intentRegister = new Intent(RegisterActivity.this, RegisterActiveActivity.class);
                                    startActivity(intentRegister);
                                } else {
                                    Toast.makeText(RegisterActivity.this, response.getString("data"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegisterActivity.this, "Volley Error!", Toast.LENGTH_SHORT).show();
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

    /**
     * Check Input
     */
    private boolean checkEditText(EditText editText) {
        if (editText.getText().toString().trim().length() > 0)
            return true;
        else {
            editText.setError("Please enter full information");
        }
        return false;
    }

    /**
     * Check Email
     */
    private boolean isValidEmail(String target) {
        if (target.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))
            return true;
        else {
            edtEmail.setError("You have entered an invalid e-mail address. Please try again!");
        }
        return false;
    }
    /**
     * Check RePassword
     */
    private boolean checkRePassword(EditText editPassword,EditText editRePassword) {
        if (editPassword.getText().toString().equals(editRePassword.getText().toString()))
            return true;
        else {
            editRePassword.setError("Incorrect password");
        }
        return false;
    }
}