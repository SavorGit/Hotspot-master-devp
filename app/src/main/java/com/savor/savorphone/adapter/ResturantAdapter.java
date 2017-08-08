package com.savor.savorphone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.bean.HotelMapBean;
import com.savor.savorphone.core.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * 餐厅适配器
 * Created by hezd on 2017/5/23.
 */

public class ResturantAdapter extends RecyclerView.Adapter<ResturantAdapter.ResturantHolder> {

    private final Context mContext;
    private List<HotelMapBean> mHotelList = new ArrayList<>();
    private final Session mSession;

    public ResturantAdapter(Context context) {
        this.mContext = context;
        mSession = Session.get(mContext);
    }

    public void setData(List<HotelMapBean> hotelList) {
        if(hotelList!=null&&hotelList.size()>0) {
            this.mHotelList.clear();
            mHotelList.addAll(hotelList);
            notifyDataSetChanged();
        }
    }

    @Override
    public ResturantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext,R.layout.layout_resturant,null);
        ResturantHolder holder = new ResturantHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ResturantHolder holder, int position) {
        HotelMapBean hotelMapBean = mHotelList.get(position);
        holder.name.setText(hotelMapBean.getName());
        String hotelid = String.valueOf(mSession.getHotelid());
        String id = hotelMapBean.getId();
        if(hotelid.equals(id)) {
            holder.distance.setText("当前餐厅");
        }else {
            holder.distance.setText(hotelMapBean.getDis());
        }
        holder.address.setText(hotelMapBean.getAddr());
    }

    @Override
    public int getItemCount() {
        return mHotelList==null?0:mHotelList.size();
    }


//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public int getItemCount() {
//        return mHotelList==null?0:mHotelList.size();
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//
//        if(convertView==null) {
//            holder = new ViewHolder();
//            convertView = View.inflate(mContext, R.layout.layout_resturant,null);
//            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.distance = (TextView) convertView.findViewById(R.id.tv_distance);
//            holder.address = (TextView) convertView.findViewById(R.id.tv_address);
//            convertView.setTag(holder);
//        }else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        HotelMapBean hotelBean = (HotelMapBean) getItem(position);
//        String name = hotelBean.getName();
//        String km = hotelBean.getDis();
//        String addr = hotelBean.getAddr();
//        String id = hotelBean.getId();
//        int hotelid = mSession.getHotelid();
//        if(String.valueOf(hotelid).equals(id)) {
//            holder.distance.setText("当前餐厅");
//        }else {
//            holder.distance.setText(km);
//        }
//        holder.name.setText(name);
//        holder.address.setText(addr);
//        return convertView;
//    }

    public class ResturantHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView distance;
        public TextView address;

        public ResturantHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            distance = (TextView) itemView.findViewById(R.id.tv_distance);
            address = (TextView) itemView.findViewById(R.id.tv_address);
        }
    }
}
