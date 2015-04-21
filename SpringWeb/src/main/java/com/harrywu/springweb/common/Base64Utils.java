package com.harrywu.springweb.common;

public final class Base64Utils {

    static final int CHUNK_SIZE = 76;

    static final byte CHUNK_SEPARATOR[] = "\r\n".getBytes();

    static final int BASELENGTH = 255;

    static final int LOOKUPLENGTH = 64;

    static final int EIGHTBIT = 8;

    static final int SIXTEENBIT = 16;

    static final int TWENTYFOURBITGROUP = 24;

    static final int FOURBYTE = 4;

    static final int SIGN = -128;

    static final byte PAD = 61;

    private static byte base64Alphabet[];

    private static byte lookUpBase64Alphabet[];

    static {
        base64Alphabet = new byte[255];
        lookUpBase64Alphabet = new byte[64];

        for (int i = 0; i < 255; i++)
            base64Alphabet[i] = -1;

        for (int j = 90; j >= 65; j--)
            base64Alphabet[j] = (byte) (j - 65);

        for (int k = 122; k >= 97; k--)
            base64Alphabet[k] = (byte) ((k - 97) + 26);

        for (int l = 57; l >= 48; l--)
            base64Alphabet[l] = (byte) ((l - 48) + 52);

        base64Alphabet[43] = 62;
        base64Alphabet[47] = 63;
        for (int i1 = 0; i1 <= 25; i1++)
            lookUpBase64Alphabet[i1] = (byte) (65 + i1);

        int j1 = 26;
        for (int k1 = 0; j1 <= 51; k1++) {
            lookUpBase64Alphabet[j1] = (byte) (97 + k1);
            j1++;
        }

        j1 = 52;
        for (int l1 = 0; j1 <= 61; l1++) {
            lookUpBase64Alphabet[j1] = (byte) (48 + l1);
            j1++;
        }

        lookUpBase64Alphabet[62] = 43;
        lookUpBase64Alphabet[63] = 47;
    }

    public Base64Utils() {
    }

    public static boolean isBase64(byte byte0) {
        if (byte0 == 61)
            return true;
        if (byte0 < 0)
            return false;
        return base64Alphabet[byte0] != -1;
    }

    public static String encode(byte abyte0[]) {
        if (abyte0 == null)
            throw new NullPointerException();
        return new String(encodeToByteArray(abyte0));
    }

    public static byte[] decode(String s) {
        if (s == null)
            throw new NullPointerException();
        return decode(s.getBytes());
    }

    public static boolean isByteArrayBase64(byte abyte0[]) {
        abyte0 = discardWhitespace(abyte0);
        int i = abyte0.length;
        if (i == 0)
            return true;
        for (int j = 0; j < i; j++)
            if (!isBase64(abyte0[j]))
                return false;
        return true;
    }

    public static byte[] decode(byte abyte0[]) {
        abyte0 = discardNonBase64(abyte0);
        if (abyte0.length == 0)
            return new byte[0];
        int i = abyte0.length / 4;
        byte abyte1[] = null;

        int j = 0;

        int l;
        for (l = abyte0.length; abyte0[l - 1] == 61;)
            if (--l == 0)
                return new byte[0];

        abyte1 = new byte[l - i];
        for (int i1 = 0; i1 < i; i1++) {
            int k = i1 * 4;
            byte byte5 = abyte0[k + 2];
            byte byte6 = abyte0[k + 3];
            byte byte0 = base64Alphabet[abyte0[k]];
            byte byte1 = base64Alphabet[abyte0[k + 1]];
            if (byte5 != 61 && byte6 != 61) {
                byte byte2 = base64Alphabet[byte5];
                byte byte4 = base64Alphabet[byte6];
                abyte1[j] = (byte) (byte0 << 2 | byte1 >> 4);
                abyte1[j + 1] = (byte) ((byte1 & 0xf) << 4 | byte2 >> 2 & 0xf);
                abyte1[j + 2] = (byte) (byte2 << 6 | byte4);
            } else
                if (byte5 == 61)
                    abyte1[j] = (byte) (byte0 << 2 | byte1 >> 4);
                else
                    if (byte6 == 61) {
                        byte byte3 = base64Alphabet[byte5];
                        abyte1[j] = (byte) (byte0 << 2 | byte1 >> 4);
                        abyte1[j + 1] = (byte) ((byte1 & 0xf) << 4 | byte3 >> 2 & 0xf);
                    }
            j += 3;
        }
        return abyte1;
    }
/*
    private static String encodeChunked(byte abyte0[]) {
        if (abyte0 == null)
            throw new NullPointerException();
        return new String(encodeChunkedToByteArray(abyte0));
    }
*/
    public static byte[] encodeToByteArray(byte abyte0[]) {
        return encodeBase64(abyte0, false);
    }
/*
    private static byte[] encodeChunkedToByteArray(byte abyte0[]) {
        return encodeBase64(abyte0, true);
    }
*/    
    private static byte[] discardNonBase64(byte abyte0[]) {
        byte abyte1[] = new byte[abyte0.length];
        int i = 0;
        for (int j = 0; j < abyte0.length; j++)
            if (isBase64(abyte0[j]))
                abyte1[i++] = abyte0[j];
        byte abyte2[] = new byte[i];
        System.arraycopy(abyte1, 0, abyte2, 0, i);
        return abyte2;
    }

    private static byte[] encodeBase64(byte abyte0[], boolean flag) {
        int i = abyte0.length * 8;
        int j = i % 24;
        int k = i / 24;
        byte abyte1[] = null;
        int l = 0;
        int i1 = 0;
        if (j != 0)
            l = (k + 1) * 4;
        else
            l = k * 4;
        if (flag) {
            i1 = CHUNK_SEPARATOR.length != 0 ? (int) Math.ceil(l / 76F) : 0;
            l += i1 * CHUNK_SEPARATOR.length;
        }
        abyte1 = new byte[l];

        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 76;
        int j2 = 0;
        for (l1 = 0; l1 < k; l1++) {
            k1 = l1 * 3;
            byte byte5 = abyte0[k1];
            byte byte8 = abyte0[k1 + 1];
            byte byte10 = abyte0[k1 + 2];
            byte byte3 = (byte) (byte8 & 0xf);
            byte byte0 = (byte) (byte5 & 3);
            byte byte11 = (byte5 & 0xffffff80) != 0 ? (byte) (byte5 >> 2 ^ 0xc0) : (byte) (byte5 >> 2);
            byte byte14 = (byte8 & 0xffffff80) != 0 ? (byte) (byte8 >> 4 ^ 0xf0) : (byte) (byte8 >> 4);
            byte byte16 = (byte10 & 0xffffff80) != 0 ? (byte) (byte10 >> 6 ^ 0xfc)
                    : (byte) (byte10 >> 6);
            abyte1[j1] = lookUpBase64Alphabet[byte11];
            abyte1[j1 + 1] = lookUpBase64Alphabet[byte14 | byte0 << 4];
            abyte1[j1 + 2] = lookUpBase64Alphabet[byte3 << 2 | byte16];
            abyte1[j1 + 3] = lookUpBase64Alphabet[byte10 & 0x3f];
            j1 += 4;
            if (flag && j1 == i2) {
                System.arraycopy(CHUNK_SEPARATOR, 0, abyte1, j1, CHUNK_SEPARATOR.length);
                j2++;
                i2 = 76 * (j2 + 1) + j2 * CHUNK_SEPARATOR.length;
                j1 += CHUNK_SEPARATOR.length;
            }
        }

        k1 = l1 * 3;
        if (j == 8) {
            byte byte6 = abyte0[k1];
            byte byte1 = (byte) (byte6 & 3);
            byte byte12 = (byte6 & 0xffffff80) != 0 ? (byte) (byte6 >> 2 ^ 0xc0) : (byte) (byte6 >> 2);
            abyte1[j1] = lookUpBase64Alphabet[byte12];
            abyte1[j1 + 1] = lookUpBase64Alphabet[byte1 << 4];
            abyte1[j1 + 2] = 61;
            abyte1[j1 + 3] = 61;
        } else
            if (j == 16) {
                byte byte7 = abyte0[k1];
                byte byte9 = abyte0[k1 + 1];
                byte byte4 = (byte) (byte9 & 0xf);
                byte byte2 = (byte) (byte7 & 3);
                byte byte13 = (byte7 & 0xffffff80) != 0 ? (byte) (byte7 >> 2 ^ 0xc0)
                        : (byte) (byte7 >> 2);
                byte byte15 = (byte9 & 0xffffff80) != 0 ? (byte) (byte9 >> 4 ^ 0xf0)
                        : (byte) (byte9 >> 4);
                abyte1[j1] = lookUpBase64Alphabet[byte13];
                abyte1[j1 + 1] = lookUpBase64Alphabet[byte15 | byte2 << 4];
                abyte1[j1 + 2] = lookUpBase64Alphabet[byte4 << 2];
                abyte1[j1 + 3] = 61;
            }
        if (flag && j2 < i1)
            System.arraycopy(CHUNK_SEPARATOR, 0, abyte1, l - CHUNK_SEPARATOR.length,
                    CHUNK_SEPARATOR.length);
        return abyte1;
    }

    private static byte[] discardWhitespace(byte abyte0[]) {
        byte abyte1[] = new byte[abyte0.length];
        int i = 0;
        int j = 0;
        do
            if (j < abyte0.length) {
                switch (abyte0[j]) {
                    default:
                        abyte1[i++] = abyte0[j];
                        // fall through
                    case 9: // '\t'
                    case 10: // '\n'
                    case 13: // '\r'
                    case 32: // ' '
                        j++;
                        break;
                }
            } else {
                byte abyte2[] = new byte[i];
                System.arraycopy(abyte1, 0, abyte2, 0, i);
                return abyte2;
            }
        while (true);
    }

/*
    @Deprecated
    // Sometime return String then getBytes, value not match with just decoded value
    public static String decode(String s) {
        if (s == null)
            throw new NullPointerException();
        return new String(decode(s.getBytes()));
    }

    @Deprecated
    public static String encode(String s) {
        if (s == null)
            throw new NullPointerException();
        return new String(encodeToByteArray(s.getBytes()));
    }
*/
}
