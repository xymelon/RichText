package com.xycoding.richtext;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

import com.xycoding.richtext.typeface.ClickSpan;

/**
 * Created by xymelon on 2017/4/28.
 */
public class LinkTouchMovementMethod extends LinkMovementMethod {

    private static volatile LinkMovementMethod sInstance;
    private ClickSpan mPressedSpan;
    private float mClickDownX, mClickDownY;
    private Path mPath = new Path();
    private RectF mRectF = new RectF();
    private Rect mRect = new Rect();

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
                calcWordCenter(textView, start, end);
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
                    mPressedSpan.onClick(mClickDownX, mClickDownY);
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

    private void calcWordCenter(TextView textView, int start, int end) {
        textView.getGlobalVisibleRect(mRect);

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

        mClickDownX = mRect.left + left + (right - left) / 2;
        mClickDownY = mRect.top + top + (bottom - top) / 2;
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