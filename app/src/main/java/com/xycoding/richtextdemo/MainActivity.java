package com.xycoding.richtextdemo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.xycoding.richtext.RichText;
import com.xycoding.richtext.typeface.ClickSpan;
import com.xycoding.richtext.typeface.IStyleSpan;
import com.xycoding.richtext.typeface.LinkClickSpan;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.tv_content);

        initRichTextView(textView);
    }

    private void initRichTextView(TextView textView) {
        final int foregroundTextColor = ContextCompat.getColor(this, R.color.T1);
        final int linkTextColor = ContextCompat.getColor(this, R.color.colorPrimary);
        int normalTextColor = ContextCompat.getColor(this, R.color.R1);
        int pressedTextColor = ContextCompat.getColor(this, R.color.W1);
        int pressedBackgroundColor = ContextCompat.getColor(this, R.color.B1);
        RichText richText = new RichText.Builder()
                .addBlockTypeSpan(new ClickSpan(
                        normalTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new ClickSpan.OnClickListener() {
                            @Override
                            public void onClick(CharSequence text, float rawX, float rawY) {
                                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                            }
                        }), "c")
                .addBlockTypeSpan(new IStyleSpan() {
                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new ForegroundColorSpan(foregroundTextColor);
                    }
                }, "f")
                .addBlockTypeSpan(new IStyleSpan() {
                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new StyleSpan(Typeface.BOLD_ITALIC);
                    }
                }, "bi")
                .addLinkTypeSpan(new LinkClickSpan(
                        linkTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new LinkClickSpan.OnLinkClickListener() {
                            @Override
                            public void onClick(String url) {
                                Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                            }
                        })
                )
                .build();
        richText.with(textView);

        String tagString = "The <a href='https://en.wikipedia.org/wiki/Rich_Text_Format'>Rich Text Format</a> is a <c>proprietary</c> <f>document</f> file format with published <bi>specification</bi> developed by Microsoft Corporation from 1987 until 2008 for cross-platform document interchange with Microsoft products.";
        textView.setText(richText.parse(tagString));
    }
}
