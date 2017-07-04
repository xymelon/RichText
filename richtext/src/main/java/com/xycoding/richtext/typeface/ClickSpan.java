package com.xycoding.richtext.typeface;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by xymelon on 2017/4/28.
 */
public class ClickSpan extends ClickableSpan implements IStyleSpan {

    protected int mNormalTextColor;
    protected int mPressedTextColor;
    protected int mPressedBackgroundColor;
    protected CharSequence mPressedText;
    protected OnClickListener mClickListener;
    protected boolean mPressed;

    public ClickSpan(int normalTextColor,
                     int pressedTextColor,
                     int pressedBackgroundColor,
                     OnClickListener listener) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
        mClickListener = listener;
    }

    @Override
    public void onClick(View widget) {
        //do nothing.
    }

    public void onClick(TextView textView, float rawX, float rawY) {
        if (mClickListener != null) {
            mClickListener.onClick(textView, mPressedText, rawX, rawY);
        }
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        super.updateDrawState(paint);
        paint.setColor(mPressed ? mPressedTextColor : mNormalTextColor);
        paint.bgColor = mPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
        paint.setUnderlineText(false);
    }

    @Override
    public CharacterStyle getStyleSpan() {
        return new ClickSpan(mNormalTextColor, mPressedTextColor, mPressedBackgroundColor, mClickListener);
    }

    public void setPressed(boolean pressed, CharSequence pressedText) {
        mPressed = pressed;
        mPressedText = pressedText;
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
