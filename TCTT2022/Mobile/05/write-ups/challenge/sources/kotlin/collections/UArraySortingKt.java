package kotlin.collections;

import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShort;
import kotlin.UShortArray;
import kotlin.UnsignedKt;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: UArraySorting.kt */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0010\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0006\u0010\u0007\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\t\u0010\n\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\f\u0010\r\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u000f\u0010\u0010\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0013\u0010\u0014\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0015\u0010\u0016\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0017\u0010\u0018\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0019\u0010\u001a\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001e\u0010\u0014\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001f\u0010\u0016\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b \u0010\u0018\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b!\u0010\u001a\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\""}, d2 = {"partition", "", "array", "Lkotlin/UByteArray;", "left", "right", "partition-4UcCI2c", "([BII)I", "Lkotlin/UIntArray;", "partition-oBK06Vg", "([III)I", "Lkotlin/ULongArray;", "partition--nroSd4", "([JII)I", "Lkotlin/UShortArray;", "partition-Aa5vz7o", "([SII)I", "quickSort", "", "quickSort-4UcCI2c", "([BII)V", "quickSort-oBK06Vg", "([III)V", "quickSort--nroSd4", "([JII)V", "quickSort-Aa5vz7o", "([SII)V", "sortArray", "fromIndex", "toIndex", "sortArray-4UcCI2c", "sortArray-oBK06Vg", "sortArray--nroSd4", "sortArray-Aa5vz7o", "kotlin-stdlib"}, k = 2, mv = {1, 7, 1}, xi = 48)
/* loaded from: classes.dex */
public final class UArraySortingKt {
    /* renamed from: partition-4UcCI2c  reason: not valid java name */
    private static final int m496partition4UcCI2c(byte[] bArr, int i, int i2) {
        int i3;
        byte m119getw2LRezQ = UByteArray.m119getw2LRezQ(bArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int m119getw2LRezQ2 = UByteArray.m119getw2LRezQ(bArr, i) & UByte.MAX_VALUE;
                i3 = m119getw2LRezQ & UByte.MAX_VALUE;
                if (Intrinsics.compare(m119getw2LRezQ2, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UByteArray.m119getw2LRezQ(bArr, i2) & UByte.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                byte m119getw2LRezQ3 = UByteArray.m119getw2LRezQ(bArr, i);
                UByteArray.m124setVurrAj0(bArr, i, UByteArray.m119getw2LRezQ(bArr, i2));
                UByteArray.m124setVurrAj0(bArr, i2, m119getw2LRezQ3);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-4UcCI2c  reason: not valid java name */
    private static final void m500quickSort4UcCI2c(byte[] bArr, int i, int i2) {
        int m496partition4UcCI2c = m496partition4UcCI2c(bArr, i, i2);
        int i3 = m496partition4UcCI2c - 1;
        if (i < i3) {
            m500quickSort4UcCI2c(bArr, i, i3);
        }
        if (m496partition4UcCI2c < i2) {
            m500quickSort4UcCI2c(bArr, m496partition4UcCI2c, i2);
        }
    }

    /* renamed from: partition-Aa5vz7o  reason: not valid java name */
    private static final int m497partitionAa5vz7o(short[] sArr, int i, int i2) {
        int i3;
        short m379getMh2AYeg = UShortArray.m379getMh2AYeg(sArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int m379getMh2AYeg2 = UShortArray.m379getMh2AYeg(sArr, i) & UShort.MAX_VALUE;
                i3 = m379getMh2AYeg & UShort.MAX_VALUE;
                if (Intrinsics.compare(m379getMh2AYeg2, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UShortArray.m379getMh2AYeg(sArr, i2) & UShort.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                short m379getMh2AYeg3 = UShortArray.m379getMh2AYeg(sArr, i);
                UShortArray.m384set01HTLdE(sArr, i, UShortArray.m379getMh2AYeg(sArr, i2));
                UShortArray.m384set01HTLdE(sArr, i2, m379getMh2AYeg3);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-Aa5vz7o  reason: not valid java name */
    private static final void m501quickSortAa5vz7o(short[] sArr, int i, int i2) {
        int m497partitionAa5vz7o = m497partitionAa5vz7o(sArr, i, i2);
        int i3 = m497partitionAa5vz7o - 1;
        if (i < i3) {
            m501quickSortAa5vz7o(sArr, i, i3);
        }
        if (m497partitionAa5vz7o < i2) {
            m501quickSortAa5vz7o(sArr, m497partitionAa5vz7o, i2);
        }
    }

    /* renamed from: partition-oBK06Vg  reason: not valid java name */
    private static final int m498partitionoBK06Vg(int[] iArr, int i, int i2) {
        int m197getpVg5ArA = UIntArray.m197getpVg5ArA(iArr, (i + i2) / 2);
        while (i <= i2) {
            while (UnsignedKt.uintCompare(UIntArray.m197getpVg5ArA(iArr, i), m197getpVg5ArA) < 0) {
                i++;
            }
            while (UnsignedKt.uintCompare(UIntArray.m197getpVg5ArA(iArr, i2), m197getpVg5ArA) > 0) {
                i2--;
            }
            if (i <= i2) {
                int m197getpVg5ArA2 = UIntArray.m197getpVg5ArA(iArr, i);
                UIntArray.m202setVXSXFK8(iArr, i, UIntArray.m197getpVg5ArA(iArr, i2));
                UIntArray.m202setVXSXFK8(iArr, i2, m197getpVg5ArA2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-oBK06Vg  reason: not valid java name */
    private static final void m502quickSortoBK06Vg(int[] iArr, int i, int i2) {
        int m498partitionoBK06Vg = m498partitionoBK06Vg(iArr, i, i2);
        int i3 = m498partitionoBK06Vg - 1;
        if (i < i3) {
            m502quickSortoBK06Vg(iArr, i, i3);
        }
        if (m498partitionoBK06Vg < i2) {
            m502quickSortoBK06Vg(iArr, m498partitionoBK06Vg, i2);
        }
    }

    /* renamed from: partition--nroSd4  reason: not valid java name */
    private static final int m495partitionnroSd4(long[] jArr, int i, int i2) {
        long m275getsVKNKU = ULongArray.m275getsVKNKU(jArr, (i + i2) / 2);
        while (i <= i2) {
            while (UnsignedKt.ulongCompare(ULongArray.m275getsVKNKU(jArr, i), m275getsVKNKU) < 0) {
                i++;
            }
            while (UnsignedKt.ulongCompare(ULongArray.m275getsVKNKU(jArr, i2), m275getsVKNKU) > 0) {
                i2--;
            }
            if (i <= i2) {
                long m275getsVKNKU2 = ULongArray.m275getsVKNKU(jArr, i);
                ULongArray.m280setk8EXiF4(jArr, i, ULongArray.m275getsVKNKU(jArr, i2));
                ULongArray.m280setk8EXiF4(jArr, i2, m275getsVKNKU2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort--nroSd4  reason: not valid java name */
    private static final void m499quickSortnroSd4(long[] jArr, int i, int i2) {
        int m495partitionnroSd4 = m495partitionnroSd4(jArr, i, i2);
        int i3 = m495partitionnroSd4 - 1;
        if (i < i3) {
            m499quickSortnroSd4(jArr, i, i3);
        }
        if (m495partitionnroSd4 < i2) {
            m499quickSortnroSd4(jArr, m495partitionnroSd4, i2);
        }
    }

    /* renamed from: sortArray-4UcCI2c  reason: not valid java name */
    public static final void m504sortArray4UcCI2c(byte[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m500quickSort4UcCI2c(array, i, i2 - 1);
    }

    /* renamed from: sortArray-Aa5vz7o  reason: not valid java name */
    public static final void m505sortArrayAa5vz7o(short[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m501quickSortAa5vz7o(array, i, i2 - 1);
    }

    /* renamed from: sortArray-oBK06Vg  reason: not valid java name */
    public static final void m506sortArrayoBK06Vg(int[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m502quickSortoBK06Vg(array, i, i2 - 1);
    }

    /* renamed from: sortArray--nroSd4  reason: not valid java name */
    public static final void m503sortArraynroSd4(long[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m499quickSortnroSd4(array, i, i2 - 1);
    }
}
