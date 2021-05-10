package com.app.orion_driver.commons;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.orion_driver.classes.OrderStatus;
import com.app.orion_driver.main.MainActivity;
import com.app.orion_driver.main.OrdersActivity;
import com.app.orion_driver.models.Destination;
import com.app.orion_driver.models.Order;
import com.app.orion_driver.models.OrderItem;
import com.app.orion_driver.models.OrionAddress;
import com.app.orion_driver.models.Paid;
import com.app.orion_driver.models.Payment;
import com.app.orion_driver.models.Product;
import com.app.orion_driver.models.Store;
import com.app.orion_driver.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Commons {
    public static MainActivity mainActivity = null;
    public static File imageFile = null;
    public static RoundedImageView logoBox = null;
    public static CircleImageView photoBox = null;
    public static ImageView background = null;
    public static User thisUser = null;
    public static int curMapTypeIndex = 1;
    public static TextView textView = null;
    public static LinearLayout layout = null;
    public static Store store = null;
    public static ArrayList<File> files = new ArrayList<>();
    public static Product product = null;
    public static OrionAddress selectedOrionAddress = null;

    public static OrderStatus orderStatus = new OrderStatus();
//    public static OrdersActivity ordersActivity = null;
    public static Order order = null;
    public static OrderItem orderItem = null;
    public static Product product1 = null;
    public static Store store1 = null;

    public static Payment payment = null;
    public static Paid paid = null;

    public static boolean mapCameraMoveF = false;
    public static boolean myLocShareF = false;
    public static GoogleMap googleMap = null;
    public static Marker marker = null;
    public static boolean initAlertF = false;

    public static OrdersActivity ordersActivity = null;

    public static Destination destination = null;

}































