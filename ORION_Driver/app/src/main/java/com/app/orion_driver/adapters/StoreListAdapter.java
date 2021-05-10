package com.app.orion_driver.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.orion_driver.R;
import com.app.orion_driver.commons.Commons;
import com.app.orion_driver.main.OrderDetailActivity;
import com.app.orion_driver.models.Store;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StoreListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Store> _datas = new ArrayList<>();
    private ArrayList<Store> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public StoreListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Store> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_ordereds, parent, false);

            holder.pictureBox = (RoundedImageView) convertView.findViewById(R.id.storeLogoBox);
            holder.nameBox = (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.addressBox = (TextView) convertView.findViewById(R.id.addressBox);
            holder.itemsBox = (TextView) convertView.findViewById(R.id.itemsCountBox);
            holder.deliveryDaysBox = (TextView) convertView.findViewById(R.id.deliveryDaysBox);
            holder.dateTimeBox = (TextView) convertView.findViewById(R.id.dateTimeBox);
            holder.distanceBox = (TextView) convertView.findViewById(R.id.distanceBox);
            holder.statusBox = (TextView) convertView.findViewById(R.id.statusBox);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Store entity = (Store) _datas.get(position);

        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        Typeface normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");

        holder.nameBox.setText(entity.getName());
        holder.addressBox.setText(entity.getAddress());
        holder.itemsBox.setText(String.valueOf(entity.getOrderedItemsCount()));
        holder.deliveryDaysBox.setText(String.valueOf(entity.getDelivery_days()) + " Days");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String myDate = dateFormat.format(new Date(Long.parseLong(entity.getOrderedDateTime())));
        holder.dateTimeBox.setText(myDate);
        holder.distanceBox.setText(df.format(entity.getDistance()/1000) + " km");
        if(entity.getOrderdStatus().length() == 0) {
            holder.statusBox.setText("NEW");
            holder.statusBox.setBackgroundColor(Color.RED);
            holder.statusBox.setTextColor(_context.getColor(R.color.white));
        }
        else if(entity.getOrderdStatus().equals("accepted")) {
            holder.statusBox.setText("ONGOING");
            holder.statusBox.setBackgroundColor(_context.getColor(R.color.green));
            holder.statusBox.setTextColor(_context.getColor(R.color.white));
        }
        else if(entity.getOrderdStatus().equals("rejected")) {
            holder.statusBox.setText("REJECTED");
            holder.statusBox.setBackgroundColor(Color.GRAY);
            holder.statusBox.setTextColor(_context.getColor(R.color.white));
        }
        else if(entity.getOrderdStatus().equals("delivered")) {
            holder.statusBox.setText("DELIVERED");
            holder.statusBox.setBackgroundColor(Color.YELLOW);
            holder.statusBox.setTextColor(_context.getColor(R.color.green));
        }

        holder.nameBox.setTypeface(bold);
        holder.statusBox.setTypeface(bold);
        holder.addressBox.setTypeface(normal);
        holder.itemsBox.setTypeface(normal);
        holder.deliveryDaysBox.setTypeface(normal);
        holder.dateTimeBox.setTypeface(normal);
        holder.distanceBox.setTypeface(normal);

        if(entity.getLogoUrl().length() > 0){
            Glide.with(_context)
                    .load(entity.getLogoUrl()).error(R.drawable.logo2)
                    .into(holder.pictureBox);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.store = entity;
                Intent intent = new Intent(_context, OrderDetailActivity.class);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {
            for (Store store : _alldatas){

                if (store instanceof Store) {

                    String value = ((Store) store).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(store);
                    }else {
                        value = ((Store) store).getPhoneNumber().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(store);
                        }else {
                            value = ((Store) store).getAddress().toLowerCase();
                            if (value.contains(charText)) {
                                _datas.add(store);
                            }else {
                                value = String.valueOf(((Store) store).getDelivery_days()).toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(store);
                                }else {
                                    value = ((Store) store).get_status().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(store);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        RoundedImageView pictureBox;
        TextView nameBox, addressBox, itemsBox, deliveryDaysBox, dateTimeBox, distanceBox, statusBox;
    }
}








