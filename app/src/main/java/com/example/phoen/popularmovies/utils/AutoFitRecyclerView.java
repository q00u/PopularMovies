package com.example.phoen.popularmovies.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

//from https://github.com/chiuki/android-recyclerview
public class AutoFitRecyclerView extends RecyclerView {

    private GridLayoutManager manager;
    private int columnWidth = -1;

    public AutoFitRecyclerView(Context context) {
        super(context);
        init(context,null);
    }

    public AutoFitRecyclerView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context,attrs);
    }

    public AutoFitRecyclerView(Context context,AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null!=attrs) {
            int[] attrsArray = {android.R.attr.columnWidth};
            TypedArray array = context.obtainStyledAttributes(attrs,attrsArray);
            columnWidth=array.getDimensionPixelSize(0,-1);
            array.recycle();
        }

        manager=new GridLayoutManager(getContext(),1);
        setLayoutManager(manager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if(0<columnWidth) {
            int spanCount = Math.max(1,getMeasuredWidth()/columnWidth);
            manager.setSpanCount(spanCount);
        }
    }
}

