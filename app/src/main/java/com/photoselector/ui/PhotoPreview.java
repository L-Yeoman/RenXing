package com.photoselector.ui;
/**
 * @author Aizaz AZ
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dldzkj.app.renxing.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.photoselector.model.PhotoModel;
import com.polites.GestureImageView;

public class PhotoPreview extends LinearLayout implements OnClickListener {

    private ProgressBar pbLoading;
    private GestureImageView ivContent;
    private ImageView gifView;
    private OnClickListener l;
    private RequestManager rm;

    public PhotoPreview(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_photopreview, this, true);
        rm = Glide.with(context);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading_vpp);
        ivContent = (GestureImageView) findViewById(R.id.iv_content_vpp);
        gifView = (ImageView) findViewById(R.id.gif_view);
        ivContent.setOnClickListener(this);
    }

    public PhotoPreview(Context context, AttributeSet attrs, int defStyle) {
        this(context);
    }

    public PhotoPreview(Context context, AttributeSet attrs) {
        this(context);
    }

    public void loadImage(PhotoModel photoModel) {
        loadImage(photoModel.getOriginalPath());
    }

    private void loadImage(String path) {
            if(path.endsWith("gif")){
                ivContent.setVisibility(View.GONE);
                gifView.setVisibility(View.VISIBLE);
                rm.load(path).asGif().into(gifView);
            }else {
                ivContent.setVisibility(View.VISIBLE);
                gifView.setVisibility(View.GONE);
                rm.load(path).into(ivContent);
            }

        /*ImageLoader.getInstance().loadImage(path, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				ivContent.setImageBitmap(loadedImage);
				pbLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				ivContent.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_loadfailed));
				pbLoading.setVisibility(View.GONE);
			}
		});*/
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.l = l;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_content_vpp && l != null)
            l.onClick(gifView);
    }

    ;

}
