package com.app.orion_driver.main;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.orion_driver.R;
import com.app.orion_driver.base.BaseFragmentActivity;
import com.app.orion_driver.classes.DirectionsJSONParser;
import com.app.orion_driver.classes.MapWrapperLayout;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.Constants;
import com.app.orion_driver.commons.ReqConst;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;

public class LocationTrackingActivity extends BaseFragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener{

    ImageView searchButton, cancelButton;
    public LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    LinearLayout notiFrame, notiLayout;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng myLatLng = null;
    MapWrapperLayout mapWrapperLayout;
    String cityName = "", countryName = "";
    String address = "";
    LocationManager locationManager;
    Marker myMarker = null;
    FrameLayout progressBar;

    public static final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracking);

        progressBar = (FrameLayout) findViewById(R.id.loading_bar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        wakeLock.release();

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

        setupUI(findViewById(R.id.activity), this);

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

    }

    private void isLocationEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationTrackingActivity.this);
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
        getAddressFromLatLng(latLng);
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
                    refreshMyMarker(myLatLng = latLng);
//                    initCamera(latLng);
                    direct(Commons.destination.getLatLng());

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
        if(marker != myMarker){
            String info = Commons.destination.getTitle() + "\n" + Commons.destination.getAddress()
                    + (Commons.destination.getAddressLine().length() > 0?"\n" + Commons.destination.getAddressLine():"");
            new androidx.appcompat.app.AlertDialog.Builder(LocationTrackingActivity.this)
                    .setTitle("Location Info")
                    .setIcon(R.drawable.mylocmarker)
                    .setMessage(info)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
        }
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

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(Commons.destination.getLatLng())
                .title(Commons.destination.getTitle())
                .icon(BitmapDescriptorFactory.fromBitmap(getCustomMarkerIcon(Commons.destination.getPicture_url()))));

        initCamera(Commons.destination.getLatLng());

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

    private Bitmap getCustomMarkerIcon(@DrawableRes String url) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        CircleImageView markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.adsicon);
        Picasso.with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.logo2)
                .into(markerImageView);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public void back(View view){
        onBackPressed();
    }

    LatLng origin = null, dest = null;
    LatLng org, dst;
    double distance = 0;

    public void direct(LatLng destLocation){

        origin = myLatLng;
        try {
            dest = destLocation;
        }catch (NullPointerException e){

        }

        // Checks, whether start and end locations are captured

//        if(markerPoints.size() >= 2){
//            LatLng origin = markerPoints.get(0);
//            LatLng dest = markerPoints.get(1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask(); Log.d("ResultJSON===>",result);

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();   Log.d("LastResult===>",result.toString());

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j); Log.d("POINTS===>",point.toString());

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    if(j==0)org=position;
                    else {
                        dst = position;
//                        double dist=getDistance(dst,org)*1.60934;
                        double dist=getDistance(dst, org);
                        org = dst;
                        distance+=dist; Log.d("DeltaDistance===>", String.valueOf(distance));
                    }
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            try {
                mMap.addPolyline(lineOptions);
//                MarkerOptions opt = new MarkerOptions().position(latLng_main);
//                opt.title("D(km) from Me:"+String.valueOf(distance));
//                mMap.addMarker(opt).showInfoWindow();
                double walk=distance*16.0934;
                double drive=distance*1.60934/0.666f;

                distance=roundTwoDecimals(distance);

                String walkStr="", driveStr="";
                if(walk >= 60)
                    walkStr=String.valueOf((int)walk/60)+" hr"+" "+String.valueOf(roundTwoDecimals(walk-((int)walk/60)*60))+" min";
                else {
                    walk=roundTwoDecimals(walk);
                    walkStr=String.valueOf(walk)+" min";
                }

                if(drive >= 60)
                    driveStr=String.valueOf((int)drive/60)+" hr"+" "+String.valueOf(roundTwoDecimals(drive-((int)drive/60)*60))+" min";
                else {
                    drive=roundTwoDecimals(drive);
                    driveStr=String.valueOf(drive)+" min";
                }
                distance=0.0f;

                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(dest, 14);
                mMap.animateCamera(update);

            }catch (NullPointerException e){

            }
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception of url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public double getDistance(LatLng latLng1, LatLng latLng2) {
        try{
            // lat1 and lng1 are the values of a previously stored location
            return distance(latLng1, latLng2);
        }catch (NullPointerException e){

        }
        return 0.0d;
    }

    /** calculates the distance between two locations in MILES */
    private double distance(LatLng latLng1, LatLng latLng2) {

        double lat1,lng1,lat2,lng2;
        lat1=latLng1.latitude;
        lng1=latLng1.longitude;
        lat2=latLng2.latitude;
        lng2=latLng2.longitude;

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }


}
























