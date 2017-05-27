package com.xycoding.richtext.style;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.xycoding.richtext.ImageSpanGetter;
import com.xycoding.richtext.TagBlock;

/**
 * Created by xymelon on 2017/4/28.
 */
public class ImageTagStyle extends BlockTagStyle {

    private static final String UNICODE_REPLACE = "\uFFFC";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String TAG_IMAGE = "img";

    private ImageSpanGetter mImageSpanGetter;

    public ImageTagStyle(ImageSpanGetter getter) {
        super(null, TAG_IMAGE);
        mImageSpanGetter = getter;
    }

    @Override
    public void start(TagBlock block, SpannableStringBuilder builder) {
        if (block.getAttributes() != null) {
            String src = block.getAttributes().get(ATTRIBUTE_SRC);
            if (!TextUtils.isEmpty(src)) {
                ImageSpan span = mImageSpanGetter.getImageSpan(src);
                if (span == null) {
                    return;
                }
                final int len = builder.length();
                builder.append(UNICODE_REPLACE);
                builder.setSpan(
                        span,
                        len,
                        builder.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    @Override
    public void end(String tagName, SpannableStringBuilder builder) {
        //do nothing.
    }

}
