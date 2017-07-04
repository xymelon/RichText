package com.xycoding.richtext.typeface;

import android.text.Spannable;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

/**
 * Created by xymelon on 2017/4/28.
 */
public class WordClickSpan extends ClickSpan {

    private OnWordClickListener mClickListener;

    public WordClickSpan(int normalTextColor,
                         int pressedTextColor,
                         int pressedBackgroundColor,
                         OnWordClickListener listener) {
        super(normalTextColor, pressedTextColor, pressedBackgroundColor, null);
        mClickListener = listener;
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
            mClickListener.onClick(this, textView, pressedText, rawX, rawY);
        }
    }

    @Override
    public CharacterStyle getStyleSpan() {
        return new WordClickSpan(mNormalTextColor, mPressedTextColor, mPressedBackgroundColor, mClickListener);
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
