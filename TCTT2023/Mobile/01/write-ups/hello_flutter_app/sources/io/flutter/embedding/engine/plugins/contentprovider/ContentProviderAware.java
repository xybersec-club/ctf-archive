package io.flutter.embedding.engine.plugins.contentprovider;
/* loaded from: classes.dex */
public interface ContentProviderAware {
    void onAttachedToContentProvider(ContentProviderPluginBinding contentProviderPluginBinding);

    void onDetachedFromContentProvider();
}
