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

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * <p>区块头对象
 *
 * <p>区块{@link TroubleBlock}的基本可见信息对象。
 * 可以通过{@code TroubleBlockHeader#TroubleBlockHeader(String, String)}来构造一个包含智能合约名称和版本号的区块头初始对象，
 * 也可以通过{@code TroubleBlockHeader#TroubleBlockHeader()}来构造一个无参区块头初始对象，
 * 随后通过{@link #setSmartContractName(String)}及
 * {@link #setSmartContractVersion(String)}的方式来赋值智能合约名称和版本号。
 * 在最终生成区块的时候，需要调用{@code TroubleBlockHeader#build(int, String, String, long, long)}方法来完成区块头。
 *
 * <p>区块头中的区块生成时间戳的方案与{@link TroubleTransaction#timestamp}一样，都需要根据上一区块的同步完成时间戳和上一区块内
 * 区块头中的时间戳来生成。
 * 具体是根据最新一个区块同步完成后的服务器时间戳（记作T1）和所关联的最新区块的打包时间戳（记作T2），
 * 在完成新的区块时，会得到当前的时间戳T3。
 * 那么当前待打包区块的时间戳T4 = T2 + (T3 - T1)。
 *
 * @author Aberic on 2018/10/7 16:21
 * @version 1.0
 * @see TroubleBlock
 * @see TroubleTransaction
 * @since 1.0
 */
public class TroubleBlockHeader {

    /** 本次写入智能合约名称 */
    @JSONField(name = "n")
    private String smartContractName;
    /** 本次写入智能合约版本号 */
    @JSONField(name = "v")
    private String smartContractVersion;
    /** 区块高度 */
    @JSONField(name = "h")
    private int height;
    /** 当前区块hash */
    @JSONField(name = "c")
    private String currentBlockHash;
    /** 上一区块hash */
    @JSONField(name = "p")
    String previousBlockHash;
    /** 当前区块生成时间戳 */
    @JSONField(name = "t")
    long timestamp;
    /** 交易时间戳转字符串——yyyy/MM/dd HH:mm:ss，序列化时不写入 */
    @JSONField(serialize = false)
    private String time;

    public TroubleBlockHeader() {
    }

    /**
     * 区快头构造
     *
     * @param smartContractName    本次写入智能合约名称
     * @param smartContractVersion 本次写入智能合约版本号
     */
    public TroubleBlockHeader(String smartContractName, String smartContractVersion) {
        this.smartContractName = smartContractName;
        this.smartContractVersion = smartContractVersion;
    }

    /**
     * 同步新区快头
     *
     * @param height                 区块高度
     * @param currentBlockHash       当前区块hash
     * @param previousBlockHash      上一区块hash
     * @param freshBlockTimestamp    最新区块的打包时间戳
     * @param freshBlockGetTimestamp 最新一个区块同步完成后的服务器时间戳
     */
    public TroubleBlockHeader build(int height, String currentBlockHash, String previousBlockHash, long freshBlockTimestamp, long freshBlockGetTimestamp) {
        this.height = height;
        this.currentBlockHash = currentBlockHash;
        this.previousBlockHash = previousBlockHash;
        this.timestamp = freshBlockTimestamp + System.currentTimeMillis() - freshBlockGetTimestamp;
        return this;
    }

    public void setSmartContractName(String smartContractName) {
        this.smartContractName = smartContractName;
    }

    public void setSmartContractVersion(String smartContractVersion) {
        this.smartContractVersion = smartContractVersion;
    }

    public String getTime() {
        return DateFormatUtils.format(timestamp, "yyyy/MM/dd HH:mm:ss");
    }

}
