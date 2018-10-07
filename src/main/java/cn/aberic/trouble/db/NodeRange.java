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

package cn.aberic.trouble.db;

import cn.aberic.trouble.db.block.TroubleBlock;
import cn.aberic.trouble.db.file.Stroage;

import java.io.Serializable;

/**
 * 结点范围对象
 *
 * @author Aberic on 2018/10/7 15:15
 * @version 1.0
 * @see Node
 * @since 1.0
 */
class NodeRange<K, V extends TroubleBlock> implements Range<K, V>, Serializable {

    private static final long serialVersionUID = 1026284682140028531L;

    /** 范围对象中的所属子结点数组大小 */
    private int length;
    /** 结点所属范围起始位置，默认0 */
    private int start = 0;
    /** 结点所属范围起始位置，默认无穷大 */
    private int end = -1;
    /** 结点数组，不可扩容 */
    private Node<K, V>[] nodes;
    /** 当前范围下的结点总数 */
    private int size;
    /** 当前结点范围的父对象，仅根可为null */
    private NodeRange parent;
    /** 当前结点范围的子对象数组，不可扩容 */
    private NodeRange[] children;

    private static final int DEFAULT_TREE_RANGE_LENGTH = 8;

    /**
     * 根据默认范围对象中的所属子结点数组大小进行构造，构造结果为顶级/虚结点范围对象
     *
     * <p>默认大小{@link #DEFAULT_TREE_RANGE_LENGTH}
     */
    public NodeRange() {
        this(DEFAULT_TREE_RANGE_LENGTH);
    }

    /**
     * 指定范围对象中的所属子结点数组大小进行构造，构造结果为顶级/虚结点范围对象
     *
     * <p>顶级/虚结点范围对象为所有结点范围对象的祖宗，它没有任何实际数据可操作意义，
     * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行切割，
     * 直到切割数量达到({@code NodeRange#length} + 1)，即满足分裂条件。
     * 当满足分裂条件后，{@code NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
     *
     * <p>持续分割会维持一个B+Tree的模型。
     *
     * @param length 范围对象中的所属子结点数组大小
     */
    public NodeRange(int length) {
        this.length = length;
        init(length, 0, -1, null);
    }

    /**
     * 内部使用结点间范围对象构造
     *
     * @param length    范围对象中的所属结点数组大小
     * @param start     结点所属范围起始位置，初始默认0
     * @param end       结点所属范围起始位置，初始默认无穷大
     * @param nodeRange 当前结点范围对象的父对象，仅根可为null
     */
    NodeRange(int length, int start, int end, NodeRange nodeRange) {
        init(length, start, end, nodeRange);
    }

    /**
     * 结点间范围对象初始化
     *
     * @param length    范围对象中的所属结点数组大小
     * @param start     结点所属范围起始位置，初始默认0
     * @param end       结点所属范围起始位置，初始默认无穷大
     * @param nodeRange 当前结点范围对象的父对象，仅根可为null
     */
    @SuppressWarnings("unchecked")
    void init(int length, int start, int end, NodeRange nodeRange) {
        this.length = length;
        this.start = start;
        this.end = end;
        this.size = 0;
        parent = nodeRange;
        // 结点数组根据设定执行初始化
        nodes = (Node<K, V>[]) new Node[length];
        // 子range集合的数量必为结点集合的大小+1
        children = new NodeRange[length + 1];
    }

    void setStart(int start) {
        if (parent == null) {
            throw new RuntimeException();
        }
        this.start = start;
    }

    void setEnd(int end) {
        this.end = end;
    }

    /**
     * 判断key是否在此结点范围内
     *
     * @param key key的整型值
     *
     * @return 与否
     */
    boolean inRange(int key) {
        return key >= start && key < end;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public NodeValue put(K key, V value) {
        return putVal(hash(key), key, value);
    }

    final NodeValue putVal(int hash, K key, V value) {
        // TODO: 2018/10/7
        if (null == parent) { // 自身为顶级NodeRange

        } else { // 正常存储直到最底层指定顺序位

        }
        return null;
    }

    @Override
    public void putAll(Range<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 结点对象
     *
     * @author Aberic on 2018/10/7 15:25
     * @version 1.0
     * @see NodeValue
     * @since 1.0
     */
    static class Node<K, V extends TroubleBlock> {

        /** 存储key */
        final int hash;
        /** 传入key */
        final K key;
        /** 存储Value */
        NodeValue nodeValue;

        Node(int hash, K key) {
            this.hash = hash;
            this.key = key;
        }

        public final K getKey() { return key; }

        public final V getValue() { return Stroage.get(nodeValue.getNum(), nodeValue.getLine()); }

        public final boolean saveValue(V v) {
            try {
                nodeValue = Stroage.save(v);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

}
