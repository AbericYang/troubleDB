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

import java.util.LinkedList;
import java.util.Queue;

/**
 * <p>本次交易的读写集对象
 *
 * <p>每一次的交易{@link TroubleTransaction}都必然会产生至少一个写集{@code TroubleValueWrite}，
 * 同时有可能产生读集{@code TroubleValueRead}，
 * 这些交易中的读写集会被分别置入{@link #reads}和{@link #writes}中。
 *
 * <p>读写集将以队列的方式进行声明，以便于在最终被打包区块进行验证的时候可以按照先进先出的原则执行，防止读写集错乱而导致无效交易。
 *
 * @author Aberic on 2018/10/7 16:17
 * @version 1.0
 * @see TroubleTransaction
 * @see TroubleValueRead
 * @see TroubleValueWrite
 * @since 1.0
 */
public class TroubleValueRWSet {

    /** 交易读内容 */
    @JSONField(name = "r")
    private Queue<TroubleValueRead> reads;
    /** 交易写内容 */
    @JSONField(name = "w")
    private Queue<TroubleValueWrite> writes;

    public TroubleValueRWSet() {
        reads = new LinkedList<>();
        writes = new LinkedList<>();
    }

    /** 新增一条读数据 */
    public void offerRead(TroubleValueRead read) {
        this.reads.offer(read);
    }

    /** 新增一条写数据 */
    public void offerWrite(TroubleValueWrite write) {
        this.writes.offer(write);
    }

    public void setReads(Queue<TroubleValueRead> reads) {
        this.reads = reads;
    }

    public void setWrites(Queue<TroubleValueWrite> writes) {
        this.writes = writes;
    }

    public Queue<TroubleValueRead> getReads() {
        return reads;
    }

    public Queue<TroubleValueWrite> getWrites() {
        return writes;
    }
}
