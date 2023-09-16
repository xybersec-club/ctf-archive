package io.flutter.embedding.engine.systemchannels;

import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.JSONMessageCodec;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class SettingsChannel {
    private static final String ALWAYS_USE_24_HOUR_FORMAT = "alwaysUse24HourFormat";
    private static final String BRIEFLY_SHOW_PASSWORD = "brieflyShowPassword";
    public static final String CHANNEL_NAME = "flutter/settings";
    private static final String NATIVE_SPELL_CHECK_SERVICE_DEFINED = "nativeSpellCheckServiceDefined";
    private static final String PLATFORM_BRIGHTNESS = "platformBrightness";
    private static final String TAG = "SettingsChannel";
    private static final String TEXT_SCALE_FACTOR = "textScaleFactor";
    public final BasicMessageChannel<Object> channel;

    public SettingsChannel(DartExecutor dartExecutor) {
        this.channel = new BasicMessageChannel<>(dartExecutor, CHANNEL_NAME, JSONMessageCodec.INSTANCE);
    }

    public MessageBuilder startMessage() {
        return new MessageBuilder(this.channel);
    }

    /* loaded from: classes.dex */
    public static class MessageBuilder {
        private final BasicMessageChannel<Object> channel;
        private Map<String, Object> message = new HashMap();

        MessageBuilder(BasicMessageChannel<Object> channel) {
            this.channel = channel;
        }

        public MessageBuilder setTextScaleFactor(float textScaleFactor) {
            this.message.put(SettingsChannel.TEXT_SCALE_FACTOR, Float.valueOf(textScaleFactor));
            return this;
        }

        public MessageBuilder setNativeSpellCheckServiceDefined(boolean nativeSpellCheckServiceDefined) {
            this.message.put(SettingsChannel.NATIVE_SPELL_CHECK_SERVICE_DEFINED, Boolean.valueOf(nativeSpellCheckServiceDefined));
            return this;
        }

        public MessageBuilder setBrieflyShowPassword(boolean brieflyShowPassword) {
            this.message.put(SettingsChannel.BRIEFLY_SHOW_PASSWORD, Boolean.valueOf(brieflyShowPassword));
            return this;
        }

        public MessageBuilder setUse24HourFormat(boolean use24HourFormat) {
            this.message.put(SettingsChannel.ALWAYS_USE_24_HOUR_FORMAT, Boolean.valueOf(use24HourFormat));
            return this;
        }

        public MessageBuilder setPlatformBrightness(PlatformBrightness brightness) {
            this.message.put(SettingsChannel.PLATFORM_BRIGHTNESS, brightness.name);
            return this;
        }

        public void send() {
            Log.v(SettingsChannel.TAG, "Sending message: \ntextScaleFactor: " + this.message.get(SettingsChannel.TEXT_SCALE_FACTOR) + "\nalwaysUse24HourFormat: " + this.message.get(SettingsChannel.ALWAYS_USE_24_HOUR_FORMAT) + "\nplatformBrightness: " + this.message.get(SettingsChannel.PLATFORM_BRIGHTNESS));
            this.channel.send(this.message);
        }
    }

    /* loaded from: classes.dex */
    public enum PlatformBrightness {
        light("light"),
        dark("dark");
        
        public String name;

        PlatformBrightness(String name) {
            this.name = name;
        }
    }
}
