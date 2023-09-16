package io.flutter.embedding.engine.plugins.service;
/* loaded from: classes.dex */
public interface ServiceAware {

    /* loaded from: classes.dex */
    public interface OnModeChangeListener {
        void onMoveToBackground();

        void onMoveToForeground();
    }

    void onAttachedToService(ServicePluginBinding servicePluginBinding);

    void onDetachedFromService();
}
