package com.sampili.browsersampili.utils;
/*
* MIT License

Copyright (c) 2017 CGSLURP LLC, author "Poppa Slurp"

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
* */
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import java.util.Vector;

/**
 * Created by Ozymandias on 5/3/2017.
 * setUserAgent will toggle the user agent between mobile and desktop reliably
 * by first removing any anonymous session cookies from the current site and then
 * making the request so that the new user agent we are sending is not ignored.
 */

public class Utils_UserAgent {
    //streamlined desktop UA access
    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36";

    private String getDefaultUA(Context context) {
        return WebSettings.getDefaultUserAgent(context);
    }

    // self explanatory. sets the user agent after clearing any anonymous session cookies.
    public void setUserAgent(Context context, WebView mWebView, boolean choice, String url) {
        if (mWebView != null) {
            String DEFAULT_USER_AGENT = getDefaultUA(context);
            if (choice) {
                CookieManager mCookieManager = CookieManager.getInstance();
                clearCookieByUrl(url, mCookieManager);
                mWebView.getSettings().setUserAgentString(DESKTOP_USER_AGENT);
                mWebView.zoomOut();
            } else {
                CookieManager mCookieManager = CookieManager.getInstance();
                clearCookieByUrl(url, mCookieManager);
                mWebView.getSettings().setUserAgentString(DEFAULT_USER_AGENT);
                mWebView.zoomOut();
            }
        }
    }
    private static void clearCookieByUrl(String url, CookieManager pCookieManager) {
        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            clearCookieByUrlInternal(url,pCookieManager);
            clearCookieByUrlInternal("http://." + host,pCookieManager);
            clearCookieByUrlInternal("https://." + host,pCookieManager);
        } catch (Exception ignored) {

        }
    }
    private static void clearCookieByUrlInternal(String url, CookieManager pCookieManager) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String cookieString = pCookieManager.getCookie(url);
        Vector<String> cookie = getCookieNamesByUrl(cookieString);
        if (cookie == null || cookie.isEmpty()) {
            return;
        }
        int len = cookie.size();
        for (int i = 0; i < len; i++) {
            pCookieManager.setCookie(url, cookie.get(i) + "=-1");
        }
        pCookieManager.flush();
    }
    private static Vector<String> getCookieNamesByUrl(String cookie) {
        if (TextUtils.isEmpty(cookie)) {
            return null;
        }
        String[] cookieField = cookie.split(";");
        int len = cookieField.length;
        for (int i = 0; i < len; i++) {
            cookieField[i] = cookieField[i].trim();
        }
        Vector<String> allCookieField = new Vector<>();
        for (String aCookieField : cookieField) {
            if (TextUtils.isEmpty(aCookieField)) {
                continue;
            }
            if (!aCookieField.contains("=")) {
                continue;
            }
            String[] singleCookieField = aCookieField.split("=");
            allCookieField.add(singleCookieField[0]);
        }
        if (allCookieField.isEmpty()) {
            return null;
        }
        return allCookieField;
    }
}