package io.flutter.embedding.engine.plugins.broadcastreceiver;
/* loaded from: classes.dex */
public interface BroadcastReceiverAware {
    void onAttachedToBroadcastReceiver(BroadcastReceiverPluginBinding broadcastReceiverPluginBinding);

    void onDetachedFromBroadcastReceiver();
}
