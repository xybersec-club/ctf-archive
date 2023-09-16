package io.flutter.embedding.android;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/* loaded from: classes.dex */
public class KeyData {
    private static final int BYTES_PER_FIELD = 8;
    public static final String CHANNEL = "flutter/keydata";
    private static final int FIELD_COUNT = 5;
    private static final String TAG = "KeyData";
    String character;
    long logicalKey;
    long physicalKey;
    boolean synthesized;
    long timestamp;
    Type type;

    /* loaded from: classes.dex */
    public enum Type {
        kDown(0),
        kUp(1),
        kRepeat(2);
        
        private long value;

        Type(long value) {
            this.value = value;
        }

        public long getValue() {
            return this.value;
        }

        static Type fromLong(long value) {
            switch ((int) value) {
                case 0:
                    return kDown;
                case 1:
                    return kUp;
                case 2:
                    return kRepeat;
                default:
                    throw new AssertionError("Unexpected Type value");
            }
        }
    }

    public KeyData() {
    }

    public KeyData(ByteBuffer buffer) {
        long charSize = buffer.getLong();
        this.timestamp = buffer.getLong();
        this.type = Type.fromLong(buffer.getLong());
        this.physicalKey = buffer.getLong();
        this.logicalKey = buffer.getLong();
        this.synthesized = buffer.getLong() != 0;
        if (buffer.remaining() != charSize) {
            throw new AssertionError(String.format("Unexpected char length: charSize is %d while buffer has position %d, capacity %d, limit %d", Long.valueOf(charSize), Integer.valueOf(buffer.position()), Integer.valueOf(buffer.capacity()), Integer.valueOf(buffer.limit())));
        }
        this.character = null;
        if (charSize != 0) {
            byte[] strBytes = new byte[(int) charSize];
            buffer.get(strBytes, 0, (int) charSize);
            try {
                this.character = new String(strBytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("UTF-8 unsupported");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBuffer toBytes() {
        try {
            String str = this.character;
            byte[] charBytes = str == null ? null : str.getBytes("UTF-8");
            int charSize = charBytes == null ? 0 : charBytes.length;
            ByteBuffer packet = ByteBuffer.allocateDirect(charSize + 48);
            packet.order(ByteOrder.LITTLE_ENDIAN);
            packet.putLong(charSize);
            packet.putLong(this.timestamp);
            packet.putLong(this.type.getValue());
            packet.putLong(this.physicalKey);
            packet.putLong(this.logicalKey);
            packet.putLong(this.synthesized ? 1L : 0L);
            if (charBytes != null) {
                packet.put(charBytes);
            }
            return packet;
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 not supported");
        }
    }
}
