package com.xycoding.richtext.typeface;

import android.text.style.CharacterStyle;

/**
 * Created by xymelon on 2017/4/28.
 */
public class LinkClickSpan extends ClickSpan {

    private OnLinkClickListener mLinkClickListener;
    private String mUrl;

    public LinkClickSpan(int normalTextColor,
                         int pressedTextColor,
                         int pressedBackgroundColor,
                         OnLinkClickListener listener) {
        super(normalTextColor, pressedTextColor, pressedBackgroundColor, null);
        mLinkClickListener = listener;
    }

    @Override
    public CharacterStyle getStyleSpan() {
        LinkClickSpan span = new LinkClickSpan(mNormalTextColor, mPressedTextColor, mPressedBackgroundColor, mLinkClickListener);
        span.setLinkUrl(mUrl);
        return span;
    }

    public void onClick(float rawX, float rawY) {
        if (mLinkClickListener != null) {
            mLinkClickListener.onClick(mUrl);
        }
    }

    public void setLinkUrl(String url) {
        mUrl = url;
    }

    public interface OnLinkClickListener {
        void onClick(String url);
    }

}
