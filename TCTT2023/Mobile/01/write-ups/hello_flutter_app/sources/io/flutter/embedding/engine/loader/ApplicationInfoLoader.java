package io.flutter.embedding.engine.loader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import java.io.IOException;
import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class ApplicationInfoLoader {
    public static final String NETWORK_POLICY_METADATA_KEY = "io.flutter.network-policy";
    public static final String PUBLIC_AUTOMATICALLY_REGISTER_PLUGINS_METADATA_KEY = "io.flutter.automatically-register-plugins";
    public static final String PUBLIC_AOT_SHARED_LIBRARY_NAME = FlutterLoader.class.getName() + ".aot-shared-library-name";
    public static final String PUBLIC_VM_SNAPSHOT_DATA_KEY = FlutterLoader.class.getName() + ".vm-snapshot-data";
    public static final String PUBLIC_ISOLATE_SNAPSHOT_DATA_KEY = FlutterLoader.class.getName() + ".isolate-snapshot-data";
    public static final String PUBLIC_FLUTTER_ASSETS_DIR_KEY = FlutterLoader.class.getName() + ".flutter-assets-dir";

    private static ApplicationInfo getApplicationInfo(Context applicationContext) {
        try {
            return applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), 128);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getString(Bundle metadata, String key) {
        if (metadata == null) {
            return null;
        }
        return metadata.getString(key, null);
    }

    private static boolean getBoolean(Bundle metadata, String key, boolean defaultValue) {
        if (metadata == null) {
            return defaultValue;
        }
        return metadata.getBoolean(key, defaultValue);
    }

    private static String getNetworkPolicy(ApplicationInfo appInfo, Context context) {
        int networkSecurityConfigRes;
        Bundle metadata = appInfo.metaData;
        if (metadata == null || (networkSecurityConfigRes = metadata.getInt(NETWORK_POLICY_METADATA_KEY, 0)) <= 0) {
            return null;
        }
        JSONArray output = new JSONArray();
        try {
            XmlResourceParser xrp = context.getResources().getXml(networkSecurityConfigRes);
            xrp.next();
            for (int eventType = xrp.getEventType(); eventType != 1; eventType = xrp.next()) {
                if (eventType == 2) {
                    if (xrp.getName().equals("domain-config")) {
                        parseDomainConfig(xrp, output, false);
                    }
                }
            }
            return output.toString();
        } catch (IOException | XmlPullParserException e) {
            return null;
        }
    }

    private static void parseDomainConfig(XmlResourceParser xrp, JSONArray output, boolean inheritedCleartextPermitted) throws IOException, XmlPullParserException {
        boolean cleartextTrafficPermitted = xrp.getAttributeBooleanValue(null, "cleartextTrafficPermitted", inheritedCleartextPermitted);
        while (true) {
            int eventType = xrp.next();
            if (eventType == 2) {
                if (xrp.getName().equals("domain")) {
                    parseDomain(xrp, output, cleartextTrafficPermitted);
                } else if (xrp.getName().equals("domain-config")) {
                    parseDomainConfig(xrp, output, cleartextTrafficPermitted);
                } else {
                    skipTag(xrp);
                }
            } else if (eventType == 3) {
                return;
            }
        }
    }

    private static void skipTag(XmlResourceParser xrp) throws IOException, XmlPullParserException {
        String name = xrp.getName();
        int eventType = xrp.getEventType();
        while (true) {
            if (eventType != 3 || xrp.getName() != name) {
                eventType = xrp.next();
            } else {
                return;
            }
        }
    }

    private static void parseDomain(XmlResourceParser xrp, JSONArray output, boolean cleartextPermitted) throws IOException, XmlPullParserException {
        boolean includeSubDomains = xrp.getAttributeBooleanValue(null, "includeSubdomains", false);
        xrp.next();
        if (xrp.getEventType() != 4) {
            throw new IllegalStateException("Expected text");
        }
        String domain = xrp.getText().trim();
        JSONArray outputArray = new JSONArray();
        outputArray.put(domain);
        outputArray.put(includeSubDomains);
        outputArray.put(cleartextPermitted);
        output.put(outputArray);
        xrp.next();
        if (xrp.getEventType() != 3) {
            throw new IllegalStateException("Expected end of domain tag");
        }
    }

    public static FlutterApplicationInfo load(Context applicationContext) {
        ApplicationInfo appInfo = getApplicationInfo(applicationContext);
        return new FlutterApplicationInfo(getString(appInfo.metaData, PUBLIC_AOT_SHARED_LIBRARY_NAME), getString(appInfo.metaData, PUBLIC_VM_SNAPSHOT_DATA_KEY), getString(appInfo.metaData, PUBLIC_ISOLATE_SNAPSHOT_DATA_KEY), getString(appInfo.metaData, PUBLIC_FLUTTER_ASSETS_DIR_KEY), getNetworkPolicy(appInfo, applicationContext), appInfo.nativeLibraryDir, getBoolean(appInfo.metaData, PUBLIC_AUTOMATICALLY_REGISTER_PLUGINS_METADATA_KEY, true));
    }
}
