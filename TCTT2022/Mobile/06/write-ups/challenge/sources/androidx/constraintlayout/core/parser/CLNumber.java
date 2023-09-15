package androidx.constraintlayout.core.parser;
/* loaded from: classes.dex */
public class CLNumber extends CLElement {
    float value;

    public CLNumber(char[] cArr) {
        super(cArr);
        this.value = Float.NaN;
    }

    public CLNumber(float f) {
        super(null);
        this.value = f;
    }

    public static CLElement allocate(char[] cArr) {
        return new CLNumber(cArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.constraintlayout.core.parser.CLElement
    public String toJSON() {
        float f = getFloat();
        int i = (int) f;
        if (i == f) {
            return "" + i;
        }
        return "" + f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.constraintlayout.core.parser.CLElement
    public String toFormattedJSON(int i, int i2) {
        StringBuilder sb = new StringBuilder();
        addIndent(sb, i);
        float f = getFloat();
        int i3 = (int) f;
        if (i3 == f) {
            sb.append(i3);
        } else {
            sb.append(f);
        }
        return sb.toString();
    }

    public boolean isInt() {
        float f = getFloat();
        return ((float) ((int) f)) == f;
    }

    @Override // androidx.constraintlayout.core.parser.CLElement
    public int getInt() {
        if (Float.isNaN(this.value)) {
            this.value = Integer.parseInt(content());
        }
        return (int) this.value;
    }

    @Override // androidx.constraintlayout.core.parser.CLElement
    public float getFloat() {
        if (Float.isNaN(this.value)) {
            this.value = Float.parseFloat(content());
        }
        return this.value;
    }

    public void putValue(float f) {
        this.value = f;
    }
}
