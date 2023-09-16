package io.flutter.plugin.editing;

import io.flutter.embedding.engine.FlutterJNI;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FlutterTextUtils {
    public static final int CANCEL_TAG = 917631;
    public static final int CARRIAGE_RETURN = 13;
    public static final int COMBINING_ENCLOSING_KEYCAP = 8419;
    public static final int LINE_FEED = 10;
    public static final int ZERO_WIDTH_JOINER = 8205;
    private final FlutterJNI flutterJNI;

    public FlutterTextUtils(FlutterJNI flutterJNI) {
        this.flutterJNI = flutterJNI;
    }

    public boolean isEmoji(int codePoint) {
        return this.flutterJNI.isCodePointEmoji(codePoint);
    }

    public boolean isEmojiModifier(int codePoint) {
        return this.flutterJNI.isCodePointEmojiModifier(codePoint);
    }

    public boolean isEmojiModifierBase(int codePoint) {
        return this.flutterJNI.isCodePointEmojiModifierBase(codePoint);
    }

    public boolean isVariationSelector(int codePoint) {
        return this.flutterJNI.isCodePointVariantSelector(codePoint);
    }

    public boolean isRegionalIndicatorSymbol(int codePoint) {
        return this.flutterJNI.isCodePointRegionalIndicator(codePoint);
    }

    public boolean isTagSpecChar(int codePoint) {
        return 917536 <= codePoint && codePoint <= 917630;
    }

    public boolean isKeycapBase(int codePoint) {
        return (48 <= codePoint && codePoint <= 57) || codePoint == 35 || codePoint == 42;
    }

    public int getOffsetBefore(CharSequence text, int offset) {
        int codePoint;
        int deleteCharCount;
        int lastOffset;
        int codePoint2;
        if (offset <= 1 || (lastOffset = offset - (deleteCharCount = Character.charCount((codePoint = Character.codePointBefore(text, offset))))) == 0) {
            return 0;
        }
        if (codePoint == 10) {
            if (Character.codePointBefore(text, lastOffset) == 13) {
                deleteCharCount++;
            }
            return offset - deleteCharCount;
        } else if (isRegionalIndicatorSymbol(codePoint)) {
            int codePoint3 = Character.codePointBefore(text, lastOffset);
            int lastOffset2 = lastOffset - Character.charCount(codePoint3);
            int regionalIndicatorSymbolCount = 1;
            while (lastOffset2 > 0 && isRegionalIndicatorSymbol(codePoint3)) {
                codePoint3 = Character.codePointBefore(text, lastOffset2);
                lastOffset2 -= Character.charCount(codePoint3);
                regionalIndicatorSymbolCount++;
            }
            if (regionalIndicatorSymbolCount % 2 == 0) {
                deleteCharCount += 2;
            }
            return offset - deleteCharCount;
        } else if (codePoint == 8419) {
            int codePoint4 = Character.codePointBefore(text, lastOffset);
            int lastOffset3 = lastOffset - Character.charCount(codePoint4);
            if (lastOffset3 > 0 && isVariationSelector(codePoint4)) {
                int tmpCodePoint = Character.codePointBefore(text, lastOffset3);
                if (isKeycapBase(tmpCodePoint)) {
                    deleteCharCount += Character.charCount(codePoint4) + Character.charCount(tmpCodePoint);
                }
            } else if (isKeycapBase(codePoint4)) {
                deleteCharCount += Character.charCount(codePoint4);
            }
            return offset - deleteCharCount;
        } else {
            if (codePoint == 917631) {
                int codePoint5 = Character.codePointBefore(text, lastOffset);
                lastOffset -= Character.charCount(codePoint5);
                codePoint = codePoint5;
                while (lastOffset > 0 && isTagSpecChar(codePoint)) {
                    deleteCharCount += Character.charCount(codePoint);
                    codePoint = Character.codePointBefore(text, lastOffset);
                    lastOffset -= Character.charCount(codePoint);
                }
                if (!isEmoji(codePoint)) {
                    return offset - 2;
                }
                deleteCharCount += Character.charCount(codePoint);
            }
            if (isVariationSelector(codePoint)) {
                codePoint = Character.codePointBefore(text, lastOffset);
                if (!isEmoji(codePoint)) {
                    return offset - deleteCharCount;
                }
                deleteCharCount += Character.charCount(codePoint);
                lastOffset -= deleteCharCount;
            }
            if (isEmoji(codePoint)) {
                boolean isZwj = false;
                int lastSeenVariantSelectorCharCount = 0;
                while (true) {
                    if (isZwj) {
                        deleteCharCount += Character.charCount(codePoint) + lastSeenVariantSelectorCharCount + 1;
                        isZwj = false;
                    }
                    lastSeenVariantSelectorCharCount = 0;
                    if (isEmojiModifier(codePoint)) {
                        int codePoint6 = Character.codePointBefore(text, lastOffset);
                        int lastOffset4 = lastOffset - Character.charCount(codePoint6);
                        if (lastOffset4 > 0 && isVariationSelector(codePoint6)) {
                            int codePoint7 = Character.codePointBefore(text, lastOffset4);
                            if (!isEmoji(codePoint7)) {
                                return offset - deleteCharCount;
                            }
                            lastSeenVariantSelectorCharCount = Character.charCount(codePoint7);
                            int charCount = lastOffset4 - Character.charCount(codePoint7);
                            codePoint2 = codePoint7;
                        } else {
                            codePoint2 = codePoint6;
                        }
                        if (isEmojiModifierBase(codePoint2)) {
                            deleteCharCount += Character.charCount(codePoint2) + lastSeenVariantSelectorCharCount;
                        }
                    } else {
                        if (lastOffset > 0) {
                            codePoint = Character.codePointBefore(text, lastOffset);
                            lastOffset -= Character.charCount(codePoint);
                            if (codePoint == 8205) {
                                isZwj = true;
                                codePoint = Character.codePointBefore(text, lastOffset);
                                lastOffset -= Character.charCount(codePoint);
                                if (lastOffset > 0 && isVariationSelector(codePoint)) {
                                    codePoint = Character.codePointBefore(text, lastOffset);
                                    lastSeenVariantSelectorCharCount = Character.charCount(codePoint);
                                    lastOffset -= Character.charCount(codePoint);
                                }
                            }
                        }
                        if (lastOffset == 0) {
                            break;
                        } else if (!isZwj) {
                            break;
                        } else if (!isEmoji(codePoint)) {
                            break;
                        }
                    }
                }
            }
            return offset - deleteCharCount;
        }
    }

    public int getOffsetAfter(CharSequence text, int offset) {
        int len = text.length();
        if (offset >= len - 1) {
            return len;
        }
        int codePoint = Character.codePointAt(text, offset);
        int nextCharCount = Character.charCount(codePoint);
        int nextOffset = offset + nextCharCount;
        if (nextOffset == 0) {
            return 0;
        }
        if (codePoint == 10) {
            if (Character.codePointAt(text, nextOffset) == 13) {
                nextCharCount++;
            }
            return offset + nextCharCount;
        } else if (isRegionalIndicatorSymbol(codePoint)) {
            if (nextOffset >= len - 1 || !isRegionalIndicatorSymbol(Character.codePointAt(text, nextOffset))) {
                int regionalIndicatorSymbolCount = offset + nextCharCount;
                return regionalIndicatorSymbolCount;
            }
            int regionalIndicatorSymbolCount2 = 0;
            int regionOffset = offset;
            while (regionOffset > 0 && isRegionalIndicatorSymbol(Character.codePointBefore(text, offset))) {
                regionOffset -= Character.charCount(Character.codePointBefore(text, offset));
                regionalIndicatorSymbolCount2++;
            }
            if (regionalIndicatorSymbolCount2 % 2 == 0) {
                nextCharCount += 2;
            }
            return offset + nextCharCount;
        } else {
            if (isKeycapBase(codePoint)) {
                nextCharCount += Character.charCount(codePoint);
            }
            if (codePoint == 8419) {
                int codePoint2 = Character.codePointBefore(text, nextOffset);
                int nextOffset2 = nextOffset + Character.charCount(codePoint2);
                if (nextOffset2 < len && isVariationSelector(codePoint2)) {
                    int tmpCodePoint = Character.codePointAt(text, nextOffset2);
                    if (isKeycapBase(tmpCodePoint)) {
                        nextCharCount += Character.charCount(codePoint2) + Character.charCount(tmpCodePoint);
                    }
                } else if (isKeycapBase(codePoint2)) {
                    nextCharCount += Character.charCount(codePoint2);
                }
                return offset + nextCharCount;
            }
            if (isEmoji(codePoint)) {
                boolean isZwj = false;
                int lastSeenVariantSelectorCharCount = 0;
                while (true) {
                    if (isZwj) {
                        nextCharCount += Character.charCount(codePoint) + lastSeenVariantSelectorCharCount + 1;
                        isZwj = false;
                    }
                    lastSeenVariantSelectorCharCount = 0;
                    if (!isEmojiModifier(codePoint)) {
                        if (nextOffset < len) {
                            codePoint = Character.codePointAt(text, nextOffset);
                            nextOffset += Character.charCount(codePoint);
                            if (codePoint == 8419) {
                                int codePoint3 = Character.codePointBefore(text, nextOffset);
                                int nextOffset3 = nextOffset + Character.charCount(codePoint3);
                                if (nextOffset3 < len && isVariationSelector(codePoint3)) {
                                    int tmpCodePoint2 = Character.codePointAt(text, nextOffset3);
                                    if (isKeycapBase(tmpCodePoint2)) {
                                        nextCharCount += Character.charCount(codePoint3) + Character.charCount(tmpCodePoint2);
                                    }
                                } else if (isKeycapBase(codePoint3)) {
                                    nextCharCount += Character.charCount(codePoint3);
                                }
                                return offset + nextCharCount;
                            } else if (isEmojiModifier(codePoint)) {
                                nextCharCount += Character.charCount(codePoint) + 0;
                                break;
                            } else if (isVariationSelector(codePoint)) {
                                nextCharCount += Character.charCount(codePoint) + 0;
                                break;
                            } else if (codePoint == 8205) {
                                isZwj = true;
                                codePoint = Character.codePointAt(text, nextOffset);
                                nextOffset += Character.charCount(codePoint);
                                if (nextOffset < len && isVariationSelector(codePoint)) {
                                    codePoint = Character.codePointAt(text, nextOffset);
                                    lastSeenVariantSelectorCharCount = Character.charCount(codePoint);
                                    nextOffset += Character.charCount(codePoint);
                                }
                            }
                        }
                        if (nextOffset >= len) {
                            break;
                        } else if (!isZwj) {
                            break;
                        } else if (!isEmoji(codePoint)) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            return offset + nextCharCount;
        }
    }
}
