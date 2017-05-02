package com.xycoding.richtext;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.xycoding.richtext.style.BaseTagStyle;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xymelon on 2017/4/28.
 */
class TagParser extends DefaultHandler {

    private final org.ccil.cowan.tagsoup.Parser mParser;
    private final ArrayList<BaseTagStyle> mBaseTagStyles;
    private SpannableStringBuilder mSpannableStringBuilder;

    TagParser() {
        mParser = new org.ccil.cowan.tagsoup.Parser();
        mBaseTagStyles = new ArrayList<>();
    }

    Spanned parse(String tagString) {
        mSpannableStringBuilder = new SpannableStringBuilder();
        try {
            mParser.setContentHandler(this);
            mParser.parse(new InputSource(new StringReader(tagString)));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
        return mSpannableStringBuilder;
    }

    void addTypefaceStyle(BaseTagStyle listener) {
        mBaseTagStyles.add(listener);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        int attributesLength = attributes.getLength();
        Map<String, String> attributesMap = new HashMap<>(attributesLength);
        for (int i = 0; i < attributesLength; i++) {
            attributesMap.put(attributes.getLocalName(i), attributes.getValue(i));
        }
        TagBlock block = new TagBlock(localName, attributesMap);
        for (BaseTagStyle style : mBaseTagStyles) {
            if (style.match(localName)) {
                style.start(block, mSpannableStringBuilder);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        TagBlock block = new TagBlock(localName);
        for (BaseTagStyle style : mBaseTagStyles) {
            if (style.match(localName)) {
                style.end(block, mSpannableStringBuilder);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        StringBuilder stringBuilder = new StringBuilder();
        mSpannableStringBuilder.append(stringBuilder.append(ch, start, length));
    }

}
