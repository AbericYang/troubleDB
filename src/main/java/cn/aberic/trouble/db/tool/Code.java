/*
 * MIT License
 *
 * Copyright (c) 2018 Aberic Yang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.aberic.trouble.db.tool;

/**
 * @author Aberic on 2018/10/16 16:31
 * @version 1.0
 * @since 1.0
 */
public class Code {

    public static String int2Hash(int num) {
        StringBuilder result = new StringBuilder();
        byte[] bytes = intToByte32(num);
        StringBuilder tmp = new StringBuilder();
        boolean zero = true;
        for (byte b : bytes) {
            if (b == 0x00) {
                if (zero) {
                    tmp.append("0");
                } else {
                    result.append(b1[tmp.toString().length() - 1]);
                    tmp.delete(0, tmp.length());
                    tmp.append("0");
                }
                zero = true;
            } else if (b == 0x01) {
                if (zero) {
                    result.append(b0[tmp.toString().length() - 1]);
                    tmp.delete(0, tmp.length());
                    tmp.append("1");
                } else {
                    tmp.append("1");
                }
                zero = false;
            }
        }
        if (zero) {
            result.append(b0[tmp.toString().length() - 1]);
        } else {
            result.append(b1[tmp.toString().length() - 1]);
        }
        return result.toString().replace("qz", "h").replace("mz", "o");
    }

    public static int hash2Int(String hash) {
        byte[] bytes = new byte[32];
        char[] chars = hash2ByteString(hash).toCharArray();
        for (int i = 0; i < 32; i++) {
            bytes[i] = String.valueOf(chars[i]).equals("0") ? (byte) 0x00 : (byte) 0x01;
        }
        return byte32ToInt(bytes);
    }

    private static String hash2ByteString(String hash) {
        hash = hash.replace("h", "qz").replace("o", "mz");
        StringBuilder result = new StringBuilder();
        String[] arr = hash.split("z");
        for (String str : arr) {
            switch (str) {
                case "q":
                    result.append("0");
                    break;
                case "w":
                    result.append("00");
                    break;
                case "m":
                    result.append("1");
                    break;
                case "n":
                    result.append("11");
                    break;
                default:
                    for (int i = 2; i < 32; i++) {
                        if (str.length() == i) {
                            if (str.substring(1, 2).equals("b")) {
                                result.append(s0[i]);
                            } else {
                                result.append(s1[i]);
                            }
                            break;
                        }
                    }
                    break;
            }
        }
        return result.toString();
    }


    /**
     * 将int转换为32位byte.
     * 实际上每个8位byte只存储了一个0或1的数字
     *
     * @param num num
     *
     * @return byte[]
     */
    private static byte[] intToByte32(int num) {
        byte[] arr = new byte[32];
        for (int i = 31; i >= 0; i--) {
            // &1 也可以改为num&0x01,表示取最地位数字.
            arr[i] = (byte) (num & 1);
            // 右移一位.
            num >>= 1;
        }
        return arr;
    }

    /**
     * 将长度为32的byte数组转换为一个int类型值.
     * 每一个8位byte都只存储了0或1的数字.
     *
     * @param arr arr
     *
     * @return int
     */
    private static int byte32ToInt(byte[] arr) {
        if (arr == null || arr.length != 32) {
            throw new IllegalArgumentException("byte数组必须不为空,并且长度是32!");
        }
        int sum = 0;
        for (int i = 0; i < 32; ++i) {
            sum |= (arr[i] << (31 - i));
        }
        return sum;
    }

    private static final String s0_1 = "0";
    private static final String s0_2 = "00";
    private static final String s0_3 = "000";
    private static final String s0_4 = "0000";
    private static final String s0_5 = "00000";
    private static final String s0_6 = "000000";
    private static final String s0_7 = "0000000";
    private static final String s0_8 = "00000000";
    private static final String s0_9 = "000000000";
    private static final String s0_10 = "0000000000";
    private static final String s0_11 = "00000000000";
    private static final String s0_12 = "000000000000";
    private static final String s0_13 = "0000000000000";
    private static final String s0_14 = "00000000000000";
    private static final String s0_15 = "000000000000000";
    private static final String s0_16 = "0000000000000000";
    private static final String s0_17 = "00000000000000000";
    private static final String s0_18 = "000000000000000000";
    private static final String s0_19 = "0000000000000000000";
    private static final String s0_20 = "00000000000000000000";
    private static final String s0_21 = "000000000000000000000";
    private static final String s0_22 = "0000000000000000000000";
    private static final String s0_23 = "00000000000000000000000";
    private static final String s0_24 = "000000000000000000000000";
    private static final String s0_25 = "0000000000000000000000000";
    private static final String s0_26 = "00000000000000000000000000";
    private static final String s0_27 = "000000000000000000000000000";
    private static final String s0_28 = "0000000000000000000000000000";
    private static final String s0_29 = "00000000000000000000000000000";
    private static final String s0_30 = "000000000000000000000000000000";
    private static final String s0_31 = "0000000000000000000000000000000";

    private static final String s1_1 = "1";
    private static final String s1_2 = "11";
    private static final String s1_3 = "111";
    private static final String s1_4 = "1111";
    private static final String s1_5 = "11111";
    private static final String s1_6 = "111111";
    private static final String s1_7 = "1111111";
    private static final String s1_8 = "11111111";
    private static final String s1_9 = "111111111";
    private static final String s1_10 = "1111111111";
    private static final String s1_11 = "11111111111";
    private static final String s1_12 = "111111111111";
    private static final String s1_13 = "1111111111111";
    private static final String s1_14 = "11111111111111";
    private static final String s1_15 = "111111111111111";
    private static final String s1_16 = "1111111111111111";
    private static final String s1_17 = "11111111111111111";
    private static final String s1_18 = "111111111111111111";
    private static final String s1_19 = "1111111111111111111";
    private static final String s1_20 = "11111111111111111111";
    private static final String s1_21 = "111111111111111111111";
    private static final String s1_22 = "1111111111111111111111";
    private static final String s1_23 = "11111111111111111111111";
    private static final String s1_24 = "111111111111111111111111";
    private static final String s1_25 = "1111111111111111111111111";
    private static final String s1_26 = "11111111111111111111111111";
    private static final String s1_27 = "111111111111111111111111111";
    private static final String s1_28 = "1111111111111111111111111111";
    private static final String s1_29 = "11111111111111111111111111111";
    private static final String s1_30 = "111111111111111111111111111111";
    private static final String s1_31 = "1111111111111111111111111111111";

    private static final String b0_1 = "qz";
    private static final String b0_2 = "wz";
    private static final String b0_3 = "ebz";
    private static final String b0_4 = "rbez";
    private static final String b0_5 = "tbppz";
    private static final String b0_6 = "yb8y5z";
    private static final String b0_7 = "ubu8e3z";
    private static final String b0_8 = "ibsjld9z";
    private static final String b0_9 = "xbaue6cez";
    private static final String b0_10 = "pbdu8d3kcz";
    private static final String b0_11 = "abd83jnf1xz";
    private static final String b0_12 = "sb83n01lmnlz";
    private static final String b0_13 = "dblkmv023vwdz";
    private static final String b0_14 = "fbqxe9231lkcsz";
    private static final String b0_15 = "gbniw20skjnqqpz";
    private static final String b0_16 = "ybbuq8xqkn23m91z";
    private static final String b0_17 = "jbnxjye019ufnd09z";
    private static final String b0_18 = "kbbnicjni289yf25jz";
    private static final String b0_19 = "lberb149uninqa2pkdz";
    private static final String b0_20 = "wb2nfmwxkxm10mcaknlz";
    private static final String b0_21 = "xbc81b2uybd0nalnsqqdz";
    private static final String b0_22 = "cbaxkm012jenijncnalknz";
    private static final String b0_23 = "vb7jsxdnxn10a3nexlknmxz";
    private static final String b0_24 = "bbjsybdf9u29unfxknakjnkz";
    private static final String b0_25 = "nbeibqwkmdpmkpxkna418y3nz";
    private static final String b0_26 = "mbqnieyr6g2i3jn4xinmfxjnaz";
    private static final String b0_27 = "qbuybw9829inlknmkn1xnnl1n2z";
    private static final String b0_28 = "wbrn83un4nx2n09t5inse98dn1lz";
    private static final String b0_29 = "ebtn92n9funxansx1nd09nxansknz";
    private static final String b0_30 = "rbynd9un1infksnkjnekj2njinbijz";
    private static final String b0_31 = "tbucbb23eijrnfinin59nknakbkkabz";

    private static final String b1_1 = "mz";
    private static final String b1_2 = "nz";
    private static final String b1_3 = "bcz";
    private static final String b1_4 = "vcez";
    private static final String b1_5 = "ccppz";
    private static final String b1_6 = "xc8y5z";
    private static final String b1_7 = "wcu8e3z";
    private static final String b1_8 = "lcsjld9z";
    private static final String b1_9 = "kcaue6cez";
    private static final String b1_10 = "jcdu8d3kcz";
    private static final String b1_11 = "ycd83jnf1xz";
    private static final String b1_12 = "gc83n01lmnlz";
    private static final String b1_13 = "fclkmv023vwdz";
    private static final String b1_14 = "dcqxe9231lkcsz";
    private static final String b1_15 = "scniw20skjnqqpz";
    private static final String b1_16 = "acbuq8xqkn23m91z";
    private static final String b1_17 = "pcnxjye019ufnd09z";
    private static final String b1_18 = "xcbnicjni289yf25jz";
    private static final String b1_19 = "icerb149uninqa2pkdz";
    private static final String b1_20 = "uc2nfmwxkxm10mcaknlz";
    private static final String b1_21 = "ycc81b2uybd0nalnsqqdz";
    private static final String b1_22 = "tcaxkm012jenijncnalknz";
    private static final String b1_23 = "rc7jsxdnxn10a3nexlknmxz";
    private static final String b1_24 = "ecjsybdf9u29unfxknakjnkz";
    private static final String b1_25 = "wceibqwkmdpmkpxkna418y3nz";
    private static final String b1_26 = "qcqnieyr6g2i3jn4xinmfxjnaz";
    private static final String b1_27 = "mcuybw9829inlknmkn1xnnl1n2z";
    private static final String b1_28 = "ncvn83un4nx2n09t5inse98dn1lz";
    private static final String b1_29 = "bccn92n9funxansx1nd09nxansknz";
    private static final String b1_30 = "vcxnd9un1infksnkjnekj2njinbijz";
    private static final String b1_31 = "ccwcbb23eijrnfinin59nknakbkkabz";

    private static final String[] s0 = new String[]{
            s0_1, s0_2, s0_3, s0_4, s0_5, s0_6, s0_7, s0_8, s0_9, s0_10, s0_11, s0_12, s0_13, s0_14, s0_15, s0_16, s0_17, s0_18, s0_19, s0_20, s0_21, s0_22, s0_23, s0_24, s0_25, s0_26, s0_27, s0_28, s0_29, s0_30, s0_31
    };

    private static final String[] s1 = new String[]{
            s1_1, s1_2, s1_3, s1_4, s1_5, s1_6, s1_7, s1_8, s1_9, s1_10, s1_11, s1_12, s1_13, s1_14, s1_15, s1_16, s1_17, s1_18, s1_19, s1_20, s1_21, s1_22, s1_23, s1_24, s1_25, s1_26, s1_27, s1_28, s1_29, s1_30, s1_31
    };

    private static final String[] b0 = new String[]{
            b0_1, b0_2, b0_3, b0_4, b0_5, b0_6, b0_7, b0_8, b0_9, b0_10, b0_11, b0_12, b0_13, b0_14, b0_15, b0_16, b0_17, b0_18, b0_19, b0_20, b0_21, b0_22, b0_23, b0_24, b0_25, b0_26, b0_27, b0_28, b0_29, b0_30, b0_31
    };

    private static final String[] b1 = new String[]{
            b1_1, b1_2, b1_3, b1_4, b1_5, b1_6, b1_7, b1_8, b1_9, b1_10, b1_11, b1_12, b1_13, b1_14, b1_15, b1_16, b1_17, b1_18, b1_19, b1_20, b1_21, b1_22, b1_23, b1_24, b1_25, b1_26, b1_27, b1_28, b1_29, b1_30, b1_31
    };


}
