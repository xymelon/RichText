package com.xycoding.richtext;

import android.text.Spanned;
import android.widget.TextView;

import com.xycoding.richtext.style.BaseTagStyle;
import com.xycoding.richtext.style.BlockTagStyle;
import com.xycoding.richtext.style.LinkTagStyle;
import com.xycoding.richtext.typeface.IStyleSpan;
import com.xycoding.richtext.typeface.LinkClickSpan;

/**
 * Created by xymelon on 2017/4/28.
 */
public class RichText {

    private final TagParser mTagParser;

    private RichText(TagParser parser) {
        mTagParser = parser;
    }

    public void with(TextView textView) {
        textView.setMovementMethod(LinkTouchMovementMethod.getInstance());
    }

    public Spanned parse(String tagString) {
        // add a tag at the start that is not handled by default,
        // because custom tag handler does not work for the first html tag, thus a fake tag has to be added at the start.
        return mTagParser.parse("<inject/>" + tagString);
    }

    public static class Builder {

        private TagParser mParser;

        public Builder() {
            mParser = new TagParser();
        }

        public Builder addBlockTypeSpan(IStyleSpan span, String... tags) {
            mParser.addTypefaceStyle(new BlockTagStyle(span, tags));
            return this;
        }

        public Builder addLinkTypeSpan(LinkClickSpan span) {
            mParser.addTypefaceStyle(new LinkTagStyle(span));
            return this;
        }

        public Builder addTypeSpan(BaseTagStyle style) {
            mParser.addTypefaceStyle(style);
            return this;
        }

        public RichText build() {
            return new RichText(mParser);
        }

    }

}
