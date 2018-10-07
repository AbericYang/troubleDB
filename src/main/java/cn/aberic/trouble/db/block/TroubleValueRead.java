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
 * <p>一笔交易操作的读取Key
 *
 * <p>每一笔交易{@link TroubleTransaction}有可能会触发对区块链数据的读操作，每一次的读取操作都会执行如get类似List的方法。
 * 每一个{@code TroubleValueRead}都会将当前所属交易的读取结果记录下来，
 * 并置入本次交易读写集{@link TroubleTransaction#rwSet}的读集{@link TroubleValueRWSet#reads}中存放。
 * 读集数据可作为最终区块进行本地同步时候的一种验证策略。
 *
 * @author Aberic on 2018/10/7 16:12
 * @version 1.0
 * @see TroubleValueRWSet
 * @see TroubleTransaction
 * @since 1.0
 */
public class TroubleValueRead {

    /** 本次读取k-v中key */
    @JSONField(name = "k")
    private String key;

    public TroubleValueRead(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
