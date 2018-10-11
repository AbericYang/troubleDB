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
 * 将键映射到值的对象。一个映射不能包含重复的键；每个键最多只能映射到一个值。
 *
 * @author Aberic on 2018/10/08 15:38
 * @version 1.0
 * @see AbstractTMap
 * @see // RangeTreeMap
 * @see // BlockTreeMap
 * @since 1.0
 */
public interface TMap<K, V> {

    /**
     * 返回此映射中的键-值映射关系数。
     * 如果该映射包含的元素大于 <tt>Integer.MAX_VALUE</tt> ，则返回 <tt>Integer.MAX_VALUE</tt> 。
     *
     * @return 此映射中的键-值映射关系数
     */
    int size();

    /**
     * 如果此映射未包含键-值映射关系，则返回 <tt>true</tt> 。
     *
     * @return 如果此映射未包含键-值映射关系，则返回 <tt>true</tt>
     */
    boolean isEmpty();

    /**
     * 如果此映射包含指定键的映射关系，则返回 <tt>true</tt> 。
     * 更确切地讲，当且仅当此映射包含针对满足 <tt>(key==null ? k==null : key.equals(k))</tt> 的键 <tt>k</tt> 的映射关系时，返回 true。
     * （最多只能有一个这样的映射关系）。
     *
     * @param key 测试是否存在于此映射中的键
     * @return 如果此映射包含指定键的映射关系，则返回 <tt>true</tt>
     * @throws ClassCastException   如果该键对于此映射是不合适的类型（可选）
     * @throws NullPointerException 如果指定键为 null 并且此映射不允许 null 键（可选）
     */
    boolean containsKey(K key);

    /**
     * 返回指定键所映射的值；如果此映射不包含该键的映射关系，则返回{@code null}。
     *
     * <p>更确切地讲，如果此映射包含满足 <tt>(key==null ? k==null : key.equals(k))</tt> 的键 <tt>k</tt> 到值 <tt>v</tt> 的映射关系，
     * 则此方法返回 <tt>v</tt> ；否则返回{@code null}。（最多只能有一个这样的映射关系）。
     *
     * <p>如果此映射允许{@code null}值，则返回{@code null}值并不一定表示该映射不包含该键的映射关系；
     * 也可能该映射将该键显示地映射到{@code null}。使用{@link #containsKey}操作可区分这两种情况。
     *
     * @param key 要返回其关联值的键
     * @return 指定键所映射的值；如果此映射不包含该键的映射关系，则返回{@code null}
     * @throws ClassCastException   如果该键对于此映射是不合适的类型（可选）
     * @throws NullPointerException 如果指定键为 null 并且此映射不允许 null 键（可选）
     */
    V get(K key);

    /**
     * 将指定的值与此映射中的指定键关联（可选操作）。
     * 如果此映射以前包含一个该键的映射关系，
     * 则用指定值替换旧值（当且仅当{@link #containsKey(K) m.containsKey(k)}返回 <tt>true</tt> 时，
     * 才能说映射 <tt>m</tt> 包含键 <tt>k</tt> 的映射关系）。
     *
     * @param key   与指定值关联的键
     * @param value 与指定键关联的值
     * @return 以前与 <tt>key</tt> 关联的值，如果没有针对 <tt>key</tt> 的映射关系，则返回 <tt>null</tt> 。
     * （如果该实现支持 <tt>null</tt> 值，则返回 <tt>null</tt> 也可能表示此映射以前将 <tt>null</tt> 与 <tt>key</tt> 关联）
     * @throws UnsupportedOperationException 如果此映射不支持 <tt>put</tt> 操作
     * @throws ClassCastException            如果指定键或值的类不允许将其存储在此映射中
     * @throws NullPointerException          如果指定键或值为 <tt>null</tt> ，并且此映射不允许 <tt>null</tt> 键或值
     * @throws IllegalArgumentException      如果指定键或值的某些属性不允许将其存储在此映射中
     */
    V put(K key, V value);

//    /**
//     * 从指定映射中将所有映射关系复制到此映射中（可选操作）。
//     * 对于指定映射中的每个键 <tt>k</tt> 到值 <tt>v</tt> 的映射关系，
//     * 此调用等效于对此映射调用一次{@link #put(Object, Object) put(k, v)}。
//     * 如果正在进行此操作的同时修改了指定的映射，则此操作的行为是不确定的。
//     *
//     * @param m 要存储在此映射中的映射关系
//     * @throws UnsupportedOperationException 如果此映射不支持 <tt>putAll</tt> 操作
//     * @throws ClassCastException            如果指定键或值的类不允许将其存储在此映射中
//     * @throws NullPointerException          如果指定键或值为 <tt>null</tt> ，并且此映射不允许 <tt>null</tt> 键或值
//     * @throws IllegalArgumentException      如果指定键或值的某些属性不允许将其存储在此映射中
//     */
//    void putAll(RangeMap<? extends K, ? extends V> m);

    /**
     * 映射项（键-值对）。
     * <tt>RangeTreeMap.range</tt> 方法返回属于此类元素的映射视图。
     *
     * @see //RangeTreeMap#range()
     * @see //RangeTreeMap
     * @see //RangeTreeMap.Node
     * @since 1.0
     */
    interface RangePair<K, V> {

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
         * @param v 要存储在此项中的新值
         * @return 与此项对应的旧值
         * @throws UnsupportedOperationException 如果此映射不支持 <tt>put</tt> 操作
         * @throws ClassCastException            如果指定值的类不允许将其存储在底层映射中
         * @throws NullPointerException          如果底层映射不允许值为 <tt>null</tt> ，并且指定的值为 <tt>null</tt>
         * @throws IllegalArgumentException      如果此值的某些方面不允许将其存储在底层映射中
         * @throws IllegalStateException         如果已经从底层映射中移除了该项，则实现可以，但不要求，抛出此异常
         */
        V setValue(V v);

    }

}
