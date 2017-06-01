package com.xycoding.richtext.style;

import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.xycoding.richtext.TagBlock;
import com.xycoding.richtext.typeface.IStyleSpan;

/**
 * Created by xymelon on 2017/4/28.
 */
public class BlockTagStyle extends BaseTagStyle {

    public BlockTagStyle(IStyleSpan span, String... tags) {
        super(span, tags);
    }

    @Override
    public void start(TagBlock block, SpannableStringBuilder builder) {
        final int len = builder.length();
        builder.setSpan(this, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void end(String tagName, SpannableStringBuilder builder) {
        final int len = builder.length();
        BlockTagStyle[] styles = builder.getSpans(0, builder.length(), BlockTagStyle.class);
        if (styles.length != 0) {
            //This knows that the last returned object from getSpans() will be the most recently added.
            Object obj = styles[styles.length - 1];
            int start = builder.getSpanStart(obj);
            builder.removeSpan(obj);
            if (start != len) {
                builder.setSpan(mStyleSpan.getStyleSpan(), start, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //In android 6.0, 'ForegroundColorSpan in ForegroundColorSpan' shows wrong color,
                //so we set outermost 'ForegroundColorSpan' again.
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && styles.length > 1) {
                    for (BlockTagStyle style : styles) {
                        int styleStart = builder.getSpanStart(style);
                        if (start == styleStart) {
                            break;
                        }
                        CharacterStyle span = style.mStyleSpan.getStyleSpan();
                        if (span instanceof ForegroundColorSpan) {
                            builder.setSpan(span, start, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //The outer span will be taken effect.
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean match(String tagName) {
        return mTags.contains(tagName);
    }

}
