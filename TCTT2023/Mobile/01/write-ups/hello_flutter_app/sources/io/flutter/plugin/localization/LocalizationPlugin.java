package io.flutter.plugin.localization;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import io.flutter.embedding.engine.systemchannels.LocalizationChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class LocalizationPlugin {
    private final Context context;
    private final LocalizationChannel localizationChannel;
    final LocalizationChannel.LocalizationMessageHandler localizationMessageHandler;

    public LocalizationPlugin(Context context, LocalizationChannel localizationChannel) {
        LocalizationChannel.LocalizationMessageHandler localizationMessageHandler = new LocalizationChannel.LocalizationMessageHandler() { // from class: io.flutter.plugin.localization.LocalizationPlugin.1
            @Override // io.flutter.embedding.engine.systemchannels.LocalizationChannel.LocalizationMessageHandler
            public String getStringResource(String key, String localeString) {
                Context localContext = LocalizationPlugin.this.context;
                String stringToReturn = null;
                Locale savedLocale = null;
                if (localeString != null) {
                    Locale locale = LocalizationPlugin.localeFromString(localeString);
                    if (Build.VERSION.SDK_INT < 17) {
                        Resources resources = LocalizationPlugin.this.context.getResources();
                        Configuration config = resources.getConfiguration();
                        savedLocale = config.locale;
                        config.locale = locale;
                        resources.updateConfiguration(config, null);
                    } else {
                        Configuration config2 = new Configuration(LocalizationPlugin.this.context.getResources().getConfiguration());
                        config2.setLocale(locale);
                        localContext = LocalizationPlugin.this.context.createConfigurationContext(config2);
                    }
                }
                String packageName = LocalizationPlugin.this.context.getPackageName();
                int resId = localContext.getResources().getIdentifier(key, "string", packageName);
                if (resId != 0) {
                    stringToReturn = localContext.getResources().getString(resId);
                }
                if (localeString != null && Build.VERSION.SDK_INT < 17) {
                    Resources resources2 = LocalizationPlugin.this.context.getResources();
                    Configuration config3 = resources2.getConfiguration();
                    config3.locale = savedLocale;
                    resources2.updateConfiguration(config3, null);
                }
                return stringToReturn;
            }
        };
        this.localizationMessageHandler = localizationMessageHandler;
        this.context = context;
        this.localizationChannel = localizationChannel;
        localizationChannel.setLocalizationMessageHandler(localizationMessageHandler);
    }

    public Locale resolveNativeLocale(List<Locale> supportedLocales) {
        if (supportedLocales == null || supportedLocales.isEmpty()) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            List<Locale.LanguageRange> languageRanges = new ArrayList<>();
            LocaleList localeList = this.context.getResources().getConfiguration().getLocales();
            int localeCount = localeList.size();
            for (int index = 0; index < localeCount; index++) {
                Locale locale = localeList.get(index);
                String fullRange = locale.getLanguage();
                if (!locale.getScript().isEmpty()) {
                    fullRange = fullRange + "-" + locale.getScript();
                }
                if (!locale.getCountry().isEmpty()) {
                    fullRange = fullRange + "-" + locale.getCountry();
                }
                languageRanges.add(new Locale.LanguageRange(fullRange));
                languageRanges.add(new Locale.LanguageRange(locale.getLanguage()));
                languageRanges.add(new Locale.LanguageRange(locale.getLanguage() + "-*"));
            }
            Locale platformResolvedLocale = Locale.lookup(languageRanges, supportedLocales);
            if (platformResolvedLocale != null) {
                return platformResolvedLocale;
            }
            return supportedLocales.get(0);
        } else if (Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList2 = this.context.getResources().getConfiguration().getLocales();
            for (int index2 = 0; index2 < localeList2.size(); index2++) {
                Locale preferredLocale = localeList2.get(index2);
                for (Locale locale2 : supportedLocales) {
                    if (preferredLocale.equals(locale2)) {
                        return locale2;
                    }
                }
                for (Locale locale3 : supportedLocales) {
                    if (preferredLocale.getLanguage().equals(locale3.toLanguageTag())) {
                        return locale3;
                    }
                }
                for (Locale locale4 : supportedLocales) {
                    if (preferredLocale.getLanguage().equals(locale4.getLanguage())) {
                        return locale4;
                    }
                }
            }
            return supportedLocales.get(0);
        } else {
            Locale preferredLocale2 = this.context.getResources().getConfiguration().locale;
            if (preferredLocale2 != null) {
                for (Locale locale5 : supportedLocales) {
                    if (preferredLocale2.equals(locale5)) {
                        return locale5;
                    }
                }
                for (Locale locale6 : supportedLocales) {
                    if (preferredLocale2.getLanguage().equals(locale6.toString())) {
                        return locale6;
                    }
                }
            }
            return supportedLocales.get(0);
        }
    }

    public void sendLocalesToFlutter(Configuration config) {
        List<Locale> locales = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = config.getLocales();
            int localeCount = localeList.size();
            for (int index = 0; index < localeCount; index++) {
                Locale locale = localeList.get(index);
                locales.add(locale);
            }
        } else {
            locales.add(config.locale);
        }
        this.localizationChannel.sendLocales(locales);
    }

    public static Locale localeFromString(String localeString) {
        String[] parts = localeString.replace('_', '-').split("-", -1);
        String languageCode = parts[0];
        String scriptCode = "";
        String countryCode = "";
        int index = 1;
        if (parts.length > 1 && parts[1].length() == 4) {
            scriptCode = parts[1];
            index = 1 + 1;
        }
        if (parts.length > index && parts[index].length() >= 2 && parts[index].length() <= 3) {
            countryCode = parts[index];
            int i = index + 1;
        }
        return new Locale(languageCode, countryCode, scriptCode);
    }
}
