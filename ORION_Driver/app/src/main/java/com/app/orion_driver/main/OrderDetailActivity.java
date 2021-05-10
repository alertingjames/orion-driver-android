package com.app.orion_driver.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_driver.R;
import com.app.orion_driver.base.BaseActivity;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.Constants;
import com.app.orion_driver.commons.ReqConst;
import com.app.orion_driver.fragments.PDetailFragment;
import com.app.orion_driver.models.Destination;
import com.app.orion_driver.models.Order;
import com.app.orion_driver.models.OrderItem;
import com.app.orion_driver.models.Product;
import com.app.orion_driver.models.Store;
import com.app.orion_driver.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class OrderDetailActivity extends BaseActivity {

    RoundedImageView pictureBox;
    TextView nameBox, statusBox, addressBox, distanceBox, acceptButton, rejectButton, itemsCountBox, deliveryDaysBox;
    TextView title, label;
    ImageView contactButton, backButton;
    LinearLayout container, buttonLayout;
    FrameLayout progressBar;
    ArrayList<OrderItem> orderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        progressBar = (FrameLayout)findViewById(R.id.loading_bar);

        pictureBox = (RoundedImageView)findViewById(R.id.storeLogoBox);
        nameBox = (TextView)findViewById(R.id.storeNameBox);
        statusBox = (TextView)findViewById(R.id.statusBox);
        itemsCountBox = (TextView)findViewById(R.id.itemsCountBox);
        deliveryDaysBox = (TextView)findViewById(R.id.deliveryDaysBox);
        statusBox = (TextView)findViewById(R.id.statusBox);
        addressBox = (TextView)findViewById(R.id.addressBox);
        distanceBox = (TextView)findViewById(R.id.distanceBox);
        acceptButton = (TextView)findViewById(R.id.btn_accept);
        rejectButton = (TextView)findViewById(R.id.btn_reject);

        container = (LinearLayout) findViewById(R.id.container);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        label = (TextView)findViewById(R.id.lb);
        title = (TextView)findViewById(R.id.title);
        contactButton = (ImageView) findViewById(R.id.btn_contact);
        backButton = (ImageView) findViewById(R.id.btn_back);

        title.setTypeface(bold);
        nameBox.setTypeface(bold);
        statusBox.setTypeface(normal);
        addressBox.setTypeface(normal);
        distanceBox.setTypeface(normal);
        itemsCountBox.setTypeface(normal);
        deliveryDaysBox.setTypeface(normal);
        label.setTypeface(normal);
        acceptButton.setTypeface(bold);
        rejectButton.setTypeface(bold);

        itemsCountBox.setText(String.valueOf(Commons.store.getOrderedItemsCount()));
        deliveryDaysBox.setText(String.valueOf(Commons.store.getDelivery_days()) + " Days");

        Glide.with(getApplicationContext()).load(Commons.store.getLogoUrl()).into(pictureBox);
        nameBox.setText(Commons.store.getName());
        addressBox.setText(Commons.store.getAddress());
        distanceBox.setText(String.valueOf(df.format(Commons.store.getDistance()/1000)) + " km");

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Commons.store.getPhoneNumber()));
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    processVendorOrder("accepted", createItemIdsJsonString(orderItems));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogForQuestion("Warning", "Are you sure you want to reject this order?", OrderDetailActivity.this, null, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {

                        try {
                            processVendorOrder("rejected", createItemIdsJsonString(orderItems));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Commons.store.getOrderdStatus().length() == 0) {
            statusBox.setText("NEW");
            statusBox.setTextColor(Color.RED);
            buttonLayout.setVisibility(View.VISIBLE);
            statusBox.setTextSize(14);
        }
        else if(Commons.store.getOrderdStatus().equals("accepted")) {
            statusBox.setText("ONGOING");
            statusBox.setTextColor(getColor(R.color.green));
            buttonLayout.setVisibility(View.GONE);
            statusBox.setTextSize(20);
        }
        else if(Commons.store.getOrderdStatus().equals("rejected")) {
            statusBox.setText("REJECTED");
            statusBox.setTextColor(Color.GRAY);
            buttonLayout.setVisibility(View.GONE);
            statusBox.setTextSize(20);
        }
        else if(Commons.store.getOrderdStatus().equals("delivered")) {
            statusBox.setText("DELIVERED");
            statusBox.setTextColor(Color.BLACK);
            statusBox.setTextSize(30);
            buttonLayout.setVisibility(View.GONE);
        }

        getVendorOrderItems();

    }

    public void getVendorOrderItems() {

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getVendorOrderItems")
                .addBodyParameter("porder_id", String.valueOf(Commons.store.getOrderId()))
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
                                orderItems.clear();
                                container.removeAllViews();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int j=0; j<dataArr.length(); j++) {
                                    JSONObject obj = (JSONObject) dataArr.get(j);
                                    OrderItem item = new OrderItem();
                                    item.setId(obj.getInt("id"));
                                    item.setOrder_id(obj.getInt("order_id"));
                                    item.setUser_id(obj.getInt("member_id"));
                                    item.setVendor_id(obj.getInt("vendor_id"));
                                    item.setStore_id(obj.getInt("store_id"));
                                    item.setStore_name(obj.getString("store_name"));
                                    item.setProduct_id(obj.getInt("product_id"));
                                    item.setProduct_name(obj.getString("product_name"));
                                    item.setCategory(obj.getString("category"));
                                    item.setSubcategory(obj.getString("subcategory"));
                                    item.setGender(obj.getString("gender"));
                                    item.setGender_key(obj.getString("gender_key"));
                                    item.setDelivery_days(obj.getInt("delivery_days"));
                                    item.setDelivery_price(Double.parseDouble(obj.getString("delivery_price")));
                                    item.setPrice(Double.parseDouble(obj.getString("price")));
                                    item.setUnit(obj.getString("unit"));
                                    item.setQuantity(obj.getInt("quantity"));
                                    item.setDate_time(obj.getString("date_time"));
                                    item.setPicture_url(obj.getString("picture_url"));
                                    item.setStatus(obj.getString("status"));
                                    item.setOrderID(obj.getString("orderID"));
                                    item.setContact(obj.getString("contact"));
                                    item.setDiscount(obj.getInt("discount"));
                                    item.setPaid_amount(Double.parseDouble(obj.getString("paid_amount")));
                                    item.setPaid_time(obj.getString("paid_time"));
                                    item.setPayment_status(obj.getString("payment_status"));
                                    item.setPaid_id(obj.getInt("paid_id"));
                                    item.setAddress(obj.getString("address"));
                                    item.setAddress_line(obj.getString("address_line"));
                                    double lat = 0.0d, lng = 0.0d;
                                    if(obj.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(obj.getString("latitude"));
                                        lng = Double.parseDouble(obj.getString("longitude"));
                                    }
                                    item.setLatLng(new LatLng(lat, lng));

                                    Log.d("STATUS!!!", item.getStatus());

                                    orderItems.add(item);

                                    LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = layoutInflater.inflate(R.layout.item_vendor_orders, null);
                                    TextView orderIDBox = (TextView) view.findViewById(R.id.orderIDBox);
                                    TextView categoryBox = (TextView) view.findViewById(R.id.categoryBox);
                                    TextView priceBox = (TextView) view.findViewById(R.id.priceBox);
                                    TextView quantityBox = (TextView) view.findViewById(R.id.quantityBox);
                                    ImageView pictureBox = (ImageView) view.findViewById(R.id.pictureBox);
                                    TextView genderBox = (TextView) view.findViewById(R.id.genderBox);
                                    TextView addressBox = (TextView) view.findViewById(R.id.addressBox);
                                    TextView productNameBox = (TextView) view.findViewById(R.id.productNameBox);
                                    TextView statusBox = (TextView) view.findViewById(R.id.statusBox);
                                    TextView addressLineBox = (TextView) view.findViewById(R.id.addressLineBox);
                                    TextView deliveryDaysBox = (TextView) view.findViewById(R.id.deliveryDaysBox);
                                    ImageView detailButton = (ImageView) view.findViewById(R.id.btn_detail);
                                    TextView confirmButton = (TextView) view.findViewById(R.id.btn_confirm);

                                    priceBox.setTypeface(bold);
                                    quantityBox.setTypeface(bold);
                                    productNameBox.setTypeface(bold);
                                    categoryBox.setTypeface(normal);
                                    genderBox.setTypeface(normal);
                                    addressBox.setTypeface(normal);
                                    addressLineBox.setTypeface(normal);
                                    deliveryDaysBox.setTypeface(normal);
                                    orderIDBox.setTypeface(normal);
                                    statusBox.setTypeface(normal);

                                    orderIDBox.setText(item.getOrderID());
                                    priceBox.setText(df.format(item.getPrice()) + " " + Constants.currency);
                                    productNameBox.setText(item.getProduct_name());
                                    categoryBox.setText(item.getCategory() + "|" + item.getSubcategory());
                                    quantityBox.setText("QTY: " + String.valueOf(item.getQuantity()));
                                    genderBox.setText(item.getGender());
                                    addressBox.setText(item.getAddress());
                                    addressLineBox.setText(item.getAddress_line());
                                    deliveryDaysBox.setText(item.getDelivery_days() + " Days");

                                    if(item.getStatus().length() > 0){
                                        statusBox.setText(Commons.orderStatus.statusStr.get(item.getStatus()));
                                        if(item.getStatus().equals("ready")){
                                            confirmButton.setVisibility(View.VISIBLE);
                                        }else if(item.getStatus().equals("delivered")){
                                            confirmButton.setVisibility(View.VISIBLE);
                                            confirmButton.setBackgroundResource(R.drawable.green_round_stroke);
                                            confirmButton.setTextColor(getColor(R.color.green));
                                            confirmButton.setText("DELIVERED");
                                        }
                                        else {
                                            confirmButton.setVisibility(View.GONE);
                                        }
                                    }

                                    if(item.getPicture_url().length() > 0){
                                        Picasso.with(getApplicationContext())
                                                .load(item.getPicture_url())
                                                .into(pictureBox);
                                    }

                                    pictureBox.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            productInfo(String.valueOf(item.getProduct_id()));
                                        }
                                    });

                                    detailButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getMember(String.valueOf(item.getUser_id()), item);
                                        }
                                    });

                                    confirmButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(!item.getStatus().equals("delivered")){
                                                confirmDelivered(String.valueOf(item.getId()));
                                            }
                                        }
                                    });

                                    container.addView(view);

                                }

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

    private void productInfo(String productId) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "productInfo")
                .addBodyParameter("product_id", productId)
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
                                JSONObject object = response.getJSONObject("product");
                                Product product = new Product();
                                product.setIdx(object.getInt("id"));
                                product.setStoreId(object.getInt("store_id"));
                                product.setUserId(object.getInt("member_id"));
                                product.setBrandId(object.getInt("brand_id"));
                                product.setName(object.getString("name"));
                                product.setPicture_url(object.getString("picture_url"));
                                product.setCategory(object.getString("category"));
                                product.setSubcategory(object.getString("subcategory"));
                                product.setGender(object.getString("gender"));
                                product.setGenderKey(object.getString("gender_key"));
                                product.setPrice(Double.parseDouble(object.getString("price")));
                                product.setNew_price(Double.parseDouble(object.getString("new_price")));
                                product.setUnit(object.getString("unit"));
                                product.setDescription(object.getString("description"));
                                product.setRegistered_time(object.getString("registered_time"));
                                product.setStatus(object.getString("status"));
                                product.setBrand_name(object.getString("brand_name"));
                                product.setBrand_logo(object.getString("brand_logo"));
                                product.setDelivery_price(Double.parseDouble(object.getString("delivery_price")));
                                product.setDelivery_days(Integer.parseInt(object.getString("delivery_days")));

                                product.setLikes(object.getInt("likes"));
                                product.setRatings(Float.parseFloat(object.getString("ratings")));


                                object = response.getJSONObject("store");
                                Store store = new Store();
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
                                double lat = 0.0d, lng = 0.0d;
                                if(object.getString("latitude").length() > 0){
                                    lat = Double.parseDouble(object.getString("latitude"));
                                    lng = Double.parseDouble(object.getString("longitude"));
                                }
                                store.setLatLng(new LatLng(lat, lng));

                                Commons.product1 = product;
                                Commons.store1 = store;

                                Fragment fragment = new PDetailFragment();
                                FragmentManager manager = getSupportFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.activity, fragment);
                                transaction.addToBackStack(null).commit();

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


    String itemsStr = "";

    public String createItemIdsJsonString(ArrayList<OrderItem> items)throws JSONException{

        itemsStr = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (items.size()>0){
            for(OrderItem item:items){

                String itemId = String.valueOf(item.getId());

                jsonObj=new JSONObject();

                try {
                    jsonObj.put("item_id", itemId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArr.put(jsonObj);
            }
            JSONObject scheduleObj = new JSONObject();
            scheduleObj.put("itemIds", jsonArr);
            itemsStr = scheduleObj.toString();
            return itemsStr;
        }

        return itemsStr;
    }

    public void processVendorOrder(String option, final String itemsStr) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "processVendorOrder")
                .addBodyParameter("porder_id", String.valueOf(Commons.store.getOrderId()))
                .addBodyParameter("option", option)
                .addBodyParameter("itemsStr", itemsStr)
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
                                Commons.store.setOrderdStatus(option);
                                if(option.equals("accepted")){
                                    showAlertDialog("Info", "You have accepted the vendor order. Please process it ahead successfully.\n" +
                                                    "Confirm the store address:\n" + Commons.store.getAddress(),
                                            OrderDetailActivity.this, new Callable<Void>() {
                                                @Override
                                                public Void call() throws Exception {
                                                    onResume();
                                                    return null;
                                                }
                                            });
                                }else if(option.equals("rejected")){
                                    showToast("The order has been rejected.");
                                    if(Commons.ordersActivity != null)Commons.ordersActivity.finish();
                                    finish();
                                }

                            }else if(result.equals("1")){
                                showAlertDialog("Info", "Sorry, the order has already been accepted by another driver.",
                                        OrderDetailActivity.this, new Callable<Void>() {
                                            @Override
                                            public Void call() throws Exception {
                                                finish();
                                                return null;
                                            }
                                        });
                            }
                            else {
                                showToast("Error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });

    }

    public void confirmDelivered(String itemId) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "confirmDelivered")
                .addBodyParameter("porder_id", String.valueOf(Commons.store.getOrderId()))
                .addBodyParameter("item_id", itemId)
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
                                Commons.store.setOrderdStatus("delivered");
                                onResume();
                            }else {
                                showToast("Error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });

    }


    public void getMember(String userId, OrderItem item) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getMember")
                .addBodyParameter("member_id", userId)
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

                                Destination destination = new Destination();
                                destination.setTitle(user.get_name());
                                destination.setPicture_url(user.get_photoUrl());
                                destination.setAddress(item.getAddress());
                                destination.setAddressLine(item.getAddress_line());
                                destination.setLatLng(item.getLatLng());
                                Commons.destination = destination;
                                Intent intent = new Intent(getApplicationContext(), LocationTrackingActivity.class);
                                startActivity(intent);

                            }else {
                                showToast("Error");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });

    }


    public void toStoreLocation(View view){
        Destination destination = new Destination();
        destination.setTitle(Commons.store.getName());
        destination.setAddress(Commons.store.getAddress());
        destination.setPicture_url(Commons.store.getLogoUrl());
        destination.setLatLng(Commons.store.getLatLng());
        Commons.destination = destination;
        Intent intent = new Intent(getApplicationContext(), LocationTrackingActivity.class);
        startActivity(intent);
    }



}



































