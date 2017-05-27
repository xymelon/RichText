package com.xycoding.richtext.typeface;

import android.text.style.CharacterStyle;

/**
 * Created by xymelon on 2017/4/28.
 */
public interface IStyleSpan<T extends CharacterStyle> {
    T getStyleSpan();
}
