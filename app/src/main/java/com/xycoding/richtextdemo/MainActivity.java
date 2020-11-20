package com.xycoding.richtextdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.xycoding.richtext.ImageSpanGetter;
import com.xycoding.richtext.RichText;
import com.xycoding.richtext.typeface.CenteredImageSpan;
import com.xycoding.richtext.typeface.ClickSpan;
import com.xycoding.richtext.typeface.FontTypefaceSpan;
import com.xycoding.richtext.typeface.IStyleSpan;
import com.xycoding.richtext.typeface.LinkClickSpan;
import com.xycoding.richtext.typeface.WordClickSpan;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.tv_content);

        initRichTextView(textView);
    }

    private void initRichTextView(final TextView textView) {
        String tagString = "The <a href='https://en.wikipedia.org/wiki/Rich_Text_Format'>Rich Text Format</a> " +
                "is a <c>proprietary</c> <f>document</f> file format with published <bi>specification</bi> " +
                "developed by <t>Microsoft Corporation</t> from 1987 until 2008 for <s>cross-platform</s> document interchange " +
                "with <c1>Microsoft</c1> products. <img src='ic_vip' />";

        final int foregroundTextColor = ContextCompat.getColor(this, R.color.T1);
        final int linkTextColor = ContextCompat.getColor(this, R.color.colorPrimary);
        final int normalTextColor = ContextCompat.getColor(this, R.color.R1);
        final int pressedTextColor = ContextCompat.getColor(this, R.color.W1);
        final int pressedBackgroundColor = ContextCompat.getColor(this, R.color.B1);
        final Typeface georgiaTypeface = Typeface.createFromAsset(getAssets(), "fonts/Georgia Italic.ttf");

        RichText richText = new RichText.Builder()
                .addBlockTypeSpan(new ClickSpan(
                        normalTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new ClickSpan.OnClickListener() {
                            @Override
                            public void onClick(TextView textView, CharSequence text, float rawX, float rawY) {
                                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                            }
                        }), "c")
                .addBlockTypeSpan(new WordClickSpan(
                        normalTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new WordClickSpan.OnWordClickListener() {
                            @Override
                            public void onClick(final WordClickSpan span, TextView textView, CharSequence text, float rawX, float rawY) {
                                AlertDialog dialog = new AlertDialog.Builder(textView.getContext())
                                        .setMessage(text)
                                        .create();
                                dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        span.clearBackgroundColor();
                                    }
                                });
                                dialog.show();
                            }
                        }), "c1")
                .addBlockTypeSpan(new IStyleSpan() {
                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new ForegroundColorSpan(foregroundTextColor);
                    }
                }, "f", "t")
                .addBlockTypeSpan(new IStyleSpan() {
                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new StyleSpan(Typeface.BOLD_ITALIC);
                    }
                }, "bi")
                .addBlockTypeSpan(new IStyleSpan() {
                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new TextAppearanceSpan(MainActivity.this, R.style.TextSize);
                    }
                }, "s")
                .addBlockTypeSpan(new FontTypefaceSpan(georgiaTypeface), "t")
                .addLinkTypeSpan(new LinkClickSpan(
                        linkTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new LinkClickSpan.OnLinkClickListener() {
                            @Override
                            public void onClick(TextView textView, String url) {
                                Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                            }
                        })
                )
                .addImageSpan(new ImageSpanGetter() {
                    @Override
                    public ImageSpan getImageSpan(String src) {
                        final Drawable drawable = getDrawable(textView.getContext(), src);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        return new CenteredImageSpan(drawable);
                    }
                })
                .build();
        //notice: if set click span, you must invoke this method.
        richText.with(textView);
        textView.setText(richText.parse(tagString));
    }

    public static Drawable getDrawable(Context context, String imageName) {
        final int drawableId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        return ContextCompat.getDrawable(context, drawableId);
    }
}
