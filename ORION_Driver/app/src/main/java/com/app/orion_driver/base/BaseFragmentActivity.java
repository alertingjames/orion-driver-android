package com.app.orion_driver.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.orion_driver.R;

import java.text.DecimalFormat;
import java.util.concurrent.Callable;

public class BaseFragmentActivity extends AppCompatActivity {

    public Typeface bold, normal, shape, futura, weather;
    public static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getApplicationContext().getAssets(), "futura book font.ttf");
        shape = Typeface.createFromAsset(getApplicationContext().getAssets(), "futura.ttf");

        futura = Typeface.createFromAsset(getApplicationContext().getAssets(), "ag-futura-58e274b5588ad.ttf");
        weather = Typeface.createFromAsset(getApplicationContext().getAssets(), "weather.ttf");

    }

    public void showToast(String content){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = BaseFragmentActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.toast_view, null);
                TextView textView=(TextView)dialogView.findViewById(R.id.text);
                textView.setText(content);
                Toast toast=new Toast(BaseFragmentActivity.this);
                toast.setView(dialogView);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidCellPhone(String number) {
        return Patterns.PHONE.matcher(number).matches();
    }

    public void setupUI(View view, Activity activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    try{
                        hideSoftKeyboard(activity);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void showAlertDialogForQuestion(String title, String content, Activity activity, Callable<Void> nofunc, Callable<Void> yesfunc){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_alert_question, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        TextView titleBox = (TextView)view.findViewById(R.id.title);
        titleBox.setTypeface(bold);
        titleBox.setText(title);
        TextView contentBox = (TextView)view.findViewById(R.id.content);
        contentBox.setTypeface(normal);
        contentBox.setText(content);
        TextView noButton = (TextView)view.findViewById(R.id.no_button);
        noButton.setTypeface(bold);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nofunc.call();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        TextView yesButton = (TextView)view.findViewById(R.id.yes_button);
        yesButton.setTypeface(bold);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    yesfunc.call();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        ImageView cancelButton = (ImageView)view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCancelable(false);
    }


    public void showAlertDialog(String title, String content, Activity activity, Callable<Void> func){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_alert_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        TextView titleBox = (TextView)view.findViewById(R.id.title);
        titleBox.setTypeface(bold);
        titleBox.setText(title);
        TextView contentBox = (TextView)view.findViewById(R.id.content);
        contentBox.setTypeface(normal);
        contentBox.setText(content);
        TextView okButton = (TextView)view.findViewById(R.id.btn_ok);
        okButton.setTypeface(bold);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    func.call();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        ImageView cancelButton = (ImageView)view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCancelable(false);
    }

}



































