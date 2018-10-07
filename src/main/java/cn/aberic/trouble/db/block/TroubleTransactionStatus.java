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

package cn.aberic.trouble.db.block;

/**
 * <p>交易状态
 *
 * <p>交易状态主要是指交易的成功与否状态，
 * {@link TroubleTransactionStatus#SUCCESS}表示交易成功，
 * {@link TroubleTransactionStatus#FAIL}表示交易失败。
 * 交易状态没有给出失败的描述字符串类型设置，当一笔交易在执行的时候，在所有区块都同步完成的情况下，自身没有作恶行为，
 * 则在执行一次交易的过程中如果发生了错误，会及时得到反馈，而不应该在区块打包的过程中给予反馈。
 * 区块打包的过程中失败的交易将仅标记一个错误的状态，不会提供任何有关错误的描述信息。
 *
 * @author Aberic on 2018/10/7 16:20
 * @version 1.0
 * @see TroubleBlockHeader
 * @see TroubleBlockBody
 * @since 1.0
 */
public enum TroubleTransactionStatus {

    /** 交易成功 */
    SUCCESS(1),
    /** 交易失败 */
    FAIL(0);

    /** 交易结果码 */
    private int code;

    /**
     * 当前交易状态
     *
     * @param code 交易结果码
     */
    TroubleTransactionStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
