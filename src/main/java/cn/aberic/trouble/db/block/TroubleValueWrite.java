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
 * <p>交易中一笔操作真实写入的键值对象
 *
 * <p>每一笔交易{@link TroubleTransaction}都会触发至少一次写入操作，每一次的写入操作都会执行如put类似Map的方法，
 * 每一个{@code TroubleValueWrite}都会将当前所属交易的写入结果记录下来，
 * 并置入本次交易读写集{@link TroubleTransaction#rwSet}的写集{@link TroubleValueRWSet#writes}中存放。
 * 写集数据可作为最终区块进行本地同步时候的一种验证策略。
 *
 * @author Aberic on 2018/10/7 16:04
 * @version 1.0
 * @see TroubleValueRWSet
 * @see TroubleTransaction
 * @since 1.0
 */
public class TroubleValueWrite {

    @JSONField(name = "k")
    private String key;
    @JSONField(name = "v")
    private String value;

    public TroubleValueWrite(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
