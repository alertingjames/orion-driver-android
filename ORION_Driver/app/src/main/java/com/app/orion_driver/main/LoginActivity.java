package com.app.orion_driver.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_driver.R;
import com.app.orion_driver.base.BaseActivity;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.ReqConst;
import com.app.orion_driver.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {

    EditText emailBox, passwordBox;
    TextView loginButton, signupButton, forgotpasswordButton;
    ImageButton showButton;
    boolean pwShow = false;
    TextView label1;
    FrameLayout progressBar;

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.SET_TIME,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.CALL_PRIVILEGED,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            android.Manifest.permission.LOCATION_HARDWARE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkAllPermission();

        progressBar = (FrameLayout)findViewById(R.id.loading_bar);

        emailBox = (EditText)findViewById(R.id.emailBox);
        passwordBox = (EditText)findViewById(R.id.passwordBox);

        loginButton = (TextView) findViewById(R.id.btn_login);
        signupButton = (TextView) findViewById(R.id.btn_signup);
        forgotpasswordButton = (TextView) findViewById(R.id.btn_forgot_pwd);
        showButton = (ImageButton)findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pwShow){
                    pwShow = true;
                    showButton.setImageResource(R.drawable.eyelock);
                    if(passwordBox.getText().length() > 0){
                        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT);
                        passwordBox.setTypeface(normal);
                    }
                }else {
                    pwShow = false;
                    showButton.setImageResource(R.drawable.eyeunlock);
                    if(passwordBox.getText().length() > 0){
                        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }
            }
        });

        label1 = (TextView) findViewById(R.id.lb1);

        emailBox.setTypeface(normal);
        passwordBox.setTypeface(normal);
        loginButton.setTypeface(bold);
        signupButton.setTypeface(normal);
        forgotpasswordButton.setTypeface(normal);
        label1.setTypeface(normal);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        forgotpasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        setupUI(findViewById(R.id.activity), this);

    }

    private void login(){

        if(emailBox.getText().length() == 0){
            showToast("Enter your email.");
            return;
        }

        if(!isValidEmail(emailBox.getText().toString().trim())){
            showToast("Your email is invalid. Please enter a valid email.");
            return;
        }

        if(passwordBox.getText().length() == 0){
            showToast("Enter your password.");
            return;
        }

        login(emailBox.getText().toString().trim(), passwordBox.getText().toString().trim(), "driver");

    }

    private void login(String email, String password, String role){

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "login")
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("role", role)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                JSONObject object = response.getJSONObject("data");
                                User user = new User();
                                user.set_idx(object.getInt("id"));
                                user.set_name(object.getString("name"));
                                user.set_email(object.getString("email"));
                                user.set_password(object.getString("password"));
                                user.set_photoUrl(object.getString("picture_url"));
                                user.set_registered_time(object.getString("registered_time"));
                                user.setRole(object.getString("role"));
                                user.set_phone_number(object.getString("phone_number"));
                                user.set_address(object.getString("address"));
                                user.set_country(object.getString("country"));
                                user.set_area(object.getString("area"));
                                user.set_street(object.getString("street"));
                                user.set_house(object.getString("house"));
                                double lat = 0.0d, lng = 0.0d;
                                if(object.getString("latitude").length() > 0){
                                    lat = Double.parseDouble(object.getString("latitude"));
                                    lng = Double.parseDouble(object.getString("longitude"));
                                }
                                user.setLatLng(new LatLng(lat, lng));
                                user.set_stores(object.getInt("stores"));
                                user.set_status(object.getString("status"));

                                Commons.thisUser = user;
                                showToast("Successfully logged In.");

                                EasyPreference.with(getApplicationContext(), "user_info")
                                        .addString("email", Commons.thisUser.get_email())
                                        .addString("role", Commons.thisUser.getRole())
                                        .save();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }else if(result.equals("1")){
                                Commons.thisUser = null;
                                showToast("You haven't been registered. Please sign up.");
                                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                                startActivity(intent);
                            }else if(result.equals("2")){
                                Commons.thisUser = null;
                                showToast("Your password is incorrect.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });

    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}















