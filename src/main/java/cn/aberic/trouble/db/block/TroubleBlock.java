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

/**
 * <p>区块对象
 *
 * <p>区块文件中的最大单元，以换行符进行分隔。
 * 包含有区块头{@link TroubleBlockHeader}和区块体{@link TroubleBlockBody}两个成员。
 *
 * <p>在区块头中的当前区块hash由{@code TroubleBlock#calculateHash()}方法生成。
 *
 * @author Aberic on 2018/10/7 16:00
 * @version 1.0
 * @see TroubleBlockHeader
 * @see TroubleBlockBody
 * @since 1.0
 */
public class TroubleBlock<H extends TroubleBlockHeader, B extends TroubleBlockBody> {

    /** 区块头部信息 */
    @JSONField(name = "h")
    private H header;
    /** 区块数据体 */
    @JSONField(name = "b")
    private B body;

    public TroubleBlock(H header, B body) {
        this.header = header;
        this.body = body;
    }

    public void setHeader(H header) {
        this.header = header;
    }

    public void setBody(B body) {
        this.body = body;
    }

    public H getHeader() {
        return header;
    }

    public B getBody() {
        return body;
    }

}
