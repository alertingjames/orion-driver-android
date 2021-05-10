package com.app.orion_driver.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_driver.R;
import com.app.orion_driver.adapters.PictureListAdapter;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.commons.ReqConst;
import com.app.orion_driver.models.Picture;
import com.bumptech.glide.Glide;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.appbar.AppBarLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PDetailFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    FrameLayout progressBar;
    Toolbar toolbar;
    private int mMaxScrollSize;
    private boolean mIsImageHidden = false;

    TextView priceBox, oldPriceBox, brandNameBox, categoryBox, storeNameBox, nameBox, descriptionBox, deliveryBox;
    FrameLayout oldPriceFrame;
    CircleImageView storeLogoBox;
    RoundedImageView brandLogoBox;

    RatingBar ratingBar;
    TextView ratingsBox;

    ViewPager pager;
    PageIndicatorView pageIndicatorView;
    ArrayList<Picture> pictures = new ArrayList<>();
    PictureListAdapter adapter = null;

    Typeface bold, normal;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_pdetail, viewGroup, false);

        bold = Typeface.createFromAsset(getActivity().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        toolbar = (Toolbar) view.findViewById(R.id.flexible_example_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ratingBar = (RatingBar)view.findViewById(R.id.ratingbar);
        ratingsBox = (TextView)view.findViewById(R.id.ratings);
        ratingBar.setRating(Commons.product1.getRatings());
        ratingsBox.setText(String.valueOf(Commons.product1.getRatings()));

        AppBarLayout appbarLayout = (AppBarLayout) view.findViewById(R.id.flexible_example_appbar);

        progressBar = (FrameLayout) view.findViewById(R.id.loading_bar);

        priceBox = (TextView)view.findViewById(R.id.priceBox);
        oldPriceBox = (TextView)view.findViewById(R.id.oldPriceBox);
        brandNameBox = (TextView)view.findViewById(R.id.brandNameBox);
        categoryBox = (TextView)view.findViewById(R.id.categoryBox);
        storeNameBox = (TextView)view.findViewById(R.id.storeNameBox);
        nameBox = (TextView)view.findViewById(R.id.nameBox);
        descriptionBox = (TextView)view.findViewById(R.id.descriptionBox);
        oldPriceFrame = (FrameLayout)view.findViewById(R.id.oldPriceFrame);

        deliveryBox = (TextView)view.findViewById(R.id.deliveryBox);
        deliveryBox.setTypeface(normal);
        deliveryBox.setText(String.valueOf(Commons.product1.getDelivery_days()) + " Days, " + String.valueOf(Commons.product1.getDelivery_price()) + " SGD");

        ((TextView)view.findViewById(R.id.caption)).setTypeface(bold);
        priceBox.setTypeface(bold);
        brandNameBox.setTypeface(bold);
        oldPriceBox.setTypeface(normal);
        descriptionBox.setTypeface(normal);
        categoryBox.setTypeface(normal);

        categoryBox.setText(Commons.product1.getCategory() + "|" + Commons.product1.getSubcategory() + "|" + Commons.product1.getGender());

        brandLogoBox = (RoundedImageView) view.findViewById(R.id.brandLogoBox);

        storeLogoBox = (CircleImageView)view.findViewById(R.id.storeLogoBox);
        Glide.with(this).load(Commons.store1.getLogoUrl()).into(storeLogoBox);

        storeNameBox.setText(Commons.store1.getName());

        storeNameBox.setTypeface(bold);
        brandNameBox.setTypeface(bold);
        nameBox.setTypeface(bold);

        pager = view.findViewById(R.id.viewPager);
        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        return view;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        Log.d("Percentage+++", String.valueOf(currentScrollPercentage));

        if (currentScrollPercentage >= 10) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view)).setVisibility(View.VISIBLE);
                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view))
                        .animate()
                        .alpha(1.0f)
                        .setDuration(500)
                        .start();
            }
        }else if (currentScrollPercentage <= 20) {
            if (mIsImageHidden) {
                mIsImageHidden = false;

                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view))
                        .animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .start();
                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view)).setVisibility(View.GONE);
            }
        }
    }

    private void getProductPictures(String productId) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getProductPictures")
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
                            String success = response.getString("result_code");
                            Log.d("Rcode=====> :",success);
                            if (success.equals("0")) {
                                pictures.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Picture picture = new Picture();
                                    picture.setIdx(object.getInt("id"));
                                    picture.setUrl(object.getString("picture_url"));
                                    picture.setFile(null);
                                    pictures.add(picture);
                                }

                                Commons.product1.set_pictureList(pictures);
                                if(getActivity() != null){
                                    adapter = new PictureListAdapter(getActivity());
                                    adapter.setDatas(pictures);
                                    adapter.notifyDataSetChanged();
                                    pager.setAdapter(adapter);
                                }

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
//                        toInit();
                    }
                });
    }

    public void showToast(String content){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.toast_view, null);
                TextView textView=(TextView)dialogView.findViewById(R.id.text);
                textView.setText(content);
                Toast toast=new Toast(getActivity());
                toast.setView(dialogView);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        toolbar.setTitle(Commons.product1.getName());
        nameBox.setText(Commons.product1.getName());
        if(Commons.product1.getNew_price() == 0){
            oldPriceFrame.setVisibility(View.GONE);
            priceBox.setText(String.valueOf(Commons.product1.getPrice()) + " SGD");
        }
        else {
            oldPriceFrame.setVisibility(View.VISIBLE);
            priceBox.setText(String.valueOf(Commons.product1.getNew_price()) + " SGD");
            oldPriceBox.setText(String.valueOf(Commons.product1.getPrice()) + " SGD");
        }

        descriptionBox.setText(Commons.product1.getDescription());
        Glide.with(this).load(Commons.product1.getBrand_logo()).into(brandLogoBox);
        brandNameBox.setText(Commons.product1.getBrand_name());

        getProductPictures(String.valueOf(Commons.product1.getIdx()));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}


























