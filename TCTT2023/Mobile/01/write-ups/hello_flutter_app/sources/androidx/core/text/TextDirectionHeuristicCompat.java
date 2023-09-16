package androidx.core.text;
/* loaded from: classes.dex */
public interface TextDirectionHeuristicCompat {
    boolean isRtl(CharSequence cs, int start, int count);

    boolean isRtl(char[] array, int start, int count);
}
