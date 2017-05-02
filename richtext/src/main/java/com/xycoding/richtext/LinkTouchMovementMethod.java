package com.xycoding.richtext;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

import com.xycoding.richtext.typeface.ClickSpan;

/**
 * Created by xymelon on 2017/4/28.
 */
public class LinkTouchMovementMethod extends LinkMovementMethod {

    private static volatile LinkMovementMethod sInstance;
    private ClickSpan mPressedSpan;

    private LinkTouchMovementMethod() {
    }

    public static MovementMethod getInstance() {
        if (sInstance == null)
            synchronized (LinkTouchMovementMethod.class) {
                if (sInstance == null) {
                    sInstance = new LinkTouchMovementMethod();
                }
            }
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPressedSpan = getPressedSpan(textView, spannable, event);
            if (mPressedSpan != null) {
                int start = spannable.getSpanStart(mPressedSpan);
                int end = spannable.getSpanEnd(mPressedSpan);
                mPressedSpan.setPressed(true, textView.getText().subSequence(start, end));
                Selection.setSelection(spannable, start, end);
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            ClickSpan touchedSpan = getPressedSpan(textView, spannable, event);
            if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                mPressedSpan.setPressed(false, null);
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
        } else {
            if (mPressedSpan != null) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //click callback
                    mPressedSpan.onClick(event.getRawX(), event.getRawX());
                }
                mPressedSpan.setPressed(false, null);
                super.onTouchEvent(textView, spannable, event);
            }
            mPressedSpan = null;
            Selection.removeSelection(spannable);
        }
        return true;
    }

    private ClickSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        ClickSpan[] link = spannable.getSpans(off, off, ClickSpan.class);
        if (link.length > 0) {
            //if multi spans find same position, return outer span.
            return link[link.length - 1];
        }
        return null;
    }

}