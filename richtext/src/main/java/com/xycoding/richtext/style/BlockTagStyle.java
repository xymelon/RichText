package com.xycoding.richtext.style;

import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.xycoding.richtext.TagBlock;
import com.xycoding.richtext.typeface.ClickSpan;
import com.xycoding.richtext.typeface.IStyleSpan;

import java.util.ArrayList;
import java.util.List;

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
        //Insert a marker Span, then locate it based on the marker later and set the actual Span.
        //Note: You must create new instances, otherwise when setting multiple times, subsequent getSpans calls will only retrieve one instance. eg, <b>hello <b>world</b></b>, the 'hello' will not show 'b' color.
        builder.setSpan(new BlockTagStyle(mStyleSpan), len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void end(String tagName, SpannableStringBuilder builder) {
        final int len = builder.length();
        final BlockTagStyle[] styles = builder.getSpans(0, builder.length(), BlockTagStyle.class);
        if (styles.length != 0) {
            //This knows that the last returned object from getSpans() will be the most recently added.
            final Object obj = styles[styles.length - 1];
            final int start = builder.getSpanStart(obj);
            builder.removeSpan(obj);
            if (start != len) {
                builder.setSpan(mStyleSpan.getStyleSpan(), start, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && styles.length > 1) {
                    //In android 6.0, 'ForegroundColorSpan in ForegroundColorSpan' shows wrong color,
                    //so we set outermost 'ForegroundColorSpan' again.
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
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //In android 7.0, 'ClickSpan with foreground color in ForegroundColorSpan' shows wrong color.
                    //eg, '<b><c>Hello</c></b> <b><c>Hello</c></b> <c>world</c>.',
                    //'b' is ForegroundColorSpan and 'c' is ClickSpan with foreground color,
                    //the second 'Hello' shows 'c' color, but we want the 'b' color.
                    //So when exists multi foreground colors in same place, we just retain outermost foreground color.
                    final CharacterStyle[] characterStyles = builder.getSpans(start, len, CharacterStyle.class);
                    if (characterStyles != null && characterStyles.length > 1) {
                        final int stylesLength = characterStyles.length;
                        final List<CharacterStyle> removeStyles = new ArrayList<>();
                        int color = -1;
                        for (int i = stylesLength - 1; i >= 0; i--) {
                            if (color != -1) {
                                for (int j = 0; j <= i; j++) {
                                    final CharacterStyle characterStyle = characterStyles[j];
                                    if (characterStyle instanceof ClickSpan) {
                                        ((ClickSpan) characterStyle).setNormalTextColor(color);
                                    } else if (characterStyle instanceof ForegroundColorSpan) {
                                        removeStyles.add(characterStyle);
                                    }
                                }
                                break;
                            } else {
                                //outermost foreground color
                                final CharacterStyle characterStyle = characterStyles[i];
                                if (characterStyle instanceof ForegroundColorSpan) {
                                    color = ((ForegroundColorSpan) characterStyle).getForegroundColor();
                                } else if (characterStyle instanceof ClickSpan) {
                                    color = ((ClickSpan) characterStyle).getNormalTextColor();
                                }
                            }
                        }
                        for (CharacterStyle style : removeStyles) {
                            builder.removeSpan(style);
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
