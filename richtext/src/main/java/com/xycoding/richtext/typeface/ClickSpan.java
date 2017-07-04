package com.xycoding.richtext.typeface;

import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by xymelon on 2017/4/28.
 */
public class ClickSpan extends ClickableSpan implements IStyleSpan {

    private final static int BACK_GROUND_PRESSED_DURATION = 50;

    private OnClickListener mClickListener;
    protected int mNormalTextColor;
    protected int mPressedTextColor;
    protected int mPressedBackgroundColor;
    protected View mClickView;
    protected int mClickStart;
    protected int mClickEnd;
    protected Spannable mCachedSpannable;
    protected CharacterStyle[] mCachedStyles;
    protected ForegroundColorSpan mForegroundColorSpan;
    protected BackgroundColorSpan mBackgroundColorSpan;

    public ClickSpan(int normalTextColor,
                     int pressedTextColor,
                     int pressedBackgroundColor,
                     OnClickListener listener) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
        mClickListener = listener;
        mForegroundColorSpan = new ForegroundColorSpan(mPressedTextColor);
        mBackgroundColorSpan = new BackgroundColorSpan(mPressedBackgroundColor);
    }

    @Override
    public void onClick(View widget) {
        mClickView = widget;
    }

    @Override
    public CharacterStyle getStyleSpan() {
        return new ClickSpan(mNormalTextColor, mPressedTextColor, mPressedBackgroundColor, mClickListener);
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.setColor(mNormalTextColor);
        paint.setUnderlineText(false);
    }

    public void onClick(TextView textView, String pressedText, float rawX, float rawY, Spannable spannable, int start, int end) {
        mCachedSpannable = spannable;
        mClickStart = start;
        mClickEnd = end;
        mCachedStyles = spannable.getSpans(start, end, CharacterStyle.class);
        if (mCachedStyles != null) {
            //clear spans
            for (CharacterStyle style : mCachedStyles) {
                if (!(style instanceof TypefaceSpan)) {
                    spannable.removeSpan(style);
                }
            }
        }
        //add pressed span
        spannable.setSpan(mBackgroundColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(mForegroundColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (mClickListener != null) {
            mClickListener.onClick(textView, pressedText, rawX, rawY);
        }
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                clearBackgroundColor();
            }
        }, BACK_GROUND_PRESSED_DURATION);
    }

    public void clearBackgroundColor() {
        if (mCachedSpannable != null) {
            //clear pressed span
            mCachedSpannable.removeSpan(mBackgroundColorSpan);
            mCachedSpannable.removeSpan(mForegroundColorSpan);
            if (mCachedStyles != null) {
                //add original span
                for (CharacterStyle style : mCachedStyles) {
                    if (!(style instanceof TypefaceSpan)) {
                        mCachedSpannable.setSpan(style, mClickStart, mClickEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        if (mClickView != null) {
            mClickView.invalidate();
        }
    }

    public void setNormalTextColor(int color) {
        mNormalTextColor = color;
    }

    public void setPressedTextColor(int color) {
        mPressedTextColor = color;
    }

    public void setPressedBackgroundColor(int color) {
        mPressedBackgroundColor = color;
    }

    public interface OnClickListener {
        /**
         * Called when a word has been clicked.
         *
         * @param textView clicked text view.
         * @param text     clicked word.
         * @param rawX     original raw X coordinate on the screen of this word's center.
         * @param rawY     original raw Y coordinate on the screen of this word's center.
         */
        void onClick(TextView textView, CharSequence text, float rawX, float rawY);
    }

}
