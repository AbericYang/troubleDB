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

import cn.aberic.trouble.db.block.TroubleBlock;
import cn.aberic.trouble.db.file.Stroage;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author Aberic on 2018/10/08 15:31
 * @version 1.0
 * @see
 * @since 1.0
 */
public class HashBlockMap<K, V extends TroubleBlock> extends AbstractTMap<K, V> implements TMap<K, V>, Serializable {

    private static final long serialVersionUID = 6995005831834163349L;

    /** 构造顶级结点范围对象的默认所属子结点数组大小 */
    private static final int DEFAULT_TREE_RANGE_LENGTH = 4;

    /**
     * 指定构造顶级结点范围对象的所属子结点数组大小，如未在构造中使用此参数，
     * 则该值将会在无参构造的时候被赋值为{@link #DEFAULT_TREE_RANGE_LENGTH}
     */
    private int rangeLength;
    /** 当前结点范围对象的根对象，祖宗结点 */
    private HashRangeMap.NodeRange<K, V> root;

    /**
     * 根据默认范围对象中的所属子结点数组大小进行构造，具体参见{@link #HashBlockMap(int)}
     *
     * <p>默认大小{@link #DEFAULT_TREE_RANGE_LENGTH}
     */
    public HashBlockMap() {
        this(DEFAULT_TREE_RANGE_LENGTH);
    }

    /**
     * 指定范围对象中的所属子结点数组大小进行构造，构造中初始化一个顶级结点范围对象
     *
     * <p>顶级结点范围对象为所有结点范围对象的祖宗结点，
     * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行传入赋值或切割，
     * 直到切割数量达到{@code {@link HashRangeMap.NodeRange#length} + 1}，即满足分裂条件。
     * 当满足分裂条件后，{@code HashRangeMap.NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
     *
     * <p>初始化时结点所属范围起始位置默认0，结点所属范围终止位置默认无穷大
     *
     * <p>持续分割会维持一个B+Tree的模型。
     *
     * @param length 范围对象中的所属子结点数组大小
     */
    public HashBlockMap(int length) {
        if (length <= 0) {
            rangeLength = DEFAULT_TREE_RANGE_LENGTH;
        } else {
            rangeLength = length;
        }
        root = new HashRangeMap.NodeRange<>(rangeLength);
    }

    @Override
    public Range<K, V> range() {
        return root;
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
    static class Node<K, V extends TroubleBlock> implements BlockPair<K, V, NodeValue> {

        /** 存储key */
        final int hash;
        /** 传入key */
        final K key;
        /** 存储Value，此对象将被存储到本地文件系统中 */
        V value;
        /** 内存存储真实Value */
        NodeValue nodeValue;
        /** 上一个结点对象的Key，首结点默认上一结点为-1 */
        K preKey;
        /** 下一个结点对象的Key，末结点默认下一结点为-1 */
        K nextKey;

        Node(int hash, K key) {
            this.hash = hash;
            this.key = key;
        }

        @Override
        public final K getKey() { return key; }

        @Override
        public final V getValue() { return Stroage.get(nodeValue.getNum(), nodeValue.getLine()); }

        @Override
        public final NodeValue setValue(V newValue) {
            try {
                nodeValue = Stroage.save(newValue);
                return null;
            } catch (Exception e) {
                return null;
            }
        }

    }

    static class NodeRange<K, V extends TroubleBlock> implements Range<K, V> {

        /** 范围对象中的所属子结点数组大小 */
        private int length;
        /** 结点所属范围起始位置，默认0 */
        private int start = 0;
        /** 结点所属范围起始位置，默认无穷大 */
        private int end = -1;
        /** 结点数组，不可扩容 */
        private HashRangeMap.Node<K, V>[] nodes;
        /** 当前范围下的结点总数 */
        private int size;
        /** 当前结点范围的父对象，仅根可为null */
        private HashRangeMap.NodeRange parent;
        /** 当前结点范围的子对象数组，不可扩容 */
        private HashRangeMap.NodeRange[] children;

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
        NodeRange(int length, int start, int end, HashRangeMap.NodeRange nodeRange) {
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
        void init(int length, int start, int end, HashRangeMap.NodeRange nodeRange) {
            this.length = length;
            this.start = start;
            this.end = end;
            this.size = 0;
            parent = nodeRange;
            // 结点数组根据设定执行初始化
            nodes = (HashRangeMap.Node<K, V>[]) new HashRangeMap.Node[length];
            // 子range集合的数量必为结点集合的大小+1
            children = new HashRangeMap.NodeRange[length + 1];
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void setStart(int start) {
            if (parent == null) {
                throw new RuntimeException();
            }
            this.start = start;
        }

        @Override
        public void setEnd(int end) {
            this.end = end;
        }

        @Override
        public boolean inRange(int key) {
            return key >= start && key < end;
        }

        @Override
        public V get(Object key) {
            return null;
        }

        @Override
        public V put(K key, V value) {
            return putVal(hash(key), key, value);
        }

        final V putVal(int hash, K key, V value) {
            // TODO: 2018/10/7
            if (null == parent) { // 自身为顶级NodeRange

            } else { // 正常存储直到最底层指定顺序位

            }
            return null;
        }

        @Override
        public boolean contains(Object key) {
            return false;
        }

    }

    /**
     * 结点值对象
     * <p>
     * 结点{@link Node}的值对象，保存其所指引的具体内容所在文件及所在文件中的位置
     *
     * @author Aberic on 2018/10/7 22:05
     * @see Node
     * @see NodeRange
     * @since 1.0
     */
    public final static class NodeValue {

        /** 区块所在区块文件编号 */
        @JSONField(name = "n")
        private int num;
        /** 区块所在区块文件中的行号 */
        @JSONField(name = "l")
        private int line;

        public NodeValue(int num, int line) {
            this.num = num;
            this.line = line;
        }

        public int getNum() {
            return num;
        }

        public int getLine() {
            return line;
        }
    }

}
