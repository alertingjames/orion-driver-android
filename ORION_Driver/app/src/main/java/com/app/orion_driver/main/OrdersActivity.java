package com.app.orion_driver.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_driver.R;
import com.app.orion_driver.adapters.StoreListAdapter;
import com.app.orion_driver.base.BaseActivity;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.ReqConst;
import com.app.orion_driver.models.Store;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class OrdersActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    ListView list;
    FrameLayout progressBar;

    ArrayList<Store> stores = new ArrayList<>();
    StoreListAdapter adapter = new StoreListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        Commons.ordersActivity = this;

        progressBar = (FrameLayout) findViewById(R.id.loading_bar);
        title = (TextView)findViewById(R.id.title);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(true);
        ui_edtsearch.requestFocus();

        title.setTypeface(bold);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().trim().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });

        list = (ListView) findViewById(R.id.list);

        setupUI((FrameLayout)findViewById(R.id.activity), this);

    }

    public void search(View view){
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
    }

    public void cancelSearch(View view){
        cancelButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getStoreOrders();
    }

    private void getStoreOrders() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getStoreOrders")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
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
                                stores.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);

                                    Store store = new Store();

                                    store.setOrderedDateTime(object.getString("date_time"));
                                    store.setOrderedItemsCount(object.getInt("items_count"));
                                    store.setOrderdStatus(object.getString("status"));
                                    store.setOrderId(object.getInt("id"));

                                    object = object.getJSONObject("store");

                                    store.setId(object.getInt("id"));
                                    store.setUserId(object.getInt("member_id"));
                                    store.setName(object.getString("name"));
                                    store.setPhoneNumber(object.getString("phone_number"));
                                    store.setAddress(object.getString("address"));
                                    store.setRatings(Float.parseFloat(object.getString("ratings")));
                                    store.setReviews(object.getInt("reviews"));
                                    store.setLogoUrl(object.getString("logo_url"));
                                    store.set_registered_time(object.getString("registered_time"));
                                    store.set_status(object.getString("status"));
                                    store.setDelivery_days(object.getInt("delivery_days"));
                                    store.setDelivery_price(Double.parseDouble(object.getString("delivery_price")));
                                    double lat = 0.0d, lng = 0.0d;
                                    if(object.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(object.getString("latitude"));
                                        lng = Double.parseDouble(object.getString("longitude"));
                                    }
                                    store.setLatLng(new LatLng(lat, lng));

                                    Location myLocation = new Location("MyLocation");
                                    myLocation.setLatitude(Commons.thisUser.getLatLng().latitude);
                                    myLocation.setLongitude(Commons.thisUser.getLatLng().longitude);
                                    Location driverLocation = new Location("DealLocation");
                                    driverLocation.setLatitude(store.getLatLng().latitude);
                                    driverLocation.setLongitude(store.getLatLng().longitude);
                                    double distance = myLocation.distanceTo(driverLocation);

                                    store.setDistance(distance);

                                    stores.add(store);
                                }

                                if(stores.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                                adapter.setDatas(stores);
                                list.setAdapter(adapter);

                            }else {
                                showToast("Server issue.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.ordersActivity = null;
    }
}












































