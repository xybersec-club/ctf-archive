package io.flutter.embedding.engine.systemchannels;

import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.StringCodec;
/* loaded from: classes.dex */
public class LifecycleChannel {
    private static final String TAG = "LifecycleChannel";
    public final BasicMessageChannel<String> channel;

    public LifecycleChannel(DartExecutor dartExecutor) {
        this.channel = new BasicMessageChannel<>(dartExecutor, "flutter/lifecycle", StringCodec.INSTANCE);
    }

    public void appIsInactive() {
        Log.v(TAG, "Sending AppLifecycleState.inactive message.");
        this.channel.send("AppLifecycleState.inactive");
    }

    public void appIsResumed() {
        Log.v(TAG, "Sending AppLifecycleState.resumed message.");
        this.channel.send("AppLifecycleState.resumed");
    }

    public void appIsPaused() {
        Log.v(TAG, "Sending AppLifecycleState.paused message.");
        this.channel.send("AppLifecycleState.paused");
    }

    public void appIsDetached() {
        Log.v(TAG, "Sending AppLifecycleState.detached message.");
        this.channel.send("AppLifecycleState.detached");
    }
}
