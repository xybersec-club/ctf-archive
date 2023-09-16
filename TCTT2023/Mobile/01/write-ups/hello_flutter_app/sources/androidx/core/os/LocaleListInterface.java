package androidx.core.os;

import java.util.Locale;
/* loaded from: classes.dex */
interface LocaleListInterface {
    Locale get(int index);

    Locale getFirstMatch(String[] supportedLocales);

    Object getLocaleList();

    int indexOf(Locale locale);

    boolean isEmpty();

    int size();

    String toLanguageTags();
}
