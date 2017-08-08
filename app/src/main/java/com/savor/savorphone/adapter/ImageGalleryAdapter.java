package com.savor.savorphone.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.savor.savorphone.R;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.ModelPic;
import com.savor.savorphone.utils.blur.RotateTransformation;

import java.util.List;

/**
 * Created by etiennelawlor on 8/20/15.
 */
public class ImageGalleryAdapter extends PagerAdapter {

    // region Member Variables
    private final List<MediaInfo> images;

    // region Constructors
    public ImageGalleryAdapter(Context context, List<MediaInfo> images) {
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fullscreen_image, null);

        final ImageView imageView = (ImageView) view.findViewById(R.id.iv);

        final MediaInfo image = images.get(position);
        Context context = imageView.getContext();
        String path = image.getAssetpath();
        String compoundPath = image.getCompoundPath();
        if(!TextUtils.isEmpty(compoundPath)) {
            path = compoundPath;
        }
        int rotate ;
        if(!TextUtils.isEmpty(compoundPath)) {
            rotate = image.getComRotateValue()%360;
        }
        else {
            rotate = image.getRotatevalue()%360;
        }
        view.setTag(image);
        Glide.with(context).
                load(path).
                centerCrop().
                crossFade().
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                transform(new RotateTransformation(context,rotate)).
                into(imageView);
        container.addView(view, 0);

        return view;
    }

    @Override
    public int getCount() {
        return images==null?0:images.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    // endregion

    private int mChildCount = 0;

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {
        if ( mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
