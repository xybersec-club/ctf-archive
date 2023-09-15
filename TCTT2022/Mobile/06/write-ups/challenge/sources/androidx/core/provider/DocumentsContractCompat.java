package androidx.core.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import java.io.FileNotFoundException;
import java.util.List;
/* loaded from: classes.dex */
public final class DocumentsContractCompat {
    private static final String PATH_TREE = "tree";

    /* loaded from: classes.dex */
    public static final class DocumentCompat {
        public static final int FLAG_VIRTUAL_DOCUMENT = 512;

        private DocumentCompat() {
        }
    }

    public static boolean isDocumentUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= 19) {
            return DocumentsContractApi19Impl.isDocumentUri(context, uri);
        }
        return false;
    }

    public static boolean isTreeUri(Uri uri) {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 24) {
            List<String> pathSegments = uri.getPathSegments();
            return pathSegments.size() >= 2 && PATH_TREE.equals(pathSegments.get(0));
        }
        return DocumentsContractApi24Impl.isTreeUri(uri);
    }

    public static String getDocumentId(Uri uri) {
        if (Build.VERSION.SDK_INT >= 19) {
            return DocumentsContractApi19Impl.getDocumentId(uri);
        }
        return null;
    }

    public static String getTreeDocumentId(Uri uri) {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContractApi21Impl.getTreeDocumentId(uri);
        }
        return null;
    }

    public static Uri buildDocumentUri(String str, String str2) {
        if (Build.VERSION.SDK_INT >= 19) {
            return DocumentsContractApi19Impl.buildDocumentUri(str, str2);
        }
        return null;
    }

    public static Uri buildDocumentUriUsingTree(Uri uri, String str) {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContractApi21Impl.buildDocumentUriUsingTree(uri, str);
        }
        return null;
    }

    public static Uri buildTreeDocumentUri(String str, String str2) {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContractApi21Impl.buildTreeDocumentUri(str, str2);
        }
        return null;
    }

    public static Uri buildChildDocumentsUri(String str, String str2) {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContractApi21Impl.buildChildDocumentsUri(str, str2);
        }
        return null;
    }

    public static Uri buildChildDocumentsUriUsingTree(Uri uri, String str) {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContractApi21Impl.buildChildDocumentsUriUsingTree(uri, str);
        }
        return null;
    }

    public static Uri createDocument(ContentResolver contentResolver, Uri uri, String str, String str2) throws FileNotFoundException {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContractApi21Impl.createDocument(contentResolver, uri, str, str2);
        }
        return null;
    }

    public static Uri renameDocument(ContentResolver contentResolver, Uri uri, String str) throws FileNotFoundException {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContractApi21Impl.renameDocument(contentResolver, uri, str);
        }
        return null;
    }

    public static boolean removeDocument(ContentResolver contentResolver, Uri uri, Uri uri2) throws FileNotFoundException {
        if (Build.VERSION.SDK_INT >= 24) {
            return DocumentsContractApi24Impl.removeDocument(contentResolver, uri, uri2);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return DocumentsContractApi19Impl.deleteDocument(contentResolver, uri);
        }
        return false;
    }

    /* loaded from: classes.dex */
    private static class DocumentsContractApi19Impl {
        public static Uri buildDocumentUri(String str, String str2) {
            return DocumentsContract.buildDocumentUri(str, str2);
        }

        static boolean isDocumentUri(Context context, Uri uri) {
            return DocumentsContract.isDocumentUri(context, uri);
        }

        static String getDocumentId(Uri uri) {
            return DocumentsContract.getDocumentId(uri);
        }

        static boolean deleteDocument(ContentResolver contentResolver, Uri uri) throws FileNotFoundException {
            return DocumentsContract.deleteDocument(contentResolver, uri);
        }

        private DocumentsContractApi19Impl() {
        }
    }

    /* loaded from: classes.dex */
    private static class DocumentsContractApi21Impl {
        static String getTreeDocumentId(Uri uri) {
            return DocumentsContract.getTreeDocumentId(uri);
        }

        public static Uri buildTreeDocumentUri(String str, String str2) {
            return DocumentsContract.buildTreeDocumentUri(str, str2);
        }

        static Uri buildDocumentUriUsingTree(Uri uri, String str) {
            return DocumentsContract.buildDocumentUriUsingTree(uri, str);
        }

        static Uri buildChildDocumentsUri(String str, String str2) {
            return DocumentsContract.buildChildDocumentsUri(str, str2);
        }

        static Uri buildChildDocumentsUriUsingTree(Uri uri, String str) {
            return DocumentsContract.buildChildDocumentsUriUsingTree(uri, str);
        }

        static Uri createDocument(ContentResolver contentResolver, Uri uri, String str, String str2) throws FileNotFoundException {
            return DocumentsContract.createDocument(contentResolver, uri, str, str2);
        }

        static Uri renameDocument(ContentResolver contentResolver, Uri uri, String str) throws FileNotFoundException {
            return DocumentsContract.renameDocument(contentResolver, uri, str);
        }

        private DocumentsContractApi21Impl() {
        }
    }

    /* loaded from: classes.dex */
    private static class DocumentsContractApi24Impl {
        static boolean isTreeUri(Uri uri) {
            return DocumentsContract.isTreeUri(uri);
        }

        static boolean removeDocument(ContentResolver contentResolver, Uri uri, Uri uri2) throws FileNotFoundException {
            return DocumentsContract.removeDocument(contentResolver, uri, uri2);
        }

        private DocumentsContractApi24Impl() {
        }
    }

    private DocumentsContractCompat() {
    }
}
