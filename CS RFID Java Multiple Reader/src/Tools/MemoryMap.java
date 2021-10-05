package Tools;

/**
 * Utility to do memory mapping
 */
public class MemoryMap {
    /**
     * Put int value to byte array
     * @param byteArray byte array
     * @param offset starting offset of byte array
     * @param value the int value
     */
    public static void putIntToByteArray(byte[] byteArray, int offset, int value)
    {
        byteArray[offset] = (byte)( value & 0x000000FF);
        byteArray[offset+1] = (byte)( (value >> 8) & 0x000000FF);
        byteArray[offset+2] = (byte)( (value >> 16) & 0x000000FF);
        byteArray[offset+3] = (byte)( (value >> 24) & 0x000000FF);
    }

    /**
     * Get int value from byte array
     * @param byteArray byte array
     * @param offset starting offset of byte array
     * @return int value
     */
    public static int getIntFromByteArray(byte[] byteArray, int offset)
    {
        int i = 0;

        i += unsignedByteToInt(byteArray[offset]);
        i += unsignedByteToInt(byteArray[offset+1]) << 8;
        i += unsignedByteToInt(byteArray[offset+2]) << 16;
        i += unsignedByteToInt(byteArray[offset+3]) << 24;

        return i;
    }

    /**
     * Convert unsigned byte to int
     * @param b byte to be converted
     * @return converted int value
     */
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    /**
     * Put short value to byte array
     * @param byteArray byte array
     * @param offset starting offset of byte array
     * @param value the short value
     * @param isBigEndian putting order is Big Endian or Little Endian
     */
    public static void putShortToByteArray(byte[] byteArray, int offset, short value, boolean isBigEndian)
    {
        if (isBigEndian)
        {
            byteArray[offset+1] = (byte)( value & 0x000000FF);
            byteArray[offset] = (byte)( (value >> 8) & 0x000000FF);
        }
        else
        {
            byteArray[offset] = (byte)( value & 0x000000FF);
            byteArray[offset+1] = (byte)( (value >> 8) & 0x000000FF);
        }
    }

    /**
     * Get short value from byte array
     * @param byteArray byte array
     * @param offset starting offset of byte array
     * @param isBigEndian getting order is Big Endian or Little Endian
     * @return short value
     */
    public static short getShortFromByteArray(byte[] byteArray, int offset, boolean isBigEndian)
    {
        short i = 0;

        if (isBigEndian)
        {
            i += unsignedByteToShort(byteArray[offset]) << 8;
            i += unsignedByteToShort(byteArray[offset+1]);
        }
        else
        {
            i += unsignedByteToShort(byteArray[offset]);
            i += unsignedByteToShort(byteArray[offset+1]) << 8;
        }
        
        return i;
    }

    /**
     * Convert unsigned byte to short
     * @param b byte to be converted
     * @return converted short value
     */
    public static short unsignedByteToShort(byte b) {
        if ((b & 0x80) == 0x80)
            return (short) (128 + (b & 0x7f));
        else
            return (short) b;
    }

    /**
     * Convert unsigned byte to char
     * @param b byte to be converted
     * @return converted char value
     */
    public static char unsignedByteToChar(byte b) {
        if ((b & 0x80) == 0x80)
            return (char) (128 + (b & 0x7f));
        else
            return (char) b;
    }

    /**
     * Resize an array to new size
     * @param oldArray array to be resized
     * @param newSize new size
     * @return resized array
     */
    public static Object resizeArray(Object oldArray, int newSize)
    {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(elementType,newSize);
        int preserveLength = Math.min(oldSize,newSize);
        if (preserveLength > 0)
            System.arraycopy(oldArray,0,newArray,0,preserveLength);
        return newArray;
    }
}
