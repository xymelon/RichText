package com.xycoding.richtext.typeface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import java.lang.ref.WeakReference;

/**
 * Created by xuyang on 2016/9/18.
 */
public class CenteredImageSpan extends ImageSpan {

    private WeakReference<Drawable> mDrawableRef;

    public CenteredImageSpan(Drawable drawable) {
        super(drawable);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {
        Drawable drawable = getCachedDrawable();
        canvas.save();

        int drawableCenter = drawable.getIntrinsicHeight() / 2;
        int fontTop = paint.getFontMetricsInt().top;
        int fontBottom = paint.getFontMetricsInt().bottom;
        int transY = bottom - drawable.getBounds().bottom - (((fontBottom - fontTop) / 2) - drawableCenter);

        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

    // Redefined locally because it is a private member from DynamicDrawableSpan
    private Drawable getCachedDrawable() {
        WeakReference<Drawable> drawableRef = mDrawableRef;
        Drawable drawable = null;
        if (drawableRef != null)
            drawable = drawableRef.get();
        if (drawable == null) {
            drawable = getDrawable();
            mDrawableRef = new WeakReference<>(drawable);
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
        return drawable;
    }

}
