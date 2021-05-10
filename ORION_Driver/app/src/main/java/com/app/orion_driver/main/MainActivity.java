package com.app.orion_driver.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_driver.R;
import com.app.orion_driver.base.BaseFragmentActivity;
import com.app.orion_driver.classes.CustomTypefaceSpan;
import com.app.orion_driver.classes.MapWrapperLayout;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.Constants;
import com.app.orion_driver.commons.ReqConst;
import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends BaseFragmentActivity  implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener{

    DrawerLayout drawer;
    ImageView searchButton, cancelButton;
    public LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    NavigationView navigationView;
    LinearLayout notiFrame, notiLayout;

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

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng myLatLng = null;
    MapWrapperLayout mapWrapperLayout;
    String cityName = "", countryName = "";
    String address = "";
    LocationManager locationManager;
    Marker myMarker = null;
    FrameLayout progressBar;

    private static Timer timer = null;
    Handler mHandler = new Handler();
    boolean initLocF = false;

    static LatLng myOldLatLng = null;

    public static final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Commons.mainActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        progressBar = (FrameLayout) findViewById(R.id.loading_bar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        wakeLock.release();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        checkAllPermission();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        title = (TextView)findViewById(R.id.title);
        title.setTypeface(bold);

        notiFrame = (LinearLayout)findViewById(R.id.notiFrame);
        notiLayout = (LinearLayout)findViewById(R.id.notiLayout);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(true);
        ui_edtsearch.requestFocus();
        ui_edtsearch.setTypeface(normal);

        ui_edtsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLocationOnAddress(ui_edtsearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.name)).setTypeface(bold);

        setupUI(findViewById(R.id.activity), this);

        changeMenuFonts();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getNotifications();
            }
        }).start();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mapFragment.getMapAsync(this);
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));

        mapFragment.setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        isLocationEnabled();

        if(!Commons.initAlertF){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAlertDialog("Hint!", "Please confirm your availability today for ORION delivery.", MainActivity.this, new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {

                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);

                            return null;
                        }
                    });
                }
            }, 3000);
            Commons.initAlertF = true;
        }

    }

    private void isLocationEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.cancel();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        } else {
            Log.d("Info+++", "Location enabled");
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            try {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());           ///////////////////////////////////////////////////////////////////////////////////////////////
                                refreshMyMarker(latLng);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d("INFO+++", "GPS Provider enabled");
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d("INFO+++", "GPS Provider disabled");
                    }
                });
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            try {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());           ///////////////////////////////////////////////////////////////////////////////////////////////
                                refreshMyMarker(latLng);
//                                initCamera(latLng);

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d("INFO+++", "NETWORK Provider enabled");
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d("INFO+++", "NETWORK Provider disabled");
                    }
                });
            }
            else if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
                Log.d("Info+++", "Passive Location Provider enabled");
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            try {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());           ///////////////////////////////////////////////////////////////////////////////////////////////
                                refreshMyMarker(latLng);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d("INFO+++", "PASSIVE Provider enabled");
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d("INFO+++", "PASSIVE Provider disabled");
                    }
                });
            }
        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void moveToMyLocation(View view){
        if(myLatLng != null) {
            initCamera(myLatLng);
            locationManager.removeUpdates((LocationListener) this);
        }
    }

    public void openSettings(View view){
        Commons.googleMap = mMap;
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void refreshMyMarker(LatLng latLng) {
        drawCircle(latLng);
        if(myMarker == null)
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationmarker)));
        Commons.thisUser.setLatLng(latLng);
        myLatLng = latLng;
        Log.d("MyLatLng+++", String.valueOf(myLatLng));
        if(Commons.mapCameraMoveF){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));
        }
        Location location = new Location("Refreshed Location");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
//        animateMarker(myMarker, location);
        myMarker.setPosition(latLng);
        if(myOldLatLng == null)myOldLatLng = myLatLng;
        if(getDistance(myLatLng, myOldLatLng) >= Constants.RADIUS){
            myOldLatLng = myLatLng;
            setLocation();
        }
        getAddressFromLatLng(latLng);
    }

    private double getDistance(LatLng latLng1, LatLng latLng2){

        Location loc1 = new Location("Location1");
        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);
        Location loc2 = new Location("Location2");
        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);
        double distance = loc1.distanceTo(loc2);

        return distance;
    }

    private void changeMenuFonts(){

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.name)).setTypeface(bold);

        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

    }

    private void applyFontToMenuItem(MenuItem mi) {
        int size = 16;
        float scaledSizeInPixels = size * getResources().getDisplayMetrics().scaledDensity;
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan(mi.getTitle().toString(), bold, scaledSizeInPixels), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void getNotifications(){
        if(notiLayout.getChildCount() > 0) notiLayout.removeAllViews();

        if(Commons.thisUser != null){
            getVendorNotification();
            getAdminNotification();
        }
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
        ui_edtsearch.setText("");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        displaySelectedScreen(menuItem.getItemId());

        return false;
    }


    private void displaySelectedScreen(int itemId) {
        switch (itemId) {
            case R.id.orders:
                Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                startActivity(intent);
                break;
            case R.id.notifications:
                intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
                break;
            case R.id.account:
                intent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(intent);
                break;
            case R.id.settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
//                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
//                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //TODO Update UI
            if (myLatLng != null) {
                setLocation();
            }
        }
    };

    public void stopTimer() {
        if (timer != null) {
            mHandler.removeCallbacks(runnable);
            timer.cancel();
            timer.purge();
            timer = null;
        }
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.GONE);
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(runnable);
            }
        }, 0, 30000);
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Commons.mainActivity = this;

        initOrderStatus();

        if(Commons.thisUser != null){
            Glide.with(getApplicationContext())
                    .load(Commons.thisUser.get_photoUrl())
                    .into((CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.avatar));
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.name)).setText(Commons.thisUser.get_name());
        }

        if(Commons.thisUser != null){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("HomeActivity:", "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            Log.d("Token!!!", token);
                            if(token.length() > 0)
                                uploadNewToken(token);
                        }
                    });
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.mainActivity = null;
        stopTimer();

    }

    private void uploadNewToken(String token){
        AndroidNetworking.post(ReqConst.SERVER_URL + "uploadfcmtoken")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("fcm_token", token)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private void getVendorNotification(){

        Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "request_driver/" + String.valueOf(Commons.thisUser.get_idx()));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                try{
                    LayoutInflater inflater = getLayoutInflater();
                    View myLayout = inflater.inflate(R.layout.layout_notification, null);
                    String noti = map.get("msg").toString();
                    String time = map.get("date").toString();
                    String fromid = map.get("fromid").toString();
                    String fromname = map.get("fromname").toString();
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING,200);
//                    noti = "Customer's new order: " + fromname;
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String date = dateFormat.format(new Date(Long.parseLong(time)));
                    ((TextView)myLayout.findViewById(R.id.date)).setText(date);
                    ((TextView)myLayout.findViewById(R.id.name)).setText(fromname);
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    ((TextView)myLayout.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }

                            Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                            startActivity(intent);
                        }
                    });

                    ((ImageView)myLayout.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });
                    notiLayout.addView(myLayout);
                    ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                    ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getAdminNotification(){

        Firebase ref;
        ref = new Firebase(ReqConst.FIREBASE_URL + "admin/" + String.valueOf(Commons.thisUser.get_idx()));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                try{
                    LayoutInflater inflater = getLayoutInflater();
                    View myLayout = inflater.inflate(R.layout.layout_notification, null);
                    String noti = map.get("msg").toString();   Log.d("Customer Noti!!!", noti);
                    String time = map.get("date").toString();
                    String fromid = map.get("fromid").toString();
                    String fromname = map.get("fromname").toString();
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_PING_RING,200);
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String date = dateFormat.format(new Date(Long.parseLong(time)));
                    ((TextView)myLayout.findViewById(R.id.date)).setText(date);
                    ((TextView)myLayout.findViewById(R.id.name)).setText("Qhome");
                    ((TextView)myLayout.findViewById(R.id.notiText)).setText(noti);
                    ((TextView)myLayout.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });

                    ((ImageView)myLayout.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            dataSnapshot.getRef().removeValue();
                            notiLayout.removeView(myLayout);
                            ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                            ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                            if(notiLayout.getChildCount() == 0){
                                ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.GONE);
                                dismissNotiFrame();
                                ShortcutBadger.removeCount(getApplicationContext());
                            }
                        }
                    });
                    notiLayout.addView(myLayout);
                    ((FrameLayout)findViewById(R.id.notimark)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.count)).setText(String.valueOf(notiLayout.getChildCount()));
                    ShortcutBadger.applyCount(getApplicationContext(), notiLayout.getChildCount());
                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void initOrderStatus(){
        Commons.orderStatus.initOrderStatus();
    }

    public void showNotiFrame(View view){
        if(notiLayout.getChildCount() > 0){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_in);
            notiFrame.setAnimation(animation);
            notiFrame.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((View)findViewById(R.id.notiBackground)).setVisibility(View.VISIBLE);
                }
            }, 200);
        }
    }

    private void dismissNotiFrame(){
        if(notiFrame.getVisibility() == View.VISIBLE){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_out);
            notiFrame.setAnimation(animation);
            notiFrame.setVisibility(View.GONE);
            ((View)findViewById(R.id.notiBackground)).setVisibility(View.GONE);
        }
    }

    public void dismissNotiFrame(View view){
        dismissNotiFrame();
    }

    private static final int ACCESS_COARSE_LOCATION_PERMISSION_REQUEST = 7001;

    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION_REQUEST);

        } else {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location == null) location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null) location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                try {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());           ///////////////////////////////////////////////////////////////////////////////////////////////
                    refreshMyMarker(latLng);
                    initCamera(latLng);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initCamera(LatLng location) {
        CameraPosition position = CameraPosition.builder()
                .target(location)
                .zoom(20f)
                .bearing(30)                // Sets the orientation of the camera to east
                .tilt(30)
                .build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getApplicationContext());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            String zip = addresses.get(0).getPostalCode();
            String url= addresses.get(0).getUrl();

            cityName = state; countryName = country;
            ((LinearLayout)findViewById(R.id.addressLayout)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.addressBar)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.addressBar)).setText(address);

            if(!initLocF){
                setLocation();
                initLocF = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(Commons.curMapTypeIndex == 2){
            mMap.setMapType(MAP_TYPES[Commons.curMapTypeIndex = 1]);
        }
        else if(Commons.curMapTypeIndex == 1)mMap.setMapType(MAP_TYPES[Commons.curMapTypeIndex = 2]);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initListeners();
//        googleMap.setPadding(0, 0, 0, 500);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void initListeners() {

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled( true );
        mMap.setBuildingsEnabled(true);

        mMap.setMapType(MAP_TYPES[Commons.curMapTypeIndex]);

        checkForLocationPermission();

    }

    public LatLng getCenterCoordinate(LatLng latLng) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng);
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }

    Circle circle = null;

    private void drawCircle(LatLng latLng) {
        if(circle == null){
            try{
                LatLng loc = getCenterCoordinate(latLng);
                double radius = Constants.RADIUS;
                CircleOptions options = new CircleOptions();
                if(loc != null) {
                    options.center(loc);
                    //Radius in meters
                    options.radius(radius);
                    options.fillColor(getResources()
                            .getColor(R.color.circle_fill_color));
                    options.strokeColor(getResources()
                            .getColor(R.color.circle_stroke_color));
                    options.strokeWidth(2);
                    circle = mMap.addCircle(options);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else circle.setCenter(latLng);
    }

    private void searchLocationOnAddress(String addr) {
        List<Address> addresses =null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocationName(addr, 1);

            if(addresses.size() > 0){
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();

                String address = addresses.get(0).getAddressLine(0);

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                String zip = addresses.get(0).getPostalCode();
                String url= addresses.get(0).getUrl();

                LatLng latLng = new LatLng(latitude,longitude);
                MarkerOptions options = new MarkerOptions().position(latLng);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(options);
                initCamera(latLng);
                getAddressFromLatLng(latLng);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLocation(){
        AndroidNetworking.post(ReqConst.SERVER_URL + "setLocation")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("address", address)
                .addBodyParameter("country", countryName)
                .addBodyParameter("area", cityName)
                .addBodyParameter("latitude", String.valueOf(myLatLng.latitude))
                .addBodyParameter("longitude", String.valueOf(myLatLng.longitude))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                Log.d("MY LOCATION!!!",
                                        String.valueOf(response.getString("lat")) + "///" + String.valueOf(response.getString("lng")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }


}
























