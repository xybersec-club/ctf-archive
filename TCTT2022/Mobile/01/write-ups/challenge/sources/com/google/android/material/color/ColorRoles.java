package com.google.android.material.color;
/* loaded from: classes.dex */
public final class ColorRoles {
    private final int accent;
    private final int accentContainer;
    private final int onAccent;
    private final int onAccentContainer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ColorRoles(int i, int i2, int i3, int i4) {
        this.accent = i;
        this.onAccent = i2;
        this.accentContainer = i3;
        this.onAccentContainer = i4;
    }

    public int getAccent() {
        return this.accent;
    }

    public int getOnAccent() {
        return this.onAccent;
    }

    public int getAccentContainer() {
        return this.accentContainer;
    }

    public int getOnAccentContainer() {
        return this.onAccentContainer;
    }
}
