package com.app.orion_driver.classes;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newType;
    private final float newSp;

    public CustomTypefaceSpan(String family, Typeface type, float sp) {
        super(family);
        newType = type;
        newSp = sp;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType, newSp);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType, newSp);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf, float sp) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTextSize(sp);
        paint.setTypeface(tf);
    }

    @SuppressWarnings("unused")
    public static final Creator<CustomTypefaceSpan> CREATOR = new Creator<CustomTypefaceSpan>() {
        @Override
        public CustomTypefaceSpan createFromParcel(Parcel in) {
            return null;
        }

        @Override
        public CustomTypefaceSpan[] newArray(int size) {
            return new CustomTypefaceSpan[size];
        }
    };
}