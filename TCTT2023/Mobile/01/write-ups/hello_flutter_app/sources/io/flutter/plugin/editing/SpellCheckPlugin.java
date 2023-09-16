package io.flutter.plugin.editing;

import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import io.flutter.embedding.engine.systemchannels.SpellCheckChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.localization.LocalizationPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
/* loaded from: classes.dex */
public class SpellCheckPlugin implements SpellCheckChannel.SpellCheckMethodHandler, SpellCheckerSession.SpellCheckerSessionListener {
    public static final String END_INDEX_KEY = "endIndex";
    private static final int MAX_SPELL_CHECK_SUGGESTIONS = 5;
    public static final String START_INDEX_KEY = "startIndex";
    public static final String SUGGESTIONS_KEY = "suggestions";
    private final SpellCheckChannel mSpellCheckChannel;
    private SpellCheckerSession mSpellCheckerSession;
    private final TextServicesManager mTextServicesManager;
    MethodChannel.Result pendingResult;

    public SpellCheckPlugin(TextServicesManager textServicesManager, SpellCheckChannel spellCheckChannel) {
        this.mTextServicesManager = textServicesManager;
        this.mSpellCheckChannel = spellCheckChannel;
        spellCheckChannel.setSpellCheckMethodHandler(this);
    }

    public void destroy() {
        this.mSpellCheckChannel.setSpellCheckMethodHandler(null);
        SpellCheckerSession spellCheckerSession = this.mSpellCheckerSession;
        if (spellCheckerSession != null) {
            spellCheckerSession.close();
        }
    }

    @Override // io.flutter.embedding.engine.systemchannels.SpellCheckChannel.SpellCheckMethodHandler
    public void initiateSpellCheck(String locale, String text, MethodChannel.Result result) {
        if (this.pendingResult != null) {
            result.error("error", "Previous spell check request still pending.", null);
            return;
        }
        this.pendingResult = result;
        performSpellCheck(locale, text);
    }

    public void performSpellCheck(String locale, String text) {
        Locale localeFromString = LocalizationPlugin.localeFromString(locale);
        if (this.mSpellCheckerSession == null) {
            this.mSpellCheckerSession = this.mTextServicesManager.newSpellCheckerSession(null, localeFromString, this, true);
        }
        TextInfo[] textInfos = {new TextInfo(text)};
        this.mSpellCheckerSession.getSentenceSuggestions(textInfos, 5);
    }

    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        if (results.length == 0) {
            this.pendingResult.success(new ArrayList());
            this.pendingResult = null;
            return;
        }
        ArrayList<HashMap<String, Object>> spellCheckerSuggestionSpans = new ArrayList<>();
        SentenceSuggestionsInfo spellCheckResults = results[0];
        for (int i = 0; i < spellCheckResults.getSuggestionsCount(); i++) {
            SuggestionsInfo suggestionsInfo = spellCheckResults.getSuggestionsInfoAt(i);
            int suggestionsCount = suggestionsInfo.getSuggestionsCount();
            if (suggestionsCount > 0) {
                HashMap<String, Object> spellCheckerSuggestionSpan = new HashMap<>();
                int start = spellCheckResults.getOffsetAt(i);
                int end = spellCheckResults.getLengthAt(i) + start;
                spellCheckerSuggestionSpan.put(START_INDEX_KEY, Integer.valueOf(start));
                spellCheckerSuggestionSpan.put(END_INDEX_KEY, Integer.valueOf(end));
                ArrayList<String> suggestions = new ArrayList<>();
                boolean validSuggestionsFound = false;
                for (int j = 0; j < suggestionsCount; j++) {
                    String suggestion = suggestionsInfo.getSuggestionAt(j);
                    if (!suggestion.equals("")) {
                        validSuggestionsFound = true;
                        suggestions.add(suggestion);
                    }
                }
                if (validSuggestionsFound) {
                    spellCheckerSuggestionSpan.put(SUGGESTIONS_KEY, suggestions);
                    spellCheckerSuggestionSpans.add(spellCheckerSuggestionSpan);
                }
            }
        }
        this.pendingResult.success(spellCheckerSuggestionSpans);
        this.pendingResult = null;
    }

    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSuggestions(SuggestionsInfo[] results) {
    }
}
