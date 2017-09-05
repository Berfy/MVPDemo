package com.wlb.agent.ui.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.core.data.agentservice.entity.BannerEntity;
import com.wlb.agent.util.UrlHandlerResult;
import com.wlb.agent.util.WebUrlHandler;
import com.wlb.common.imgloader.ImageFactory;
import com.wlb.common.imgloader.ImageLoaderImpl;

import java.util.List;

import common.widget.viewpager.AutoScrollPagerAdapter;
import common.widget.viewpager.ViewPager;

/**
 * 首页BannerImage
 *
 * @author 张全
 */
public class BannerImgPageAdapter extends AutoScrollPagerAdapter<BannerEntity> {
    private DisplayImageOptions imageOptions;
    private ViewPager viewPager;
    private static int height = -1;

    public BannerImgPageAdapter(Context context, ViewPager viewPager, List<BannerEntity> list, int layoutId) {
        super(context, list, layoutId);
        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.banner_def)//
                .showImageOnFail(R.drawable.banner_def)//
                .showImageOnLoading(R.drawable.banner_def)//
                .cacheOnDisk(true)//
                .cacheInMemory(true)//
                .decodingOptions(ImageLoaderImpl.getBitmapOptions())//
//                .preProcessor(new BitmapProcessor() {
//
//                    @Override
//                    public Bitmap process(Bitmap bitmap) {
//                        if (height == -1) {
//                            Bitmap scaleBitmap = ImageUtil.scaleBySW(
//                                    LContext.getContext(), bitmap);
//                            if (scaleBitmap != bitmap && !bitmap.isRecycled()) {
//                                bitmap.recycle();
//                            }
//                            return scaleBitmap;
//                        } else {
//                            return bitmap;
//                        }
//                    }
//                })
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        this.viewPager = viewPager;
        if (height != -1) {
            resizeViewPager(height);
        }
    }

    private void resizeViewPager(int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
        params.height = height;
        viewPager.setLayoutParams(params);
    }

    private void loadImg(BannerEntity entity, ImageView imageView) {
        ImageFactory.getUniversalImpl().getImg(entity.thumbImage, imageView,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        viewPager.setBackgroundDrawable(null);
//                        if (null != loadedImage && height == -1) {
//                            height = loadedImage.getHeight();
//                            resizeViewPager(height);
//                        }
                    }
                }, imageOptions);
    }

    @Override
    public void setItemData(View contentView, int position, BannerEntity entity) {
        ImageView imageView = (ImageView) contentView;
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BannerEntity entity = (BannerEntity) v.getTag();
                if (!TextUtils.isEmpty(entity.webUrl)) {
                    UrlHandlerResult urlHandlerResult = WebUrlHandler.handleUrlLink(v.getContext(), entity.webUrl);
                    if (!urlHandlerResult.handled) {
                        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                        webViewParam.url = urlHandlerResult.url;
                        WebViewFrag.start(v.getContext(), webViewParam);
                    }
                }
            }
        });
        imageView.setTag(entity);
        loadImg(entity, imageView);
    }

}