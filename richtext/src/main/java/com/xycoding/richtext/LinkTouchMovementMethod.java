package com.xycoding.richtext;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import com.xycoding.richtext.typeface.ClickSpan;

/**
 * Created by xymelon on 2017/4/28.
 */
public class LinkTouchMovementMethod extends LinkMovementMethod {

    private static volatile LinkMovementMethod sInstance;
    private ClickSpan mClickSpan;
    private float mClickDownX, mClickDownY;
    private final Path mPath = new Path();
    private final RectF mRectF = new RectF();
    private final Rect mRect = new Rect();
    private int mClickSpanStart;
    private int mClickSpanEnd;
    private GestureDetector mGestureDetector;

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
    public boolean onTouchEvent(final TextView textView, final Spannable spannable, MotionEvent event) {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(textView.getContext().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    mClickSpan = getPressedSpan(textView, spannable, e);
                    if (mClickSpan != null) {
                        mClickSpanStart = spannable.getSpanStart(mClickSpan);
                        mClickSpanEnd = spannable.getSpanEnd(mClickSpan);
                        //word center
                        calcWordCenter(textView, mClickSpanStart, mClickSpanEnd);
                        //click callback
                        mClickSpan.onClick(
                                textView,
                                textView.getText().subSequence(mClickSpanStart, mClickSpanEnd).toString(),
                                mClickDownX,
                                mClickDownY,
                                spannable,
                                mClickSpanStart,
                                mClickSpanEnd
                        );
                    }
                    textView.invalidate();
                    return true;
                }
            });
        }
        final boolean handled = mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            //Be sure to clean up, otherwise memory leaks.
            mClickSpan = null;
            mGestureDetector = null;
        }
        return handled;
    }

    private ClickSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        final Layout layout = textView.getLayout();
        final int line = layout.getLineForVertical(y);

        if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) {
            //click nothing
            return null;
        }

        final int off = layout.getOffsetForHorizontal(line, x);
        ClickSpan[] link = spannable.getSpans(off, off, ClickSpan.class);
        if (link.length > 0) {
            //if multi spans find same position, return outer span.
            return link[link.length - 1];
        }
        return null;
    }

    private void calcWordCenter(TextView textView, int start, int end) {
        //calc selection start position
        mPath.reset();
        textView.getLayout().getCursorPath(start, mPath, textView.getText());
        mPath.computeBounds(mRectF, false);

        final int horizontalPadding = textView.getCompoundPaddingLeft();
        final int verticalPadding = textView.getExtendedPaddingTop() + getVerticalOffset(textView);
        int left = (int) (Math.floor(horizontalPadding + mRectF.left));
        int top = (int) Math.floor(verticalPadding + mRectF.top);
        int bottom = (int) Math.ceil(verticalPadding + mRectF.bottom);

        //calc selection end position
        mPath.reset();
        textView.getLayout().getCursorPath(end, mPath, textView.getText());
        mPath.computeBounds(mRectF, false);
        int right = (int) (Math.floor(horizontalPadding + mRectF.left));

        textView.getGlobalVisibleRect(mRect);
        mClickDownX = mRect.left + left + (right - left) / 2;
        mClickDownY = mRect.top + top + (bottom - top) / 2;
        textView.getLocalVisibleRect(mRect);
        //if TextView hide partially, you should minus the hided height.
        mClickDownY -= mRect.top;
    }

    private int getVerticalOffset(TextView textView) {
        final int gravity = textView.getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        Layout layout = textView.getLayout();
        int vOffset = 0;
        if (gravity != Gravity.TOP) {
            int boxHeight = textView.getMeasuredHeight() - (textView.getExtendedPaddingTop() + textView.getExtendedPaddingBottom());
            int textHeight = layout.getHeight();
            if (textHeight < boxHeight) {
                if (gravity == Gravity.BOTTOM)
                    vOffset = boxHeight - textHeight;
                else // (gravity == Gravity.CENTER_VERTICAL)
                    vOffset = (boxHeight - textHeight) >> 1;
            }
        }
        return vOffset;
    }

}