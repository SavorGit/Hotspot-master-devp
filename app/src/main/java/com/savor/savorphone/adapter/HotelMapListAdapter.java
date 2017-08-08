package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.bean.HotelMapBean;
import com.savor.savorphone.bean.VodBean;
import com.savor.savorphone.core.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * 餐厅适配器
 * Created by hezd on 2017/5/23.
 */

public class HotelMapListAdapter extends BaseAdapter {

    private final Context mContext;
    private List<HotelMapBean> mHotelList = new ArrayList<HotelMapBean>();
    private final Session mSession;

    public HotelMapListAdapter(Context context) {
        this.mContext = context;
        mSession = Session.get(mContext);
    }

    public void setData(List<HotelMapBean> hotelList) {

        if (hotelList!= null && hotelList.size()>0) {

            mHotelList.clear();
            mHotelList.addAll(hotelList);
            notifyDataSetChanged();
        }

    }


    @Override
    public int getCount() {
        return mHotelList==null?0:mHotelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mHotelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.hotel_list_item,null);
            holder.name = (TextView) convertView.findViewById(R.id.hotel_name);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.address = (TextView) convertView.findViewById(R.id.add);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HotelMapBean hotelBean = (HotelMapBean) getItem(position);
        String name = hotelBean.getName();
        String km = hotelBean.getDis();
        String addr = hotelBean.getAddr();
        String id = hotelBean.getId();
        int hotelid = mSession.getHotelid();
        if(String.valueOf(hotelid).equals(id)) {
            holder.distance.setText("当前餐厅");
        }else {
            holder.distance.setText(km);
        }
        holder.name.setText(name);
        holder.address.setText("地址："+addr);
        return convertView;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView distance;
        public TextView address;
    }
}
