package com.app.orion_driver.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.app.orion_driver.R;
import com.app.orion_driver.base.BaseActivity;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.ReqConst;
import com.app.orion_driver.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.iamhabib.easy_preference.EasyPreference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends BaseActivity {

    FrameLayout pictureFrame;
    CircleImageView pictureBox;
    EditText nameBox, emailBox, phoneBox, passwordBox;
    TextView signupButton, loginButton;
    ImageButton showButton;
    TextView label2;
    CheckBox termsBox;
    boolean pwShow = false;
    File imageFile = null;
    ArrayList<File> files = new ArrayList<>();
    FrameLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressBar = (FrameLayout) findViewById(R.id.loading_bar);

        pictureFrame = (FrameLayout)findViewById(R.id.pictureFrame);
        pictureBox = (CircleImageView)findViewById(R.id.pictureBox);

        emailBox = (EditText)findViewById(R.id.emailBox);
        phoneBox = (EditText)findViewById(R.id.phoneBox);
        nameBox = (EditText)findViewById(R.id.nameBox);
        passwordBox = (EditText)findViewById(R.id.passwordBox);

        loginButton = (TextView) findViewById(R.id.btn_login);
        signupButton = (TextView) findViewById(R.id.btn_signup);

        label2 = (TextView) findViewById(R.id.lb2);

        termsBox = (CheckBox)findViewById(R.id.termsCheckBox);

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

        nameBox.setTypeface(normal);
        emailBox.setTypeface(normal);
        passwordBox.setTypeface(normal);
        label2.setTypeface(normal);
        signupButton.setTypeface(bold);
        loginButton.setTypeface(normal);
        termsBox.setTypeface(normal);

        pictureFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SignupActivity.this);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerMember();
            }
        });

        setupUI(findViewById(R.id.activity), this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //From here you can load the image however you need to, I recommend using the Glide library
                imageFile = new File(resultUri.getPath());
                files.clear();
                files.add(imageFile);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    pictureBox.setImageBitmap(bitmap);
                    ((ImageView)findViewById(R.id.cameraButton)).setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void registerMember(){

        if(nameBox.getText().length() == 0){
            showToast("Enter your name.");
            return;
        }

        if(emailBox.getText().length() == 0){
            showToast("Enter your email.");
            return;
        }

        if(!isValidEmail(emailBox.getText().toString().trim())){
            showToast("The email is invalid. Please enter a valid email.");
            return;
        }

        if(phoneBox.getText().length() == 0){
            showToast("Enter your phone number.");
            return;
        }

        if(!isValidCellPhone(phoneBox.getText().toString().trim())){
            showToast("The phone number is invalid. Please enter a valid phone number.");
            return;
        }

        if(passwordBox.getText().length() == 0){
            showToast("Enter your password");
            return;
        }

        if(!termsBox.isChecked()){
            showToast("Please read the Terms and Conditions.");
            return;
        }

        registerMember(files, nameBox.getText().toString().trim(),
                emailBox.getText().toString().trim(),
                phoneBox.getText().toString().trim(),
                passwordBox.getText().toString().trim(),
                "driver");

    }

    public void registerMember(ArrayList<File> files, String name, String email, String phone, String password, String role) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ReqConst.SERVER_URL + "registermember")
                .addMultipartFileList("files", files)
                .addMultipartParameter("name", name)
                .addMultipartParameter("email", email)
                .addMultipartParameter("phone_number", phone)
                .addMultipartParameter("password", password)
                .addMultipartParameter("role", role)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.d("UPLOADED!!!", String.valueOf(bytesUploaded) + "/" + String.valueOf(totalBytes));
                    }
                })
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
                                showToast("Successfully registered.");

                                EasyPreference.with(getApplicationContext(), "user_info")
                                        .addString("email", Commons.thisUser.get_email())
                                        .addString("role", Commons.thisUser.getRole())
                                        .save();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }else if(result.equals("1")){
                                showToast("Someone is using the same email. Please try another one.");
                            }else if(result.equals("2")){
                                showToast("This user has already been registered.");
                                finish();
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
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });
    }


}















