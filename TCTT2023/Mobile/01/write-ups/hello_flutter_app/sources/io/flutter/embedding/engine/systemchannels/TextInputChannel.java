package io.flutter.embedding.engine.systemchannels;

import android.os.Bundle;
import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.editing.TextEditingDelta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class TextInputChannel {
    private static final String TAG = "TextInputChannel";
    public final MethodChannel channel;
    final MethodChannel.MethodCallHandler parsingMethodHandler;
    private TextInputMethodHandler textInputMethodHandler;

    /* loaded from: classes.dex */
    public interface TextInputMethodHandler {
        void clearClient();

        void finishAutofillContext(boolean z);

        void hide();

        void requestAutofill();

        void sendAppPrivateCommand(String str, Bundle bundle);

        void setClient(int i, Configuration configuration);

        void setEditableSizeAndTransform(double d, double d2, double[] dArr);

        void setEditingState(TextEditState textEditState);

        void setPlatformViewClient(int i, boolean z);

        void show();
    }

    public TextInputChannel(DartExecutor dartExecutor) {
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.TextInputChannel.1
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (TextInputChannel.this.textInputMethodHandler == null) {
                    return;
                }
                String method = call.method;
                Object args = call.arguments;
                Log.v(TextInputChannel.TAG, "Received '" + method + "' message.");
                char c = 65535;
                switch (method.hashCode()) {
                    case -1779068172:
                        if (method.equals("TextInput.setPlatformViewClient")) {
                            c = 4;
                            break;
                        }
                        break;
                    case -1015421462:
                        if (method.equals("TextInput.setEditingState")) {
                            c = 5;
                            break;
                        }
                        break;
                    case -37561188:
                        if (method.equals("TextInput.setClient")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 270476819:
                        if (method.equals("TextInput.hide")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 270803918:
                        if (method.equals("TextInput.show")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 649192816:
                        if (method.equals("TextInput.sendAppPrivateCommand")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case 1204752139:
                        if (method.equals("TextInput.setEditableSizeAndTransform")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 1727570905:
                        if (method.equals("TextInput.finishAutofillContext")) {
                            c = '\t';
                            break;
                        }
                        break;
                    case 1904427655:
                        if (method.equals("TextInput.clearClient")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 2113369584:
                        if (method.equals("TextInput.requestAutofill")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        TextInputChannel.this.textInputMethodHandler.show();
                        result.success(null);
                        return;
                    case 1:
                        TextInputChannel.this.textInputMethodHandler.hide();
                        result.success(null);
                        return;
                    case 2:
                        try {
                            JSONArray argumentList = (JSONArray) args;
                            int textInputClientId = argumentList.getInt(0);
                            JSONObject jsonConfiguration = argumentList.getJSONObject(1);
                            TextInputChannel.this.textInputMethodHandler.setClient(textInputClientId, Configuration.fromJson(jsonConfiguration));
                            result.success(null);
                            return;
                        } catch (NoSuchFieldException | JSONException exception) {
                            result.error("error", exception.getMessage(), null);
                            return;
                        }
                    case 3:
                        TextInputChannel.this.textInputMethodHandler.requestAutofill();
                        result.success(null);
                        return;
                    case 4:
                        try {
                            JSONObject arguments = (JSONObject) args;
                            int platformViewId = arguments.getInt("platformViewId");
                            boolean usesVirtualDisplay = arguments.optBoolean("usesVirtualDisplay", false);
                            TextInputChannel.this.textInputMethodHandler.setPlatformViewClient(platformViewId, usesVirtualDisplay);
                            result.success(null);
                            return;
                        } catch (JSONException exception2) {
                            result.error("error", exception2.getMessage(), null);
                            return;
                        }
                    case 5:
                        try {
                            JSONObject editingState = (JSONObject) args;
                            TextInputChannel.this.textInputMethodHandler.setEditingState(TextEditState.fromJson(editingState));
                            result.success(null);
                            return;
                        } catch (JSONException exception3) {
                            result.error("error", exception3.getMessage(), null);
                            return;
                        }
                    case 6:
                        try {
                            JSONObject arguments2 = (JSONObject) args;
                            double width = arguments2.getDouble("width");
                            double height = arguments2.getDouble("height");
                            JSONArray jsonMatrix = arguments2.getJSONArray("transform");
                            double[] matrix = new double[16];
                            for (int i = 0; i < 16; i++) {
                                matrix[i] = jsonMatrix.getDouble(i);
                            }
                            TextInputChannel.this.textInputMethodHandler.setEditableSizeAndTransform(width, height, matrix);
                            result.success(null);
                            return;
                        } catch (JSONException exception4) {
                            result.error("error", exception4.getMessage(), null);
                            return;
                        }
                    case 7:
                        TextInputChannel.this.textInputMethodHandler.clearClient();
                        result.success(null);
                        return;
                    case '\b':
                        try {
                            JSONObject arguments3 = (JSONObject) args;
                            String action = arguments3.getString("action");
                            String data = arguments3.getString("data");
                            Bundle bundle = null;
                            if (data != null && !data.isEmpty()) {
                                bundle = new Bundle();
                                bundle.putString("data", data);
                            }
                            TextInputChannel.this.textInputMethodHandler.sendAppPrivateCommand(action, bundle);
                            result.success(null);
                            return;
                        } catch (JSONException exception5) {
                            result.error("error", exception5.getMessage(), null);
                            return;
                        }
                    case '\t':
                        TextInputChannel.this.textInputMethodHandler.finishAutofillContext(((Boolean) args).booleanValue());
                        result.success(null);
                        return;
                    default:
                        result.notImplemented();
                        return;
                }
            }
        };
        this.parsingMethodHandler = methodCallHandler;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/textinput", JSONMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    public void requestExistingInputState() {
        this.channel.invokeMethod("TextInputClient.requestExistingInputState", null);
    }

    private static HashMap<Object, Object> createEditingStateJSON(String text, int selectionStart, int selectionEnd, int composingStart, int composingEnd) {
        HashMap<Object, Object> state = new HashMap<>();
        state.put("text", text);
        state.put("selectionBase", Integer.valueOf(selectionStart));
        state.put("selectionExtent", Integer.valueOf(selectionEnd));
        state.put("composingBase", Integer.valueOf(composingStart));
        state.put("composingExtent", Integer.valueOf(composingEnd));
        return state;
    }

    private static HashMap<Object, Object> createEditingDeltaJSON(ArrayList<TextEditingDelta> batchDeltas) {
        HashMap<Object, Object> state = new HashMap<>();
        JSONArray deltas = new JSONArray();
        Iterator<TextEditingDelta> it = batchDeltas.iterator();
        while (it.hasNext()) {
            TextEditingDelta delta = it.next();
            deltas.put(delta.toJSON());
        }
        state.put("deltas", deltas);
        return state;
    }

    public void updateEditingState(int inputClientId, String text, int selectionStart, int selectionEnd, int composingStart, int composingEnd) {
        Log.v(TAG, "Sending message to update editing state: \nText: " + text + "\nSelection start: " + selectionStart + "\nSelection end: " + selectionEnd + "\nComposing start: " + composingStart + "\nComposing end: " + composingEnd);
        HashMap<Object, Object> state = createEditingStateJSON(text, selectionStart, selectionEnd, composingStart, composingEnd);
        this.channel.invokeMethod("TextInputClient.updateEditingState", Arrays.asList(Integer.valueOf(inputClientId), state));
    }

    public void updateEditingStateWithDeltas(int inputClientId, ArrayList<TextEditingDelta> batchDeltas) {
        Log.v(TAG, "Sending message to update editing state with deltas: \nNumber of deltas: " + batchDeltas.size());
        HashMap<Object, Object> state = createEditingDeltaJSON(batchDeltas);
        this.channel.invokeMethod("TextInputClient.updateEditingStateWithDeltas", Arrays.asList(Integer.valueOf(inputClientId), state));
    }

    public void updateEditingStateWithTag(int inputClientId, HashMap<String, TextEditState> editStates) {
        Log.v(TAG, "Sending message to update editing state for " + String.valueOf(editStates.size()) + " field(s).");
        HashMap<String, HashMap<Object, Object>> json = new HashMap<>();
        for (Map.Entry<String, TextEditState> element : editStates.entrySet()) {
            TextEditState state = element.getValue();
            json.put(element.getKey(), createEditingStateJSON(state.text, state.selectionStart, state.selectionEnd, -1, -1));
        }
        this.channel.invokeMethod("TextInputClient.updateEditingStateWithTag", Arrays.asList(Integer.valueOf(inputClientId), json));
    }

    public void newline(int inputClientId) {
        Log.v(TAG, "Sending 'newline' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.newline"));
    }

    public void go(int inputClientId) {
        Log.v(TAG, "Sending 'go' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.go"));
    }

    public void search(int inputClientId) {
        Log.v(TAG, "Sending 'search' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.search"));
    }

    public void send(int inputClientId) {
        Log.v(TAG, "Sending 'send' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.send"));
    }

    public void done(int inputClientId) {
        Log.v(TAG, "Sending 'done' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.done"));
    }

    public void next(int inputClientId) {
        Log.v(TAG, "Sending 'next' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.next"));
    }

    public void previous(int inputClientId) {
        Log.v(TAG, "Sending 'previous' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.previous"));
    }

    public void unspecifiedAction(int inputClientId) {
        Log.v(TAG, "Sending 'unspecified' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.unspecified"));
    }

    public void commitContent(int inputClientId, Map<String, Object> content) {
        Log.v(TAG, "Sending 'commitContent' message.");
        this.channel.invokeMethod("TextInputClient.performAction", Arrays.asList(Integer.valueOf(inputClientId), "TextInputAction.commitContent", content));
    }

    public void performPrivateCommand(int inputClientId, String action, Bundle data) {
        HashMap<Object, Object> json = new HashMap<>();
        json.put("action", action);
        if (data != null) {
            HashMap<String, Object> dataMap = new HashMap<>();
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object value = data.get(key);
                if (value instanceof byte[]) {
                    dataMap.put(key, data.getByteArray(key));
                } else if (value instanceof Byte) {
                    dataMap.put(key, Byte.valueOf(data.getByte(key)));
                } else if (value instanceof char[]) {
                    dataMap.put(key, data.getCharArray(key));
                } else if (value instanceof Character) {
                    dataMap.put(key, Character.valueOf(data.getChar(key)));
                } else if (value instanceof CharSequence[]) {
                    dataMap.put(key, data.getCharSequenceArray(key));
                } else if (value instanceof CharSequence) {
                    dataMap.put(key, data.getCharSequence(key));
                } else if (value instanceof float[]) {
                    dataMap.put(key, data.getFloatArray(key));
                } else if (value instanceof Float) {
                    dataMap.put(key, Float.valueOf(data.getFloat(key)));
                }
            }
            json.put("data", dataMap);
        }
        this.channel.invokeMethod("TextInputClient.performPrivateCommand", Arrays.asList(Integer.valueOf(inputClientId), json));
    }

    public void setTextInputMethodHandler(TextInputMethodHandler textInputMethodHandler) {
        this.textInputMethodHandler = textInputMethodHandler;
    }

    /* loaded from: classes.dex */
    public static class Configuration {
        public final String actionLabel;
        public final boolean autocorrect;
        public final Autofill autofill;
        public final String[] contentCommitMimeTypes;
        public final boolean enableDeltaModel;
        public final boolean enableIMEPersonalizedLearning;
        public final boolean enableSuggestions;
        public final Configuration[] fields;
        public final Integer inputAction;
        public final InputType inputType;
        public final boolean obscureText;
        public final TextCapitalization textCapitalization;

        public static Configuration fromJson(JSONObject json) throws JSONException, NoSuchFieldException {
            JSONArray jSONArray;
            String inputActionName = json.getString("inputAction");
            if (inputActionName == null) {
                throw new JSONException("Configuration JSON missing 'inputAction' property.");
            }
            Configuration[] fields = null;
            if (!json.isNull("fields")) {
                JSONArray jsonFields = json.getJSONArray("fields");
                fields = new Configuration[jsonFields.length()];
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fromJson(jsonFields.getJSONObject(i));
                }
            }
            Integer inputAction = inputActionFromTextInputAction(inputActionName);
            List<String> contentList = new ArrayList<>();
            if (json.isNull("contentCommitMimeTypes")) {
                jSONArray = null;
            } else {
                jSONArray = json.getJSONArray("contentCommitMimeTypes");
            }
            JSONArray contentCommitMimeTypes = jSONArray;
            if (contentCommitMimeTypes != null) {
                for (int i2 = 0; i2 < contentCommitMimeTypes.length(); i2++) {
                    contentList.add(contentCommitMimeTypes.optString(i2));
                }
            }
            return new Configuration(json.optBoolean("obscureText"), json.optBoolean("autocorrect", true), json.optBoolean("enableSuggestions"), json.optBoolean("enableIMEPersonalizedLearning"), json.optBoolean("enableDeltaModel"), TextCapitalization.fromValue(json.getString("textCapitalization")), InputType.fromJson(json.getJSONObject("inputType")), inputAction, json.isNull("actionLabel") ? null : json.getString("actionLabel"), json.isNull("autofill") ? null : Autofill.fromJson(json.getJSONObject("autofill")), (String[]) contentList.toArray(new String[contentList.size()]), fields);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:9:0x0029, code lost:
            if (r11.equals("TextInputAction.newline") != false) goto L6;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static java.lang.Integer inputActionFromTextInputAction(java.lang.String r11) {
            /*
                int r0 = r11.hashCode()
                r1 = 7
                r2 = 5
                r3 = 4
                r4 = 3
                r5 = 2
                r6 = 6
                r7 = 0
                java.lang.Integer r8 = java.lang.Integer.valueOf(r7)
                r9 = 1
                java.lang.Integer r10 = java.lang.Integer.valueOf(r9)
                switch(r0) {
                    case -810971940: goto L68;
                    case -737377923: goto L5e;
                    case -737089298: goto L54;
                    case -737080013: goto L4a;
                    case -736940669: goto L40;
                    case 469250275: goto L36;
                    case 1241689507: goto L2c;
                    case 1539450297: goto L23;
                    case 2110497650: goto L18;
                    default: goto L17;
                }
            L17:
                goto L72
            L18:
                java.lang.String r0 = "TextInputAction.previous"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 8
                goto L73
            L23:
                java.lang.String r0 = "TextInputAction.newline"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                goto L73
            L2c:
                java.lang.String r0 = "TextInputAction.go"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 4
                goto L73
            L36:
                java.lang.String r0 = "TextInputAction.search"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 5
                goto L73
            L40:
                java.lang.String r0 = "TextInputAction.send"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 6
                goto L73
            L4a:
                java.lang.String r0 = "TextInputAction.none"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 1
                goto L73
            L54:
                java.lang.String r0 = "TextInputAction.next"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 7
                goto L73
            L5e:
                java.lang.String r0 = "TextInputAction.done"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 3
                goto L73
            L68:
                java.lang.String r0 = "TextInputAction.unspecified"
                boolean r0 = r11.equals(r0)
                if (r0 == 0) goto L17
                r7 = 2
                goto L73
            L72:
                r7 = -1
            L73:
                switch(r7) {
                    case 0: goto L97;
                    case 1: goto L96;
                    case 2: goto L95;
                    case 3: goto L90;
                    case 4: goto L8b;
                    case 5: goto L86;
                    case 6: goto L81;
                    case 7: goto L7c;
                    case 8: goto L77;
                    default: goto L76;
                }
            L76:
                return r8
            L77:
                java.lang.Integer r0 = java.lang.Integer.valueOf(r1)
                return r0
            L7c:
                java.lang.Integer r0 = java.lang.Integer.valueOf(r2)
                return r0
            L81:
                java.lang.Integer r0 = java.lang.Integer.valueOf(r3)
                return r0
            L86:
                java.lang.Integer r0 = java.lang.Integer.valueOf(r4)
                return r0
            L8b:
                java.lang.Integer r0 = java.lang.Integer.valueOf(r5)
                return r0
            L90:
                java.lang.Integer r0 = java.lang.Integer.valueOf(r6)
                return r0
            L95:
                return r8
            L96:
                return r10
            L97:
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: io.flutter.embedding.engine.systemchannels.TextInputChannel.Configuration.inputActionFromTextInputAction(java.lang.String):java.lang.Integer");
        }

        /* loaded from: classes.dex */
        public static class Autofill {
            public final TextEditState editState;
            public final String hintText;
            public final String[] hints;
            public final String uniqueIdentifier;

            public static Autofill fromJson(JSONObject json) throws JSONException, NoSuchFieldException {
                String uniqueIdentifier = json.getString("uniqueIdentifier");
                JSONArray hints = json.getJSONArray("hints");
                String hintText = json.isNull("hintText") ? null : json.getString("hintText");
                JSONObject editingState = json.getJSONObject("editingValue");
                String[] autofillHints = new String[hints.length()];
                for (int i = 0; i < hints.length(); i++) {
                    autofillHints[i] = translateAutofillHint(hints.getString(i));
                }
                return new Autofill(uniqueIdentifier, autofillHints, hintText, TextEditState.fromJson(editingState));
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:24:0x0067, code lost:
                if (r16.equals("password") != false) goto L10;
             */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            private static java.lang.String translateAutofillHint(java.lang.String r16) {
                /*
                    Method dump skipped, instructions count: 740
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: io.flutter.embedding.engine.systemchannels.TextInputChannel.Configuration.Autofill.translateAutofillHint(java.lang.String):java.lang.String");
            }

            public Autofill(String uniqueIdentifier, String[] hints, String hintText, TextEditState editingState) {
                this.uniqueIdentifier = uniqueIdentifier;
                this.hints = hints;
                this.hintText = hintText;
                this.editState = editingState;
            }
        }

        public Configuration(boolean obscureText, boolean autocorrect, boolean enableSuggestions, boolean enableIMEPersonalizedLearning, boolean enableDeltaModel, TextCapitalization textCapitalization, InputType inputType, Integer inputAction, String actionLabel, Autofill autofill, String[] contentCommitMimeTypes, Configuration[] fields) {
            this.obscureText = obscureText;
            this.autocorrect = autocorrect;
            this.enableSuggestions = enableSuggestions;
            this.enableIMEPersonalizedLearning = enableIMEPersonalizedLearning;
            this.enableDeltaModel = enableDeltaModel;
            this.textCapitalization = textCapitalization;
            this.inputType = inputType;
            this.inputAction = inputAction;
            this.actionLabel = actionLabel;
            this.autofill = autofill;
            this.contentCommitMimeTypes = contentCommitMimeTypes;
            this.fields = fields;
        }
    }

    /* loaded from: classes.dex */
    public static class InputType {
        public final boolean isDecimal;
        public final boolean isSigned;
        public final TextInputType type;

        public static InputType fromJson(JSONObject json) throws JSONException, NoSuchFieldException {
            return new InputType(TextInputType.fromValue(json.getString("name")), json.optBoolean("signed", false), json.optBoolean("decimal", false));
        }

        public InputType(TextInputType type, boolean isSigned, boolean isDecimal) {
            this.type = type;
            this.isSigned = isSigned;
            this.isDecimal = isDecimal;
        }
    }

    /* loaded from: classes.dex */
    public enum TextInputType {
        TEXT("TextInputType.text"),
        DATETIME("TextInputType.datetime"),
        NAME("TextInputType.name"),
        POSTAL_ADDRESS("TextInputType.address"),
        NUMBER("TextInputType.number"),
        PHONE("TextInputType.phone"),
        MULTILINE("TextInputType.multiline"),
        EMAIL_ADDRESS("TextInputType.emailAddress"),
        URL("TextInputType.url"),
        VISIBLE_PASSWORD("TextInputType.visiblePassword"),
        NONE("TextInputType.none");
        
        private final String encodedName;

        static TextInputType fromValue(String encodedName) throws NoSuchFieldException {
            TextInputType[] values;
            for (TextInputType textInputType : values()) {
                if (textInputType.encodedName.equals(encodedName)) {
                    return textInputType;
                }
            }
            throw new NoSuchFieldException("No such TextInputType: " + encodedName);
        }

        TextInputType(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public enum TextCapitalization {
        CHARACTERS("TextCapitalization.characters"),
        WORDS("TextCapitalization.words"),
        SENTENCES("TextCapitalization.sentences"),
        NONE("TextCapitalization.none");
        
        private final String encodedName;

        static TextCapitalization fromValue(String encodedName) throws NoSuchFieldException {
            TextCapitalization[] values;
            for (TextCapitalization textCapitalization : values()) {
                if (textCapitalization.encodedName.equals(encodedName)) {
                    return textCapitalization;
                }
            }
            throw new NoSuchFieldException("No such TextCapitalization: " + encodedName);
        }

        TextCapitalization(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public static class TextEditState {
        public final int composingEnd;
        public final int composingStart;
        public final int selectionEnd;
        public final int selectionStart;
        public final String text;

        public static TextEditState fromJson(JSONObject textEditState) throws JSONException {
            return new TextEditState(textEditState.getString("text"), textEditState.getInt("selectionBase"), textEditState.getInt("selectionExtent"), textEditState.getInt("composingBase"), textEditState.getInt("composingExtent"));
        }

        public TextEditState(String text, int selectionStart, int selectionEnd, int composingStart, int composingEnd) throws IndexOutOfBoundsException {
            if ((selectionStart != -1 || selectionEnd != -1) && (selectionStart < 0 || selectionEnd < 0)) {
                throw new IndexOutOfBoundsException("invalid selection: (" + String.valueOf(selectionStart) + ", " + String.valueOf(selectionEnd) + ")");
            }
            if ((composingStart != -1 || composingEnd != -1) && (composingStart < 0 || composingStart > composingEnd)) {
                throw new IndexOutOfBoundsException("invalid composing range: (" + String.valueOf(composingStart) + ", " + String.valueOf(composingEnd) + ")");
            }
            if (composingEnd > text.length()) {
                throw new IndexOutOfBoundsException("invalid composing start: " + String.valueOf(composingStart));
            }
            if (selectionStart > text.length()) {
                throw new IndexOutOfBoundsException("invalid selection start: " + String.valueOf(selectionStart));
            }
            if (selectionEnd > text.length()) {
                throw new IndexOutOfBoundsException("invalid selection end: " + String.valueOf(selectionEnd));
            }
            this.text = text;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
            this.composingStart = composingStart;
            this.composingEnd = composingEnd;
        }

        public boolean hasSelection() {
            return this.selectionStart >= 0;
        }

        public boolean hasComposing() {
            int i = this.composingStart;
            return i >= 0 && this.composingEnd > i;
        }
    }
}
