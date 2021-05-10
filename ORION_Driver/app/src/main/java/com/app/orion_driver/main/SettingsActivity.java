package com.app.orion_driver.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
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

public class SettingsActivity extends BaseActivity {

    Switch myLocationSwitchButton, mapViewSwitchButton, myLocationSharingButton, orionAvailableButton;
    FrameLayout progressBar;
    TextView label1, label2, label3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressBar = (FrameLayout)findViewById(R.id.loading_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        ((TextView)findViewById(R.id.title)).setTypeface(bold);

        label1 = (TextView)findViewById(R.id.lb1);
        label2 = (TextView)findViewById(R.id.lb2);
        label3 = (TextView)findViewById(R.id.lb3);

        label1.setTypeface(normal);
        label2.setTypeface(normal);
        label3.setTypeface(normal);

        myLocationSwitchButton = (Switch)findViewById(R.id.locationSetting);
        mapViewSwitchButton = (Switch) findViewById(R.id.mapviewSetting);
        myLocationSharingButton = (Switch) findViewById(R.id.shareSetting);
        orionAvailableButton = (Switch) findViewById(R.id.orionAvailabilitySetting);

        if(Commons.curMapTypeIndex == 2)mapViewSwitchButton.setChecked(true);
        else mapViewSwitchButton.setChecked(false);

        if(Commons.mapCameraMoveF)myLocationSwitchButton.setChecked(true);
        else myLocationSwitchButton.setChecked(false);

        if(Commons.myLocShareF)myLocationSharingButton.setChecked(true);
        else myLocationSharingButton.setChecked(false);

        if(Commons.thisUser.get_status().equals("available"))orionAvailableButton.setChecked(true);
        else orionAvailableButton.setChecked(false);


        myLocationSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Commons.mapCameraMoveF = true;
                }else {
                    Commons.mapCameraMoveF = false;
                }
            }
        });
        mapViewSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Commons.curMapTypeIndex = 2;
                }else {
                    Commons.curMapTypeIndex = 1;
                }
                Commons.googleMap.setMapType(MainActivity.MAP_TYPES[Commons.curMapTypeIndex]);
            }
        });

        myLocationSharingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Commons.myLocShareF = true;
                    Commons.mainActivity.startTimer();
                    showToast("Location sharing started.");
                }else {
                    Commons.myLocShareF = false;
                    Commons.mainActivity.stopTimer();
                    showToast("Location sharing stopped.");
                }

            }
        });

        orionAvailableButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setDriverAvailable(b);
            }
        });

        myLocationSwitchButton.setTypeface(normal);
        myLocationSharingButton.setTypeface(normal);
        mapViewSwitchButton.setTypeface(normal);
        orionAvailableButton.setTypeface(normal);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setDriverAvailable(boolean available){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "setDriverAvailable")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("status", available?"available":"")
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
                                Commons.thisUser.set_status("available");
                            }else {
                                showToast("Server issue");
                                if(available)orionAvailableButton.setChecked(false);
                                else orionAvailableButton.setChecked(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressBar.setVisibility(View.GONE);
                        showToast("Server issue");
                        if(available)orionAvailableButton.setChecked(false);
                        else orionAvailableButton.setChecked(true);
                    }
                });
    }

}





















