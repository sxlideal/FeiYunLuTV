package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.apken;

import java.io.UnsupportedEncodingException;

public class FormatConversion
{
  public static String bytes2StringU(byte[] temp)
    throws UnsupportedEncodingException
  {
    int l = temp.length;
    byte[] b2 = new byte[l];
    for (int i = 0; i < l / 2; i++) {
      b2[(2 * i)] = temp[(2 * i + 1)];
      b2[(2 * i + 1)] = temp[(2 * i)];
    }
    String str = new String(b2, "UTF-16");
    return str;
  }

  public static byte[] String2bytesU(String str) throws UnsupportedEncodingException {
    byte[] b = new byte[2];
    byte[] temp = new byte[4];
    temp = str.getBytes("UTF-16");
    b[0] = temp[3];
    b[1] = temp[2];
    return b;
  }

  public static String bytes2StringA(byte[] temp)
    throws UnsupportedEncodingException
  {
    String str = new String(temp);
    return str;
  }

  public static int Tbytes2intD(byte[] temp)
  {
    int value = temp[0] & 0xFF | (temp[1] & 0xFF) << 8;
    return value;
  }

  public static byte[] intD2Tbytes(int value) {
    byte[] b = new byte[2];

    for (int i = 0; i < 2; i++) {
      b[(1 - i)] = ((byte)(value >>> 8 - i * 8));
    }
    return b;
  }

  public static long bytes2intD(byte[] temp)
  {
    long value = temp[0] & 0xFF | (temp[1] & 0xFF) << 8 | (temp[2] & 0xFF) << 16 | (temp[3] & 0xFF) << 24;
    return value;
  }

  public static byte[] int2bytesD(int value) {
    byte[] b = new byte[4];

    for (int i = 0; i < 4; i++) {
      b[(3 - i)] = ((byte)(value >>> 24 - i * 8));
    }
    return b;
  }
  public static byte[] flaot2bytesD(float value) {
    byte[] b = new byte[4];
    int l = Float.floatToRawIntBits(value);
    for (int i = 0; i < 4; i++) {
      b[(3 - i)] = ((byte)(l >>> 24 - i * 8));
    }
    return b;
  }
  public static float byte2int_Float(byte[] b) {
    int bits = b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | 
      (b[0] & 0xFF) << 24;

    int sign = (bits & 0x80000000) == 0 ? 1 : -1;
    int exponent = (bits & 0x7F800000) >> 23;
    int mantissa = bits & 0x7FFFFF;

    mantissa |= 8388608;

    float f = (float)(sign * mantissa * Math.pow(2.0D, exponent - 150));

    return f;
  }

  public static int bytes2intO(byte[] temp)
  {
    int value = (temp[0] & 0xFF) << 24 | (temp[1] & 0xFF) << 16 | (temp[2] & 0xFF) << 8 | temp[3] & 0xFF;
    return value;
  }

  public static byte[] int2bytesO(int value) {
    byte[] b = new byte[4];

    for (int i = 0; i < 4; i++) {
      b[i] = ((byte)(value >>> 24 - i * 8));
    }
    return b;
  }
}