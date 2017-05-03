package com.xycoding.richtext.style;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

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
        Object obj = getLast(builder, BlockTagStyle.class);
        if (obj != null) {
            int start = builder.getSpanStart(obj);
            builder.removeSpan(obj);
            if (start != len) {
                builder.setSpan(mStyleSpan.getStyleSpan(), start, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private Object getLast(SpannableStringBuilder text, Class kind) {
        /*
         * This knows that the last returned object from getSpans()
         * will be the most recently added.
         */
        Object[] objects = text.getSpans(0, text.length(), kind);
        if (objects.length == 0) {
            return null;
        } else {
            return objects[objects.length - 1];
        }
    }

    @Override
    public boolean match(String tagName) {
        return mTags.contains(tagName);
    }

}
