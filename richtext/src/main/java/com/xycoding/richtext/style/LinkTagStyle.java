package com.xycoding.richtext.style;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.xycoding.richtext.TagBlock;
import com.xycoding.richtext.typeface.LinkClickSpan;

/**
 * Created by xymelon on 2017/4/28.
 */
public class LinkTagStyle extends BlockTagStyle {

    private static final String ATTRIBUTE_HREF = "href";
    private static final String TAG_LINK = "a";

    public LinkTagStyle(LinkClickSpan span) {
        super(span, TAG_LINK);
    }

    @Override
    public void start(TagBlock block, SpannableStringBuilder builder) {
        if (block.getAttributes() != null) {
            ((LinkClickSpan) mStyleSpan).setLinkUrl(block.getAttributes().get(ATTRIBUTE_HREF));
        }
        final int len = builder.length();
        builder.setSpan(this, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

}
