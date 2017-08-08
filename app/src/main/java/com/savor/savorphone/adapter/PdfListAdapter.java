package com.savor.savorphone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.savor.savorphone.R;
import com.savor.savorphone.bean.PdfInfo;

import java.util.List;

/**
 * Created by hezd on 2017/4/26.
 */

public class PdfListAdapter extends BaseAdapter {
    private final Context mContext;
    private List<PdfInfo> mPdfList;

    public PdfListAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<PdfInfo> pdfList) {
        this.mPdfList = pdfList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPdfList==null?0:mPdfList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPdfList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.listview_pdf_item,null);
            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        PdfInfo info = (PdfInfo) getItem(position);
        holder.nameTv.setText(info.getName());
        return convertView;
    }

    class ViewHolder {
        public TextView nameTv;
    }
}
