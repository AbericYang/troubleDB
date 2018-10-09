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
 * @see RangeTreeMap
 * @see BlockTreeMap
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
    boolean containsKey(Object key);

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
    V get(Object key);

    /**
     * 将指定的值与此映射中的指定键关联（可选操作）。
     * 如果此映射以前包含一个该键的映射关系，
     * 则用指定值替换旧值（当且仅当{@link #containsKey(Object) m.containsKey(k)}返回 <tt>true</tt> 时，
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
     * B-tree的层对象。
     *
     * <p>B-tree中每一个层对象{@code Range}都包含至少一个映射项{@link RangePair}或{@link BlockPair}，
     * 且两者内的泛型元素保持一致。
     *
     * @see RangeTreeMap
     * @see BlockTreeMap
     * @see RangeTreeMap.NodeRange
     * @see BlockTreeMap.NodeRange
     * @since 1.0
     */
    interface Range<K, V> {

        /**
         * 返回此映射中的键-值映射关系数。
         * 如果该映射包含的元素大于 <tt>Integer.MAX_VALUE</tt> ，则返回 <tt>Integer.MAX_VALUE</tt> 。
         *
         * @return 此映射中的键-值映射关系数
         */
        int size();

        /**
         * 如果Range包含指定的元素，则返回 true。
         * 更确切地讲，当且仅当Range包含满足 <tt>(key==null ? e==null : key.equals(e))</tt> 的元素 <tt>e</tt> 时返回 <tt>true</tt> 。
         *
         * @param key 要测试此Range中是否存在的元素
         * @return 如果此Range包含指定的元素，则返回<tt>true</tt>
         * @throws ClassCastException   如果指定元素的类型与此Range不兼容（可选）
         * @throws NullPointerException 如果指定的元素为null并且此Range不允许null元素（可选）
         */
        boolean contains(Object key);

        /**
         * 返回指定键所映射的值；如果此映射不包含该键的映射关系，则返回{@code null}。
         *
         * <p>更确切地讲，如果此映射包含满足 <tt>(key==null ? k==null : key.equals(k))</tt> 的键 <tt>k</tt> 到值 <tt>v</tt> 的映射关系，
         * 则此方法返回 <tt>v</tt> ；否则返回{@code null}。（最多只能有一个这样的映射关系）。
         *
         * <p>如果此映射允许{@code null}值，则返回{@code null}值并不一定表示该映射不包含该键的映射关系；
         * 也可能该映射将该键显示地映射到{@code null}。使用{@link #contains}操作可区分这两种情况。
         *
         * @param key 要返回其关联值的键
         * @return 指定键所映射的值；如果此映射不包含该键的映射关系，则返回{@code null}
         * @throws ClassCastException   如果该键对于此映射是不合适的类型（可选）
         * @throws NullPointerException 如果指定键为 null 并且此映射不允许 null 键（可选）
         */
        V get(Object key);

        /**
         * 将指定的值与此映射中的指定键关联（可选操作）。
         * 如果此映射以前包含一个该键的映射关系，
         * 则用指定值替换旧值（当且仅当{@link #contains(Object) m.contains(k)}返回 <tt>true</tt> 时，
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

    }

}
