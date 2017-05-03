package com.xycoding.richtext.style;

import android.text.SpannableStringBuilder;

import com.xycoding.richtext.TagBlock;
import com.xycoding.richtext.typeface.IStyleSpan;

import java.util.Arrays;
import java.util.List;

/**
 * Created by xymelon on 2017/4/28.
 */
public abstract class BaseTagStyle {

    protected List<String> mTags;
    protected IStyleSpan mStyleSpan;

    public BaseTagStyle(IStyleSpan span, String... tags) {
        mStyleSpan = span;
        mTags = Arrays.asList(tags);
    }

    abstract public void start(TagBlock block, SpannableStringBuilder builder);

    abstract public void end(String tagName, SpannableStringBuilder builder);

    abstract public boolean match(String tagName);

}
