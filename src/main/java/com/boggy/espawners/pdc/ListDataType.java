package com.boggy.espawners.pdc;

import java.nio.charset.*;
import org.bukkit.persistence.*;
import java.nio.*;
import java.util.*;

public class ListDataType implements PersistentDataType<byte[], String[]>
{
    private Charset charset;
    
    public ListDataType(final Charset charset) {
        this.charset = charset;
    }
    
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }
    
    public Class<String[]> getComplexType() {
        return String[].class;
    }
    
    public byte[] toPrimitive(final String[] strings, final PersistentDataAdapterContext itemTagAdapterContext) {
        final byte[][] allStringBytes = new byte[strings.length][];
        int total = 0;
        for (int i = 0; i < allStringBytes.length; ++i) {
            final byte[] bytes = strings[i].getBytes(this.charset);
            allStringBytes[i] = bytes;
            total += bytes.length;
        }
        final ByteBuffer buffer = ByteBuffer.allocate(total + allStringBytes.length * 4);
        for (final byte[] bytes2 : allStringBytes) {
            buffer.putInt(bytes2.length);
            buffer.put(bytes2);
        }
        return buffer.array();
    }
    
    public String[] fromPrimitive(final byte[] bytes, final PersistentDataAdapterContext itemTagAdapterContext) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        final ArrayList<String> list = new ArrayList<String>();
        while (buffer.remaining() > 0) {
            if (buffer.remaining() < 4) {
                break;
            }
            final int stringLength = buffer.getInt();
            if (buffer.remaining() < stringLength) {
                break;
            }
            final byte[] stringBytes = new byte[stringLength];
            buffer.get(stringBytes);
            list.add(new String(stringBytes, this.charset));
        }
        return list.toArray(new String[list.size()]);
    }
}
