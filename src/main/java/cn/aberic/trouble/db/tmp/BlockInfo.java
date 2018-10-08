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

package cn.aberic.trouble.db.tmp;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 区块在区块文件中的基本信息——数据操作层-data manipulation
 * <p>
 * 作者：Aberic on 2018/08/27 17:29
 * <p>
 * 邮箱：abericyang@gmail.com
 *
 * {@inheritDoc} 生成javadoc时，继承的时候，直接把父类的注解都copy下来
 */
public class BlockInfo {

    /** 区块高度 */
    @JSONField(name="h")
    private int height;
    /** 区块hash */
    @JSONField(name="b")
    private String blockHash;
    /** 区块中交易hash集合 */
    @JSONField(name="t")
    private List<String> transactionHashList;
    /** 区块所在区块文件编号 */
    @JSONField(name="n")
    private int num;
    /** 区块所在区块文件中的行号 */
    @JSONField(name="l")
    private int line;

}
