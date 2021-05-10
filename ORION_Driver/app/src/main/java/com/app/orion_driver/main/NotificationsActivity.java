package com.app.orion_driver.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.app.orion_driver.adapters.NotificationListAdapter;
import com.app.orion_driver.base.BaseActivity;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.ReqConst;
import com.app.orion_driver.models.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;

public class NotificationsActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    ListView list;
    FrameLayout progressBar;

    ArrayList<Notification> notifications = new ArrayList<>();
    NotificationListAdapter adapter = new NotificationListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

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
        getNotifications();
    }

    private void getNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getNotifications")
                .addBodyParameter("receiver_id", String.valueOf(Commons.thisUser.get_idx()))
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
                                notifications.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Notification notification = new Notification();
                                    notification.setId(object.getInt("id"));
                                    notification.setReceiver_id(object.getInt("receiver_id"));
                                    notification.setSender_id(object.getInt("sender_id"));
                                    notification.setSender_name(object.getString("sender_name"));
                                    notification.setSender_email(object.getString("sender_email"));
                                    notification.setSender_phone(object.getString("sender_phone"));
                                    notification.setDate_time(object.getString("date_time"));
                                    notification.setMessage(object.getString("message"));
                                    notification.setImage(object.getString("image_message"));

                                    notifications.add(notification);
                                }

                                if(notifications.isEmpty())((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);

                                adapter.setDatas(notifications);
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

    public void deleteNotification(Notification notification){
        showAlertDialogForQuestion("Warning", "Are you sure you want to delete this message?", this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                progressBar.setVisibility(View.VISIBLE);
                AndroidNetworking.post(ReqConst.SERVER_URL + "delNotification")
                        .addBodyParameter("notification_id", String.valueOf(notification.getId()))
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
                                    if (result.equals("0")) {
                                        int index = notifications.indexOf(notification);
                                        notifications.remove(index);

                                        adapter.setDatas(notifications);
                                        if(adapter.getCount() == 0){
                                            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                        }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                        list.setAdapter(adapter);
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

                return null;
            }
        });
    }

}

































