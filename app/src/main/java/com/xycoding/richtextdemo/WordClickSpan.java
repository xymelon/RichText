package com.xycoding.richtextdemo;

import android.text.style.CharacterStyle;
import android.view.View;
import android.widget.TextView;

import com.xycoding.richtext.typeface.ClickSpan;

/**
 * Created by xymelon on 2017/4/28.
 */
public class WordClickSpan extends ClickSpan {

    private int mPressedBackgroundColor;
    private int mNormalTextColor;
    private int mPressedTextColor;
    private OnWordClickListener mClickListener;
    private CharSequence mPressedText;
    private View mView;

    public WordClickSpan(int normalTextColor,
                         int pressedTextColor,
                         int pressedBackgroundColor,
                         OnWordClickListener listener) {
        super(normalTextColor, pressedTextColor, pressedBackgroundColor, null);
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
        mClickListener = listener;
    }

    @Override
    public void onClick(View widget) {
        mView = widget;
    }

    @Override
    public void onClick(TextView textView, float rawX, float rawY) {
        if (mClickListener != null) {
            mClickListener.onClick(this, textView, mPressedText, rawX, rawY);
        }
    }

    @Override
    public CharacterStyle getStyleSpan() {
        return new WordClickSpan(mNormalTextColor, mPressedTextColor, mPressedBackgroundColor, mClickListener);
    }

    @Override
    public void setPressed(boolean pressed, CharSequence pressedText) {
        super.setPressed(pressed, pressedText);
        mPressedText = pressedText;
    }

    public void clearBackgroundColor() {
        setPressed(false, null);
        if (mView != null) {
            mView.invalidate();
        }
    }

    public interface OnWordClickListener {
        /**
         * Called when a word has been clicked.
         *
         * @param span     word span.
         * @param textView clicked text view.
         * @param text     click word.
         * @param rawX     original raw X coordinate on the screen of this word's center.
         * @param rawY     original raw Y coordinate on the screen of this word's center.
         */
        void onClick(WordClickSpan span, TextView textView, CharSequence text, float rawX, float rawY);
    }

}
