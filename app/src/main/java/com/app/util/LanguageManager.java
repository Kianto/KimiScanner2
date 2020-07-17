package com.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.app.kimiscanner.R;

import java.util.ArrayList;
import java.util.List;

public class LanguageManager {
    public static String LANG_ENGLISH_CODE = "en";
    public static String LANG_VIETNAMESE_CODE = "vi";

    @SuppressLint("StaticFieldLeak")
    private static LanguageManager manager;
    private LanguageManager(Context context) {
        this.context = context;
    }

    public static LanguageManager setInstance(Context context) {
        if (null == manager) {
            manager = new LanguageManager(context);
        }
        return manager;
    }
    public static LanguageManager getInstance() {
        return manager;
    }

    Context context;
    private Context curContext;
    List<LanguageChangeListener> listeners;

    public void changeLanguage(String languageCode) {
        curContext = LocaleHelper.setLocale(this.context, languageCode);
        final Resources resources = curContext.getResources();
        if (null != listeners)
            for (LanguageChangeListener listener : listeners) {
                listener.updateViews(resources);
            }
    }
    public void changeLanguage() {
        String languageCode = getCurrentLanguage().equals(LANG_ENGLISH_CODE) ? LANG_VIETNAMESE_CODE : LANG_ENGLISH_CODE;

        curContext = LocaleHelper.setLocale(this.context, languageCode);
        final Resources resources = curContext.getResources();

        if (null != listeners)
            for (LanguageChangeListener listener : listeners) {
                listener.updateViews(resources);
            }
    }

    public void register(LanguageChangeListener listener) {
        if (null == listeners) {
            listeners = new ArrayList<LanguageChangeListener>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public String getCurrentLanguage() {
        return LocaleHelper.getLanguage(context);
    }

    public Resources getCurrentResources() {
        if (null == curContext) curContext = context;
        return curContext.getResources();
    }

    public String getString(int id) {
        if (null == curContext) curContext = context;
        return curContext.getResources().getString(id);
    }

    public interface LanguageChangeListener {
        void updateViews(final Resources resources);
    }

}
