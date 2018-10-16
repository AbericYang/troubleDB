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

import java.util.Arrays;

/**
 * 0                                -> q
 * 00                               -> we
 * 000                              -> er2
 * 0000                             -> rteo
 * 00000                            -> typp0
 * 000000                           -> yu8y5q
 * 0000000                          -> uiu8e3f
 * 00000000                         -> iosjld9v
 * 000000000                        -> opaue6cee
 * 0000000000                       -> padu8d3kcw
 * 00000000000                      -> asd83jnf1oq
 * 000000000000                     -> sd83n01lmnla
 * 0000000000000                    -> dflkmv023vwds
 * 00000000000000                   -> fgqoe9231lkcsd
 * 000000000000000                  -> ghniw20skjnqqpf
 * 0000000000000000                 -> hjbuq8oqkn23m91g
 * 00000000000000000                -> jknojhe019ufnd09h
 * 000000000000000000               -> klbnicjni289hf25jj
 * 0000000000000000000              -> lzerb149uninqa2pkdk
 * 00000000000000000000             -> zx2nfmwokom10mcaknll
 * 000000000000000000000            -> xcc81b2uhbd0nalnsqqdm
 * 0000000000000000000000           -> cvaokm012jenijncnalkng
 * 00000000000000000000000          -> vb7jsodnon10a3neolknmof
 * 000000000000000000000000         -> bnjshbdf9u29unfoknakjnkj
 * 0000000000000000000000000        -> nmeibqwkmdpmkpokna418h3ne
 * 00000000000000000000000000       -> mqqniehr6g2i3jn4oinmfojna1
 * 000000000000000000000000000      -> qwuhbw9829inlknmkn1onnl1n2j
 * 0000000000000000000000000000     -> wern83un4no2n09t5inse98dn1l1
 * 00000000000000000000000000000    -> ertn92n9funoanso1nd09noansknr
 * 000000000000000000000000000000   -> rtynd9un1infksnkjnekj2njinbijf
 * 0000000000000000000000000000000  -> tyucbb23eijrnfinin59nknakbkkabs
 * <p>
 * 1                                -> m
 * 11                               -> nb
 * 111                              -> bv2
 * 1111                             -> vceo
 * 11111                            -> cxpp0
 * 111111                           -> xz8y5q
 * 1111111                          -> zlu8e3f
 * 11111111                         -> lksjld9v
 * 111111111                        -> kjaue6cee
 * 1111111111                       -> jhdu8d3kcw
 * 11111111111                      -> hgd83jnf1oq
 * 111111111111                     -> gf83n01lmnla
 * 1111111111111                    -> fdlkmv023vwds
 * 11111111111111                   -> dsqoe9231lkcsd
 * 111111111111111                  -> saniw20skjnqqpf
 * 1111111111111111                 -> apbuq8oqkn23m91g
 * 11111111111111111                -> ponojhe019ufnd09h
 * 111111111111111111               -> oibnicjni289hf25jj
 * 1111111111111111111              -> iuerb149uninqa2pkdk
 * 11111111111111111111             -> uy2nfmwokom10mcaknll
 * 111111111111111111111            -> ytc81b2uhbd0nalnsqqdm
 * 1111111111111111111111           -> traokm012jenijncnalkng
 * 11111111111111111111111          -> re7jsodnon10a3neolknmof
 * 111111111111111111111111         -> ewjshbdf9u29unfoknakjnkj
 * 1111111111111111111111111        -> wqeibqwkmdpmkpokna418h3ne
 * 11111111111111111111111111       -> qmqniehr6g2i3jn4oinmfojna1
 * 111111111111111111111111111      -> mnuhbw9829inlknmkn1onnl1n2j
 * 1111111111111111111111111111     -> nbvn83un4no2n09t5inse98dn1l1
 * 11111111111111111111111111111    -> bvcn92n9funoanso1nd09noansknr
 * 111111111111111111111111111111   -> vcxnd9un1infksnkjnekj2njinbijf
 * 1111111111111111111111111111111  -> cxzcbb23eijrnfinin59nknakbkkabs
 *
 * @author Aberic on 2018/10/16 16:31
 * @version 1.0
 * @see
 * @since 1.0
 */
public class Code {

    public static String int2Hash(int num) {
        StringBuilder result = new StringBuilder();
        byte[] bytes = intToByte32(num);
        StringBuilder tmp = new StringBuilder();
        boolean zero = false;
        for (byte b : bytes) {
            if (b == 0x00) {
                if (zero) {
                    tmp.append("0");
                } else {
                    result.append(tmp.toString());
                    tmp.delete(0, tmp.length());
                    tmp.append("0");
                }
                zero = true;
            } else if (b == 0x01) {
                if (zero) {
                    result.append(tmp.toString());
                    tmp.delete(0, tmp.length());
                    tmp.append("1");
                } else {
                    tmp.append("1");
                }
                zero = false;
            }
        }
        result.append(tmp.toString());
        return result.toString();
    }

    public static int hash2Int(String hash) {
        byte[] bytes = new byte[32];
        char[] chars = hash.toCharArray();
        for (int i = 0; i < 32; i++) {
            bytes[i] = String.valueOf(chars[i]).equals("0") ? (byte) 0x00 : (byte) 0x01;
        }
        return byte32ToInt(bytes);
    }


    /**
     * 将int转换为32位byte.
     * 实际上每个8位byte只存储了一个0或1的数字
     *
     * @param num num
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
        System.out.println("arr = " + Arrays.toString(arr));
        return arr;
    }

    /**
     * 将长度为32的byte数组转换为一个int类型值.
     * 每一个8位byte都只存储了0或1的数字.
     *
     * @param arr arr
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

    private static String hash2ByteString(String hash) {
        return null;
    }

    private static String trans0(String transFrom) {
        switch (transFrom) {
            case s0_1:
                return b0_1;
            case s0_2:
                return b0_2;
            case s0_3:
                return b0_3;
            case s0_4:
                return b0_4;
            case s0_5:
                return b0_5;
            case s0_6:
                return b0_6;
            case s0_7:
                return b0_7;
            case s0_8:
                return b0_8;
            case s0_9:
                return b0_9;
            case s0_10:
                return b0_10;
            case s0_11:
                return b0_11;
            case s0_12:
                return b0_12;
            case s0_13:
                return b0_13;
            case s0_14:
                return b0_14;
            case s0_15:
                return b0_15;
            case s0_16:
                return b0_16;
            case s0_17:
                return b0_17;
            case s0_18:
                return b0_18;
            case s0_19:
                return b0_19;
            case s0_20:
                return b0_20;
            case s0_21:
                return b0_21;
            case s0_22:
                return b0_22;
            case s0_23:
                return b0_23;
            case s0_24:
                return b0_24;
            case s0_25:
                return b0_25;
            case s0_26:
                return b0_26;
            case s0_27:
                return b0_27;
            case s0_28:
                return b0_28;
            case s0_29:
                return b0_29;
            case s0_30:
                return b0_30;
            case s0_31:
                return b0_31;
            default:
                return null;
        }
    }

    private static String trans1(String transFrom) {
        switch (transFrom) {
            case s1_1:
                return b1_1;
            case s1_2:
                return b1_2;
            case s1_3:
                return b1_3;
            case s1_4:
                return b1_4;
            case s1_5:
                return b1_5;
            case s1_6:
                return b1_6;
            case s1_7:
                return b1_7;
            case s1_8:
                return b1_8;
            case s1_9:
                return b1_9;
            case s1_10:
                return b1_10;
            case s1_11:
                return b1_11;
            case s1_12:
                return b1_12;
            case s1_13:
                return b1_13;
            case s1_14:
                return b1_14;
            case s1_15:
                return b1_15;
            case s1_16:
                return b1_16;
            case s1_17:
                return b1_17;
            case s1_18:
                return b1_18;
            case s1_19:
                return b1_19;
            case s1_20:
                return b1_20;
            case s1_21:
                return b1_21;
            case s1_22:
                return b1_22;
            case s1_23:
                return b1_23;
            case s1_24:
                return b1_24;
            case s1_25:
                return b1_25;
            case s1_26:
                return b1_26;
            case s1_27:
                return b1_27;
            case s1_28:
                return b1_28;
            case s1_29:
                return b1_29;
            case s1_30:
                return b1_30;
            case s1_31:
                return b1_31;
            default:
                return null;
        }
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

    private static final String b0_1 = "q";
    private static final String b0_2 = "we";
    private static final String b0_3 = "er2";
    private static final String b0_4 = "rteo";
    private static final String b0_5 = "typp0";
    private static final String b0_6 = "yu8y5q";
    private static final String b0_7 = "uiu8e3f";
    private static final String b0_8 = "iosjld9v";
    private static final String b0_9 = "opaue6cee";
    private static final String b0_10 = "padu8d3kcw";
    private static final String b0_11 = "asd83jnf1oq";
    private static final String b0_12 = "sd83n01lmnla";
    private static final String b0_13 = "dflkmv023vwds";
    private static final String b0_14 = "fgqoe9231lkcsd";
    private static final String b0_15 = "ghniw20skjnqqpf";
    private static final String b0_16 = "hjbuq8oqkn23m91g";
    private static final String b0_17 = "jknojhe019ufnd09h";
    private static final String b0_18 = "klbnicjni289hf25jj";
    private static final String b0_19 = "lzerb149uninqa2pkdk";
    private static final String b0_20 = "zx2nfmwokom10mcaknll";
    private static final String b0_21 = "xcc81b2uhbd0nalnsqqdm";
    private static final String b0_22 = "cvaokm012jenijncnalkng";
    private static final String b0_23 = "vb7jsodnon10a3neolknmof";
    private static final String b0_24 = "bnjshbdf9u29unfoknakjnkj";
    private static final String b0_25 = "nmeibqwkmdpmkpokna418h3ne";
    private static final String b0_26 = "mqqniehr6g2i3jn4oinmfojna1";
    private static final String b0_27 = "qwuhbw9829inlknmkn1onnl1n2j";
    private static final String b0_28 = "wern83un4no2n09t5inse98dn1l1";
    private static final String b0_29 = "ertn92n9funoanso1nd09noansknr";
    private static final String b0_30 = "rtynd9un1infksnkjnekj2njinbijf";
    private static final String b0_31 = "tyucbb23eijrnfinin59nknakbkkabs";

    private static final String b1_1 = "m";
    private static final String b1_2 = "nb";
    private static final String b1_3 = "bv2";
    private static final String b1_4 = "vceo";
    private static final String b1_5 = "cxpp0";
    private static final String b1_6 = "xz8y5q";
    private static final String b1_7 = "zlu8e3f";
    private static final String b1_8 = "lksjld9v";
    private static final String b1_9 = "kjaue6cee";
    private static final String b1_10 = "jhdu8d3kcw";
    private static final String b1_11 = "hgd83jnf1oq";
    private static final String b1_12 = "gf83n01lmnla";
    private static final String b1_13 = "fdlkmv023vwds";
    private static final String b1_14 = "dsqoe9231lkcsd";
    private static final String b1_15 = "saniw20skjnqqpf";
    private static final String b1_16 = "apbuq8oqkn23m91g";
    private static final String b1_17 = "ponojhe019ufnd09h";
    private static final String b1_18 = "oibnicjni289hf25jj";
    private static final String b1_19 = "iuerb149uninqa2pkdk";
    private static final String b1_20 = "uy2nfmwokom10mcaknll";
    private static final String b1_21 = "ytc81b2uhbd0nalnsqqdm";
    private static final String b1_22 = "traokm012jenijncnalkng";
    private static final String b1_23 = "re7jsodnon10a3neolknmof";
    private static final String b1_24 = "ewjshbdf9u29unfoknakjnkj";
    private static final String b1_25 = "wqeibqwkmdpmkpokna418h3ne";
    private static final String b1_26 = "qmqniehr6g2i3jn4oinmfojna1";
    private static final String b1_27 = "mnuhbw9829inlknmkn1onnl1n2j";
    private static final String b1_28 = "nbvn83un4no2n09t5inse98dn1l1";
    private static final String b1_29 = "bvcn92n9funoanso1nd09noansknr";
    private static final String b1_30 = "vcxnd9un1infksnkjnekj2njinbijf";
    private static final String b1_31 = "cxzcbb23eijrnfinin59nknakbkkabs";

}
