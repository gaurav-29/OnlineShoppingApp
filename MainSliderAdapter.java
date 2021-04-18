package com.android_batch_31.designdemo;

import java.util.ArrayList;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    private ArrayList<String> ImageList;
    private String SliderImagePath;

    public MainSliderAdapter(ArrayList<String> imageList) {
        ImageList = imageList;
        SliderImagePath = Common.getBaseImageUrl() + "slider/";
    }

    @Override
    public int getItemCount() {
        return ImageList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        viewHolder.bindImageSlide(SliderImagePath + ImageList.get(position));

    }
}