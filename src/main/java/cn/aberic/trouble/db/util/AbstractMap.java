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
 * 此类提供{@link Map}接口的骨干实现，以最大限度地减少实现此接口所需的工作。
 *
 * @author Aberic on 2018/10/08 09:50
 * @version 1.0
 * @see TTreeMap
 * @since 1.0
 */
public abstract class AbstractMap<K, V> implements Map<K, V> {

    /**
     * 唯一的构造方法。（由子类构造方法调用，通常是隐式的。）
     */
    AbstractMap() {}

    /**
     * {@inheritDoc}
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @implSpec 此实现在 <tt>range()</tt> 中返回结果，以N分法搜索带有指定键的条目。
     * 如果找到这样的条目，则返回 <tt>true</tt> 。
     * 如果分裂终止，并且没有找到这样的条目，则返回 <tt>false</tt> 。
     *
     * <p>注意，此实现所需的时间与{@code #get(Object)}方法所需时间相比有两种情况，
     * 1、如果是值映射入内存，则与{@code #get(Object)}方法时间一致。
     * 2、如果是区块内容查询，则值被映射到本地磁盘中，则比{@code #get(Object)}方法所花费的时间要更短。
     */
    @Override
    public boolean containsKey(int storeHash, K key) { return range().contains(storeHash, key); }

    /**
     * {@inheritDoc}
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @implSpec 此实现在 <tt>range()</tt> 中返回结果，以N分法搜索带有指定键的条目。
     * 如果找到这样的条目，则返回该条目的值。
     * 如果分裂终止，并且没有找到这样的条目，则返回 <tt>null</tt> 。
     *
     * <p>注意，此实现所需的时间与{@code #containsKey(Object)}方法所需时间相比有两种情况，
     * 1、如果是值映射入内存，则与{@code #containsKey(Object)}方法时间一致。
     * 2、如果是区块内容查询，则值被映射到本地磁盘中，则比{@code #containsKey(Object)}方法所花费的时间要更长。
     */
    @Override
    public V get(int storeHash, K key) {
        return range().get(storeHash, key);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    @Override
    public V put(int storeHash, K key, V value) {
        return range().put(storeHash, key, value);
    }

    /**
     * 返回此映射中包含的映射关系的{@link Range}视图。
     * 该Range受映射支持，所以对映射的更改可在此Range中反映出来，反之亦然。
     *
     * @return 此映射中包含的映射关系的Range视图
     */
    public abstract Range<K, V> range();

}
