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

package cn.aberic.trouble.db.util;

/**
 * 映射项（键-值对）。
 * <tt>BlockTreeMap.range</tt> 方法返回属于此类元素的映射视图。
 *
 * @see BlockTreeMap#range()
 * @see BlockTreeMap
 * @see BlockTreeMap.Node
 * @since 1.0
 */
public interface BlockPair<K, V, R> {

    /**
     * 返回与此项对应的键
     *
     * @return 与此项对应的键
     * @throws IllegalStateException 如果已经从底层映射中移除了该项，则实现可以（但不要求）抛出此异常。
     */
    K getKey();

    /**
     * 返回与此项对应的值。如果已经从底层映射中移除了映射关系，则此调用的结果是不确定的。
     *
     * @return 与此项对应的值
     * @throws IllegalStateException 如果已经从底层映射中移除了该项，则实现可以（但不要求）抛出此异常。
     */
    V getValue();

    /**
     * 用指定的值替换与此项对应的值（可选操作）。
     * 写入该映射，如果已经从映射中移除了映射关系，则此调用的行为是不确定的。
     *
     * @param v 要存储在对应的映射本地文件中的新实体序列化对象
     * @return 与此项对应的映射的存储结果值
     * @throws UnsupportedOperationException 如果此映射不支持 <tt>put</tt> 操作
     * @throws ClassCastException            如果指定值的类不允许将其存储在底层映射中
     * @throws NullPointerException          如果底层映射不允许值为 <tt>null</tt> ，并且指定的值为 <tt>null</tt>
     * @throws IllegalArgumentException      如果此值的某些方面不允许将其存储在底层映射中
     * @throws IllegalStateException         如果已经从底层映射中移除了该项，则实现可以，但不要求，抛出此异常
     */
    R setValue(V v);

}
