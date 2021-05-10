package com.app.orion_driver.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.app.orion_driver.R;
import com.app.orion_driver.main.NotificationsActivity;
import com.app.orion_driver.models.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationListAdapter extends BaseAdapter {
    private NotificationsActivity _context;
    private ArrayList<Notification> _datas = new ArrayList<>();
    private ArrayList<Notification> _alldatas = new ArrayList<>();

    public NotificationListAdapter(NotificationsActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Notification> datas) {

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
            convertView = inflater.inflate(R.layout.item_notification, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.text = (TextView) convertView.findViewById(R.id.notiText);
            holder.contactButton = (ImageView) convertView.findViewById(R.id.btn_contact);
            holder.delButton = (ImageView) convertView.findViewById(R.id.delButton);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Notification entity = (Notification) _datas.get(position);


        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.name.setTypeface(bold);

        Typeface book = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.date.setTypeface(book);
        holder.text.setTypeface(book);

        holder.name.setText(entity.getSender_name());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String myDate = dateFormat.format(new Date(Long.parseLong(entity.getDate_time())));
        holder.date.setText(myDate);

        holder.text.setText(entity.getMessage());

        holder.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity.getSender_name().equals("Administrator")){
//                    Intent intent = new Intent(_context, ContactUsActivity.class);
//                    _context.startActivity(intent);
                }else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + entity.getSender_phone()));
                    _context.startActivity(intent);
                }
            }
        });

        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _context.deleteNotification(entity);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            for (Notification notification : _alldatas){

                if (notification instanceof Notification) {

                    String value = ((Notification) notification).getSender_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(notification);
                    }else {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        String date = dateFormat.format(new Date(Long.parseLong(((Notification) notification).getDate_time())));
                        if (date.contains(charText)) {
                            _datas.add(notification);
                        }else {
                            value = ((Notification) notification).getSender_phone().toLowerCase();
                            if (value.contains(charText)) {
                                _datas.add(notification);
                            }else {
                                value = ((Notification) notification).getMessage().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(notification);
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
        ImageView contactButton, delButton;
        TextView name, date, text;
    }
}






















