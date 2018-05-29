package com.example.phoen.popularmovies;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View.BaseSavedState;
import android.widget.TextView;
import java.lang.reflect.Field;

//From https://stackoverflow.com/questions/39106454/add-opaque-shadow-outline-to-android-textview
public class OutlineTextView extends android.support.v7.widget.AppCompatTextView {
    private Field colorField;
    private int textColor;
    private int outlineColor;

    public OutlineTextView(Context context) {
        this(context,null);
    }

    public OutlineTextView(Context context, AttributeSet attrs) {
        this(context,attrs,android.R.attr.textViewStyle);
    }

    public OutlineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);

        try {
            colorField = TextView.class.getDeclaredField("mCurTextColor");
            colorField.setAccessible(true);
            //If that failed, none of this will matter, so it stays in the try-catch block
            textColor=getTextColors().getDefaultColor();

            TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.OutlineTextView);
            outlineColor = a.getColor(R.styleable.OutlineTextView_outlineColor, Color.TRANSPARENT);
            setOutlineStrokeWidth(a.getDimensionPixelSize(R.styleable.OutlineTextView_outlineWidth,0));
            a.recycle();
        } catch (NoSuchFieldException e) {
            //FAILURE!
            e.printStackTrace();
            colorField=null;
        }
    }

    public void setOutlineStrokeWidth(float width) {
        getPaint().setStrokeWidth(2*width+1);
    }

    @Override
    public void setTextColor(int color) {
        textColor=color;
        super.setTextColor(color);
    }

    public void setOutlineColor(int color) {
        outlineColor=color;
        invalidate();
    }

    public void setOutlineWidth(float width) {
        setOutlineStrokeWidth(width);
        invalidate();
    }

    private void setColorField(int color) {
        try {
            colorField.setInt(this,color);
        } catch (IllegalAccessException|IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (null!=colorField) {
            //outline
            setColorField(outlineColor);
            getPaint().setStyle(Paint.Style.STROKE);
            super.onDraw(canvas);
            //text
            setColorField(textColor);
            getPaint().setStyle((Paint.Style.FILL));
        }
        super.onDraw(canvas);
    }


    //saved state stuff
    private static class SavedState extends BaseSavedState {
        int textColor;
        int outlineColor;
        float outlineWidth;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            textColor=in.readInt();
            outlineColor=in.readInt();
            outlineWidth=in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(textColor);
            out.writeInt(outlineColor);
            out.writeFloat(outlineWidth);
        }

        public static final Parcelable.Creator<SavedState>
        CREATOR = new Parcelable.Creator<SavedState>(){
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.textColor = textColor;
        ss.outlineColor = outlineColor;
        ss.outlineWidth = getPaint().getStrokeWidth();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        textColor=ss.textColor;
        outlineColor=ss.outlineColor;
        getPaint().setStrokeWidth(ss.outlineWidth);
    }
}
