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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * <p>区块体对象
 *
 * <p>区块体{@code TroubleBlockBody}是区块{@link TroubleBlock}的成员之一，存储着区块的所有有效数据，
 * 这些有效数据中可能也包括了无效的交易{@link TroubleTransaction}信息，但对于每一次的提交操作而言，即便是无效的交易也会被看成是有效数据，
 * 以确保交易在流转的过程中始终会有一个可见状态。
 * 交易的验证会按照交易发生的时间戳大小排序进行。
 *
 * @author Aberic on 2018/10/7 16:34
 * @version 1.0
 * @see TroubleBlock
 * @see TroubleTransaction
 * @since 1.0
 */
public class TroubleBlockBody<T extends TroubleTransaction> {

    /** 交易数量，序列化时不写入 */
    @JSONField(serialize = false)
    private int txCount;
    /** 交易集合 */
    @JSONField(name = "t")
    private List<T> transactions;

    public int getTxCount() {
        return null != transactions ? transactions.size() : 0;
    }

    public List<T> getTransactions() {
        return transactions;
    }

    public TroubleBlockBody(List<T> transactions) {
        this.transactions = transactions;
    }

    /** 获取当前数据体字符串信息 */
    String bodyString() {
        return null != transactions ? String.format("%s%s", getTxCount(), JSON.toJSONString(transactions)) : null;
    }
}
