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
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.nio.charset.Charset;

/**
 * <p>区块文件中存储区块对象{@link TroubleBlock}中交易集合中的单个交易对象。
 *
 * <p>交易{@code TroubleTransaction}是区块生成的重要元素之一，如果没有交易，则不会有区块产生。
 * 即便是当前区块中仅有一笔交易，且该笔交易的状态{@link TroubleTransactionStatus#FAIL}是失败的，也会生产一个区块并进行同步。
 * 对交易的操作需要充分集合智能合约的策略，尽量避免无效交易的发生，毕竟每一个区块的生成都会消耗计算机和磁盘的资源。
 * 及时交易与区块有如此紧密的联系，但在这里交易不会被直接存储进交易中，而是由区块对象的主体成员{@link TroubleBlockBody}进行保存。
 * 交易可以被{@code TroubleTransaction#TroubleTransaction(String, String, String, long, long)}构造方法创建，
 * 即创建交易必须要知道交易所属智能合约的名称{@link #contractName}、版本{@link #contractVersion}和操作员{@link #creator}，
 * 创建后可通过{@link #setRwSet(TroubleValueRWSet)}方法设置读写集。
 * 也可以直接通过{@code TroubleTransaction#TroubleTransaction(String, String, String, TroubleValueRWSet, long, long)}构造方法一起创建。
 * 最终由创建者进行{@link #build()}，得到一个不可变更的交易hash值。
 * 但创建者无法确认交易的最终状态，交易最终状态是由打包区块的服务器进行设置，调用{@link #setTransactionStatusCode(int)}方法。
 *
 * <p>交易时间戳生成，根据最新一个区块同步完成后的服务器时间戳（记作T1）和所关联的最新区块的打包时间戳（记作T2），
 * 在创建新的交易时，会得到当前的时间戳T3。
 * 那么交易时间戳T4 = T2 + (T3 - T1)。
 *
 * <p>在交易对象中，智能合约的名称、版本号和交易时间字符串{@link #time}最终是不会被写入区块文件，智能合约名称及版本号信息会被与更上一级
 * {@code TroubleBlockBody}平级的{@link TroubleBlockHeader}进行接收比对，确保一条链对应一个智能合约。
 *
 * <p>需要注意的是，在区块链中的每一笔交易都应该有一个签名，这样可以大大简化交易的验证过程，
 * 只需要重点关注最终交易的实现是否有误并标记好交易的最终状态即可。
 *
 * @author Aberic on 2018/10/7 16:45
 * @version 1.0
 * @see TroubleBlock
 * @see TroubleBlockBody
 * @see TroubleTransactionStatus
 * @see TroubleValueRWSet
 * @see TroubleBlock
 * @see TroubleBlockHeader
 * @since 1.0
 */
public class TroubleTransaction {

    /** 本次写入值所用合约名称，序列化时不写入 */
    @JSONField(name = "n", serialize = false)
    private String contractName;
    /** 本次写入值所用合约版本，序列化时不写入 */
    @JSONField(name = "v", serialize = false)
    private String contractVersion;
    /** 发起方/交易方/创建方 */
    @JSONField(name = "c")
    private String creator;
    /** 交易读写集 */
    @JSONField(name = "rw")
    private TroubleValueRWSet rwSet;
    /** 交易时间戳 */
    @JSONField(name = "t")
    private Long timestamp;
    /** 交易hash */
    @JSONField(name = "h")
    private String txHash;
    /** 交易状态 */
    @JSONField(name = "s")
    private int transactionStatusCode = TroubleTransactionStatus.SUCCESS.getCode();
    /** 交易时间戳转字符串——yyyy/MM/dd HH:mm:ss，序列化时不写入 */
    @JSONField(name = "d", serialize = false)
    private String time;

    /**
     * 交易构造
     *
     * @param contractName           本次写入值所用合约名称，序列化时不写入
     * @param contractVersion        本次写入值所用合约版本，序列化时不写入
     * @param creator                发起方/交易方/创建方
     * @param freshBlockTimestamp    最新区块的打包时间戳
     * @param freshBlockGetTimestamp 最新一个区块同步完成后的服务器时间戳
     */
    public TroubleTransaction(String contractName, String contractVersion, String creator, long freshBlockTimestamp, long freshBlockGetTimestamp) {
        this.contractName = contractName;
        this.contractVersion = contractVersion;
        this.creator = creator;
        this.timestamp = freshBlockTimestamp + System.currentTimeMillis() - freshBlockGetTimestamp;
    }

    /**
     * 交易构造
     *
     * @param contractName           本次写入值所用合约名称，序列化时不写入
     * @param contractVersion        本次写入值所用合约版本，序列化时不写入
     * @param creator                发起方/交易方/创建方
     * @param rwSet                  交易读写集
     * @param freshBlockTimestamp    最新区块的打包时间戳
     * @param freshBlockGetTimestamp 最新一个区块同步完成后的服务器时间戳
     */
    public TroubleTransaction(String contractName, String contractVersion, String creator, TroubleValueRWSet rwSet, long freshBlockTimestamp, long freshBlockGetTimestamp) {
        this(contractName, contractVersion, creator, freshBlockTimestamp, freshBlockGetTimestamp);
        this.rwSet = rwSet;
    }

    public void setRwSet(TroubleValueRWSet rwSet) {
        this.rwSet = rwSet;
    }

    public void setTransactionStatusCode(int transactionStatusCode) {
        this.transactionStatusCode = transactionStatusCode;
    }

    public TroubleTransaction build() {
        txHash = Hashing.sha256().hashString(String.format("%s%s%s%s",
                creator, JSON.toJSONString(rwSet), timestamp), Charset.forName("UTF-8")).toString();
        return this;
    }

    public String getTime() {
        return DateFormatUtils.format(timestamp, "yyyy/MM/dd HH:mm:ss");
    }
}
