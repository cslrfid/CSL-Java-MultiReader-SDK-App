package Tools;

import java.util.*;

/**
 * Hex encoding/decoding class.
 */
public class Hex {

    static final byte[] HEX_CHAR_TABLE = {
        (byte)'0', (byte)'1', (byte)'2', (byte)'3',
        (byte)'4', (byte)'5', (byte)'6', (byte)'7',
        (byte)'8', (byte)'9', (byte)'A', (byte)'B',
        (byte)'C', (byte)'D', (byte)'E', (byte)'F'
    };

    /**
     * return short from hex string
     * @param str
     * @return
     */
    public static short ToShort(String str)
    {
        if (str == null || str.length() == 0)
            return 0x0;
        return (short)Integer.parseInt(str, 16);
    }

    /**
     * return char from hex string
     * @param str
     * @return
     */
    public static char ToChar(String str)
    {
        if (str == null || str.length() == 0)
            return 0x0;
        return (char)Integer.parseInt(str, 16);
    }
    /**
     * return char array from byte array
     * @param Input
     * @return
     */
    public static char[] ToCharArray(byte[] Input)
    {
        if (Input == null)
            return null;
        char[] Output = new char[Input.length / 2 + Input.length % 2];
        int j = 0;
        for (int i = 0; i < Output.length; i++)
        {
            if (j + 1 < Input.length)
                Output[i] = (char)(Input[j] << 8 | MemoryMap.unsignedByteToChar(Input[j + 1]));
            else
                Output[i] = (char)(Input[j] << 8 | 0x0);
            j += 2;
        }
        return Output;
    }
    /**
     * return char array from string input
     * @param Input
     * @return
     */
    public static char[] ToCharArray(String Input)
    {
        String newString = "";
        char c;
        // remove all none A-F, 0-9, characters
        for (int i = 0; i < Input.length(); i++)
        {
            c = Input.charAt(i);
            if (IsHexDigit(c))
                newString += c;
        }
        int fullUshortLength = newString.length() / 4;
        int leftUshortLength = newString.length() % 4;
        char[] ushorts = new char[fullUshortLength + (leftUshortLength > 0 ? 1 : 0)];
        String hex = "";
        int j = 0;
        for (int i = 0; i < ushorts.length; i++)
        {
            if (i < fullUshortLength)
            {
                hex = new String(new char[] { newString.charAt(j), newString.charAt(j+1), newString.charAt(j+2), newString.charAt(j+3) });
            }
            else
            {
                switch (leftUshortLength)
                {
                    case 1:
                        hex = new String(new char[] { newString.charAt(j), '0', '0', '0' });
                        break;
                    case 2: hex = new String(new char[] { newString.charAt(j), newString.charAt(j+1), '0', '0' });
                        break;
                    case 3: hex = new String(new char[] { newString.charAt(j), newString.charAt(j+1), newString.charAt(j+2), '0' });
                        break;
                    default: break;
                }

            }
            ushorts[i] = HexToChar(hex);
            j = j + 4;
        }
        return ushorts;
    }

    /**
     * Convent char array to byte array
     * @param data
     * @return
     */
    public static byte[] ToBytes(char[] data)
    {
        if(data == null || data.length == 0)
            return new byte[0];
        int bytes = data.length << 1;

        byte[] reda = new byte[bytes];
        for (int i = 0; i < data.length; i++)
        {
            reda[i * 2] = (byte)((data[i] >> 8) & 0xff);
            reda[i * 2 + 1] = (byte)(data[i] & 0xff);
        }

        return reda;
    }

    /**
     * Creates a byte array from the hexadecimal string. Each two characters are combined
     * to create one byte. First two hexadecimal characters become first byte in returned array.
     * Non-hexadecimal characters are ignored.
     * @param hexString string to be converted to byte array
     * @return byte array, in the same left-to-right order as the hexString
     */
    public static byte[] ToBytes(String hexString)
    {
        if (hexString == null || hexString.length() == 0)
            return new byte[0];
        String newString = "";
        char c;
        // remove all none A-F, 0-9, characters
        for (int i = 0; i < hexString.length(); i++)
        {
            c = hexString.charAt(i);
            if (IsHexDigit(c))
                newString += c;
        }

        int byteLength = newString.length() / 2 + newString.length() % 2;
        byte[] bytes = new byte[byteLength];
        String hex = "";

        for (int i = 0; i < byteLength; i++)
        {
            if (i * 2 + 1 < newString.length())
                hex = new String(new char[] { newString.charAt(i * 2), newString.charAt(i * 2 + 1) });
            else
                hex = new String(new char[] { newString.charAt(i * 2), '0' });
            bytes[i] = HexToByte(hex);
        }
        return bytes;
    }

    /**
     * Int to Hex String Conversion
     * @param hex integer number
     * @return string
     */
    public static String ToString(int hex)
    {
        String  hexString = paddingString(Integer.toHexString(hex), 4, '0', true);

        return hexString;
    }

    /**
     * Byte to String Conversion
     * @param bytes byte array
     * @return string
     */
    public static String ToString(byte[] bytes)
    {
        if (bytes == null)
            return "";
        String hexString = "";
        for (int i=0; i < bytes.length; i++) {
            hexString += paddingString(Integer.toString(MemoryMap.unsignedByteToChar(bytes[i]), 16), 2, '0', true);
        }
        return hexString;
    }
    /**
     * Byte to String Conversion
     * @param bytes byte array
     * @param offset start offset
     * @param count number of count to convert
     * @return string
     */
    public static String ToString(byte[] bytes, int offset, int count)
    {
        if (bytes == null)
            return "";
        String hexString = "";
        for (int i = offset, j = 0; (i < bytes.length && j < count); i++, j++)
        {
            hexString += paddingString(Integer.toString(MemoryMap.unsignedByteToChar(bytes[i]), 16), 2, '0', true);
        }
        return hexString;
    }
    /**
     * char to String Conversion
     * @param data char array
     * @return string
     */
    public static String ToString(char[] data)
    {
        if (data == null)
            return "";
        String hexString = "";
        for (int i = 0; i < data.length; i++)
        {
            hexString += paddingString(Integer.toString(data[i], 16), 4, '0', true);
        }
        return hexString;
    }
    /**
     * char to String Conversion
     * @param data source data
     * @param offset start offset
     * @param count number of count to convert
     * @return
     */
    public static String ToString(char[] data, int offset, int count)
    {
        if (data == null)
            return "";
        String hexString = "";
        for (int i = offset, j = 0; (i < data.length && j < count); i++, j++)
        {
            hexString += paddingString(Integer.toString(data[i], 16), 4, '0', true);
        }
        return hexString;
    }
    /**
     * Determines if given string is in proper hexadecimal string format
     * @param hexString
     * @return
     */
    public static boolean IsHexFormat(String hexString)
    {
        boolean hexFormat = true;

        for (int i = 0; i < hexString.length(); i++)
        {
            if (!IsHexDigit(hexString.charAt(i)))
            {
                hexFormat = false;
                break;
            }
        }
        return hexFormat;
    }

    /**
     * Returns true if c is a hexadecimal digit (A-F, a-f, 0-9)
     * @param c Character to test
     * @return true if hex digit, false if not
     */
    public static boolean IsHexDigit(char c)
    {
        int numChar;
        int numA = Integer.valueOf('A').intValue();
        int num1 = Integer.valueOf('0').intValue();
        c = Character.toUpperCase(c);
        numChar = Integer.valueOf(c).intValue();
        if (numChar >= numA && numChar < (numA + 6))
            return true;
        if (numChar >= num1 && numChar < (num1 + 10))
            return true;
        return false;
    }

    /// <summary>
    /// Converts 1 or 2 character string into equivalant byte value
    /// </summary>
    /// <param name="hex">1 or 2 character string</param>
    /// <returns>byte</returns>
    private static byte HexToByte(String hex)
    {
        if (hex.length() > 2 || hex.length() <= 0)
        {
            MessageBox.ErrorShow("hex must be 1 or 2 characters in length");
            return 0;
        }
        return (byte)Integer.parseInt(hex, 16);
    }
    private static char HexToChar(String hex)
    {
        if (hex.length() > 4 || hex.length() <= 0)
        {
            MessageBox.ErrorShow("hex must be 1 or 2 characters in length");
            return 0;
        }
        return (char)Integer.parseInt(hex, 16);
    }

    /**
     * Padding string with leading/tailing character
     * @param s string to pad
     * @param n total output length
     * @param c character to pad
     * @param paddingLeft padding left or right
     * @return padded string
     */
    public static String paddingString(String s, int n, char c, boolean paddingLeft)
    {
        if (s == null) {
            return s;
        }
        int add = n - s.length();
        if(add <= 0){
            return s;
        }
        StringBuffer str = new StringBuffer(s);
        char[] ch = new char[add];
        Arrays.fill(ch, c);
        if(paddingLeft){
            str.insert(0, ch);
        } else {
            str.append(ch);
        }
        
        return str.toString();
    }

    /**
     * Get bit length from string
     * @param hexString
     * @return
     */
    public static int GetBitLength(String hexString)
    {
        if (hexString == null || hexString.length() == 0)
        {
            return 0;
        }

        String newString = "";
        char c;
        // remove all none A-F, 0-9, characters
        for (int i = 0; i < hexString.length(); i++)
        {
            c = hexString.charAt(i);
            if (IsHexDigit(c))
                newString += c;
        }

        return (newString.length() * 4);
    }

    /**
     * Get bit length from byte array
     * @param bytes
     * @return
     */
    public static int GetBitLength(byte[] bytes)
    {
        if (bytes == null || bytes.length == 0)
        {
            return 0;
        }
        return (bytes.length * 8);
    }

    /**
     * Get Word length from string
     * @param wordString
     * @return
     */
    public static short GetWordLength(String wordString)
    {
        if (wordString == null || wordString.length() == 0)
        {
            return 0;
        }
        String newString = "";
        char c;
        // remove all none A-F, 0-9, characters
        for (int i = 0; i < wordString.length(); i++)
        {
            c = wordString.charAt(i);
            if (IsHexDigit(c))
                newString += c;
        }
        return (short)ToCharArray(newString).length;
    }
}
