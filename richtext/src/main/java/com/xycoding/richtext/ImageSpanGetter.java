package com.xycoding.richtext;

import android.text.style.ImageSpan;

/**
 * Retrieves images for HTML &lt;img&gt; tags.
 */
public interface ImageSpanGetter {
    /**
     * This method is called when the HTML parser encounters an
     * &lt;img&gt; tag.  The <code>src</code> argument is the
     * string from the "src" attribute; the return value should be
     * a ImageSpan. Make sure you call setBounds() on your Drawable
     * if it doesn't already have its bounds set.
     */
    ImageSpan getImageSpan(String src);

}