package io.flutter.plugin.common;

import androidx.core.view.MotionEventCompat;
import io.flutter.Log;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.UByte;
/* loaded from: classes.dex */
public class StandardMessageCodec implements MessageCodec<Object> {
    private static final byte BIGINT = 5;
    private static final byte BYTE_ARRAY = 8;
    private static final byte DOUBLE = 6;
    private static final byte DOUBLE_ARRAY = 11;
    private static final byte FALSE = 2;
    private static final byte FLOAT_ARRAY = 14;
    public static final StandardMessageCodec INSTANCE = new StandardMessageCodec();
    private static final byte INT = 3;
    private static final byte INT_ARRAY = 9;
    private static final byte LIST = 12;
    private static final boolean LITTLE_ENDIAN;
    private static final byte LONG = 4;
    private static final byte LONG_ARRAY = 10;
    private static final byte MAP = 13;
    private static final byte NULL = 0;
    private static final byte STRING = 7;
    private static final String TAG = "StandardMessageCodec#";
    private static final byte TRUE = 1;
    private static final Charset UTF8;

    static {
        LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
        UTF8 = Charset.forName("UTF8");
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public ByteBuffer encodeMessage(Object message) {
        if (message == null) {
            return null;
        }
        ExposedByteArrayOutputStream stream = new ExposedByteArrayOutputStream();
        writeValue(stream, message);
        ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public Object decodeMessage(ByteBuffer message) {
        if (message == null) {
            return null;
        }
        message.order(ByteOrder.nativeOrder());
        Object value = readValue(message);
        if (message.hasRemaining()) {
            throw new IllegalArgumentException("Message corrupted");
        }
        return value;
    }

    protected static final void writeSize(ByteArrayOutputStream stream, int value) {
        if (value < 0) {
            Log.e(TAG, "Attempted to write a negative size.");
        }
        if (value < 254) {
            stream.write(value);
        } else if (value <= 65535) {
            stream.write(254);
            writeChar(stream, value);
        } else {
            stream.write(255);
            writeInt(stream, value);
        }
    }

    protected static final void writeChar(ByteArrayOutputStream stream, int value) {
        if (LITTLE_ENDIAN) {
            stream.write(value);
            stream.write(value >>> 8);
            return;
        }
        stream.write(value >>> 8);
        stream.write(value);
    }

    protected static final void writeInt(ByteArrayOutputStream stream, int value) {
        if (LITTLE_ENDIAN) {
            stream.write(value);
            stream.write(value >>> 8);
            stream.write(value >>> 16);
            stream.write(value >>> 24);
            return;
        }
        stream.write(value >>> 24);
        stream.write(value >>> 16);
        stream.write(value >>> 8);
        stream.write(value);
    }

    protected static final void writeLong(ByteArrayOutputStream stream, long value) {
        if (LITTLE_ENDIAN) {
            stream.write((byte) value);
            stream.write((byte) (value >>> 8));
            stream.write((byte) (value >>> 16));
            stream.write((byte) (value >>> 24));
            stream.write((byte) (value >>> 32));
            stream.write((byte) (value >>> 40));
            stream.write((byte) (value >>> 48));
            stream.write((byte) (value >>> 56));
            return;
        }
        stream.write((byte) (value >>> 56));
        stream.write((byte) (value >>> 48));
        stream.write((byte) (value >>> 40));
        stream.write((byte) (value >>> 32));
        stream.write((byte) (value >>> 24));
        stream.write((byte) (value >>> 16));
        stream.write((byte) (value >>> 8));
        stream.write((byte) value);
    }

    protected static final void writeFloat(ByteArrayOutputStream stream, float value) {
        writeInt(stream, Float.floatToIntBits(value));
    }

    protected static final void writeDouble(ByteArrayOutputStream stream, double value) {
        writeLong(stream, Double.doubleToLongBits(value));
    }

    protected static final void writeBytes(ByteArrayOutputStream stream, byte[] bytes) {
        writeSize(stream, bytes.length);
        stream.write(bytes, 0, bytes.length);
    }

    protected static final void writeAlignment(ByteArrayOutputStream stream, int alignment) {
        int mod = stream.size() % alignment;
        if (mod != 0) {
            for (int i = 0; i < alignment - mod; i++) {
                stream.write(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeValue(ByteArrayOutputStream stream, Object value) {
        int i = 0;
        if (value == null || value.equals(null)) {
            stream.write(0);
        } else if (value instanceof Boolean) {
            stream.write(((Boolean) value).booleanValue() ? 1 : 2);
        } else if (value instanceof Number) {
            if ((value instanceof Integer) || (value instanceof Short) || (value instanceof Byte)) {
                stream.write(3);
                writeInt(stream, ((Number) value).intValue());
            } else if (value instanceof Long) {
                stream.write(4);
                writeLong(stream, ((Long) value).longValue());
            } else if ((value instanceof Float) || (value instanceof Double)) {
                stream.write(6);
                writeAlignment(stream, 8);
                writeDouble(stream, ((Number) value).doubleValue());
            } else if (value instanceof BigInteger) {
                stream.write(5);
                writeBytes(stream, ((BigInteger) value).toString(16).getBytes(UTF8));
            } else {
                throw new IllegalArgumentException("Unsupported Number type: " + value.getClass());
            }
        } else if (value instanceof CharSequence) {
            stream.write(7);
            writeBytes(stream, value.toString().getBytes(UTF8));
        } else if (value instanceof byte[]) {
            stream.write(8);
            writeBytes(stream, (byte[]) value);
        } else if (value instanceof int[]) {
            stream.write(9);
            int[] array = (int[]) value;
            writeSize(stream, array.length);
            writeAlignment(stream, 4);
            int length = array.length;
            while (i < length) {
                int n = array[i];
                writeInt(stream, n);
                i++;
            }
        } else if (value instanceof long[]) {
            stream.write(10);
            long[] array2 = (long[]) value;
            writeSize(stream, array2.length);
            writeAlignment(stream, 8);
            int length2 = array2.length;
            while (i < length2) {
                long n2 = array2[i];
                writeLong(stream, n2);
                i++;
            }
        } else if (value instanceof double[]) {
            stream.write(11);
            double[] array3 = (double[]) value;
            writeSize(stream, array3.length);
            writeAlignment(stream, 8);
            int length3 = array3.length;
            while (i < length3) {
                double d = array3[i];
                writeDouble(stream, d);
                i++;
            }
        } else if (value instanceof List) {
            stream.write(12);
            List<?> list = (List) value;
            writeSize(stream, list.size());
            for (Object o : list) {
                writeValue(stream, o);
            }
        } else if (value instanceof Map) {
            stream.write(13);
            Map<?, ?> map = (Map) value;
            writeSize(stream, map.size());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                writeValue(stream, entry.getKey());
                writeValue(stream, entry.getValue());
            }
        } else if (value instanceof float[]) {
            stream.write(14);
            float[] array4 = (float[]) value;
            writeSize(stream, array4.length);
            writeAlignment(stream, 4);
            int length4 = array4.length;
            while (i < length4) {
                float f = array4[i];
                writeFloat(stream, f);
                i++;
            }
        } else {
            throw new IllegalArgumentException("Unsupported value: '" + value + "' of type '" + value.getClass() + "'");
        }
    }

    protected static final int readSize(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            throw new IllegalArgumentException("Message corrupted");
        }
        int value = buffer.get() & UByte.MAX_VALUE;
        if (value < 254) {
            return value;
        }
        if (value == 254) {
            return buffer.getChar();
        }
        return buffer.getInt();
    }

    protected static final byte[] readBytes(ByteBuffer buffer) {
        int length = readSize(buffer);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    protected static final void readAlignment(ByteBuffer buffer, int alignment) {
        int mod = buffer.position() % alignment;
        if (mod != 0) {
            buffer.position((buffer.position() + alignment) - mod);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Object readValue(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            throw new IllegalArgumentException("Message corrupted");
        }
        byte type = buffer.get();
        return readValueOfType(type, buffer);
    }

    protected Object readValueOfType(byte type, ByteBuffer buffer) {
        switch (type) {
            case 0:
                return null;
            case 1:
                return true;
            case 2:
                return false;
            case 3:
                Object result = Integer.valueOf(buffer.getInt());
                return result;
            case 4:
                Object result2 = Long.valueOf(buffer.getLong());
                return result2;
            case 5:
                byte[] hex = readBytes(buffer);
                Object result3 = new BigInteger(new String(hex, UTF8), 16);
                return result3;
            case 6:
                readAlignment(buffer, 8);
                Object result4 = Double.valueOf(buffer.getDouble());
                return result4;
            case 7:
                byte[] bytes = readBytes(buffer);
                Object result5 = new String(bytes, UTF8);
                return result5;
            case 8:
                Object result6 = readBytes(buffer);
                return result6;
            case 9:
                int length = readSize(buffer);
                int[] array = new int[length];
                readAlignment(buffer, 4);
                buffer.asIntBuffer().get(array);
                buffer.position(buffer.position() + (length * 4));
                return array;
            case 10:
                int length2 = readSize(buffer);
                long[] array2 = new long[length2];
                readAlignment(buffer, 8);
                buffer.asLongBuffer().get(array2);
                buffer.position(buffer.position() + (length2 * 8));
                return array2;
            case 11:
                int length3 = readSize(buffer);
                double[] array3 = new double[length3];
                readAlignment(buffer, 8);
                buffer.asDoubleBuffer().get(array3);
                buffer.position(buffer.position() + (length3 * 8));
                return array3;
            case MotionEventCompat.AXIS_RX /* 12 */:
                int size = readSize(buffer);
                List<Object> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(readValue(buffer));
                }
                return list;
            case 13:
                int size2 = readSize(buffer);
                Map<Object, Object> map = new HashMap<>();
                for (int i2 = 0; i2 < size2; i2++) {
                    map.put(readValue(buffer), readValue(buffer));
                }
                return map;
            case MotionEventCompat.AXIS_RZ /* 14 */:
                int length4 = readSize(buffer);
                float[] array4 = new float[length4];
                readAlignment(buffer, 4);
                buffer.asFloatBuffer().get(array4);
                buffer.position(buffer.position() + (length4 * 4));
                return array4;
            default:
                throw new IllegalArgumentException("Message corrupted");
        }
    }

    /* loaded from: classes.dex */
    static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        /* JADX INFO: Access modifiers changed from: package-private */
        public byte[] buffer() {
            return this.buf;
        }
    }
}
