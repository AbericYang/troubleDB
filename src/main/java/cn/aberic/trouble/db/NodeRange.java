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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 结点间范围对象
 *
 * @author Aberic on 2018/10/7 15:15
 * @version 1.0
 * @see Node
 * @since 1.0
 */
public class NodeRange<K, V extends TroubleBlock> implements Map<K, V>, Serializable {

    private static final long serialVersionUID = 1026284682140028531L;

    /** 范围对象中的所属子结点数组大小 */
    private int length;
    /** 结点所属范围起始位置，默认0 */
    private int start = 0;
    /** 结点所属范围起始位置，默认无穷大 */
    private int end = -1;
    /** 结点数组，不可扩容 */
    private Node<K>[] nodes;
    /** 当前范围下的结点总数 */
    private int size;
    /** 当前结点范围的父对象，仅根可为null */
    private NodeRange parent;
    /** 当前结点范围的子对象数组，不可扩容 */
    private NodeRange[] children;

    /**
     * 结点间范围对象构造
     *
     * @param length    范围对象中的所属结点数组大小
     * @param start     结点所属范围起始位置，初始默认0
     * @param end       结点所属范围起始位置，初始默认无穷大
     * @param nodeRange 当前结点范围对象的父对象，仅根可为null
     */
    @SuppressWarnings("unchecked")
    public NodeRange(int length, int start, int end, NodeRange nodeRange) {
        this.length = length;
        this.start = start;
        this.end = end;
        this.size = 0;
        parent = nodeRange;
        // 结点数组根据设定执行初始化
        nodes = (Node<K>[]) new Node[length];
        // 子range集合的数量必为结点集合的大小+1
        children = new NodeRange[length + 1];
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * 判断key是否在此结点范围内
     *
     * @param key key的整型值
     *
     * @return 与否
     */
    public boolean inRange(int key) {
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
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

}
