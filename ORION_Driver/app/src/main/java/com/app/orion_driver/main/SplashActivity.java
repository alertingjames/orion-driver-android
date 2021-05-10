package com.app.orion_driver.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {

    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String email = EasyPreference.with(getApplicationContext(), "user_info").getString("email", "");
                String role = EasyPreference.with(getApplicationContext(), "user_info").getString("role", "");
                Log.d("EMAIL!!!", email);
                if(email.length() > 0 && role.length() > 0){
                    login(email, "", role);
                }else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);

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

                                EasyPreference.with(getApplicationContext(), "user_info")
                                        .addString("email", Commons.thisUser.get_email())
                                        .addString("role", Commons.thisUser.getRole())
                                        .save();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                            }else if(result.equals("1")){
                                Commons.thisUser = null;
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }

}
























