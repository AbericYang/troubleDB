///*
// * MIT License
// *
// * Copyright (c) 2018 Aberic Yang
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package cn.aberic.trouble.db.util;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.Serializable;
//
///**
// * 基于B-Tree的 <tt>TMap</tt> 的接口实现。此实现提供所有可选的映射操作，并允许使用 <tt>null</tt> 值和 <tt>null</tt> 键，但不推荐。
// * （除了此类的 <tt>key</tt> 被存入内存，<tt>RangeTreeMap</tt> 类与<tt>BlockTreeMap</tt> 大致相同。）
// *
// * <p>理论上此类不保证映射的顺序，特别是它不保证该顺序恒久不变。
// * 实际实现中，此类在做映射操作的时候，会将传入 <tt>key</tt> 的 <tt>hash</tt> 按照从小到大的顺序做一个类双向链表的方式排列，
// * 可以非常快速的找到第一个和最后一个结点，同时可以根据第一个和最后一个结点进行有序迭代。
// * 注意：这个有序取决于 <tt>key</tt> 的 <tt>hash</tt> 值。
// * 因此，如果自定义 <tt>key</tt> ，并且 <tt>key</tt> 是 <tt>Integer/int</tt> 类型，且一个从1开始一直到{@link Integer#MAX_VALUE}，中间允许有间断，
// * 那么 <tt>RangeTreeMap</tt> 在这种情况下，可以被看做是一个有序表。
// * 并且从1开始进行有序的不间断的传入将会使<tt>IntegerTreeMap</tt> 类效率达到最高。
// *
// * @author Aberic on 2018/10/08 12:04
// * @version 1.0
// * @see HashRangeMap
// * @since 1.0
// */
//@Slf4j
//public class RangeTreeMap<K, V> extends AbstractTMap<K, V> implements TMap<K, V>, Serializable {
//
//    private static final long serialVersionUID = 5066322126366247567L;
//
//    /** 指定构造顶级结点范围对象的所属子结点数组大小 */
//    private int rangeLength;
//    /** 当前结点范围对象的根对象，祖宗结点 */
//    private NodeRange<K, V> root;
//
//    /**
//     * 指定范围对象中的所属子结点数组大小进行构造，构造中初始化一个顶级结点范围对象
//     *
//     * <p>顶级结点范围对象为所有结点范围对象的祖宗结点，
//     * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行传入赋值或切割，
//     * 直到切割数量达到{@code {@link NodeRange#nodeArrayLength } + 1}，即满足分裂条件。
//     * 当满足分裂条件后，{@code RangeTreeMap.NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
//     *
//     * <p>初始化时结点所属范围起始位置默认0，结点所属范围终止位置默认无穷大
//     *
//     * <p>持续分割会维持一个B+Tree的模型。
//     *
//     * @param length 范围对象中的所属子结点数组大小
//     */
//    public RangeTreeMap(int length) {
//        rangeLength = length;
//        root = new NodeRange<>(rangeLength);
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     * @return {@inheritDoc}
//     */
//    @Override
//    public Range<K, V> range() {
//        return root;
//    }
//
//    static class NodeRange<K, V> implements Range<K, V> {
//
//        /** 结点范围对象中的所属子结点数组大小 */
//        private int nodeArrayLength;
//        /** B-Tree的最大度，即结点范围结点拥有子树的数目 */
//        private int maxDegree;
//        /** B-Tree的层 */
//        private int level;
//        /** 结点所属范围起始位置，默认0 */
//        private int start = 0;
//        /** 结点所属范围起始位置，默认无穷大 */
//        private int end = -1;
//        /** 结点数组，不可扩容 */
//        private Node<K, V>[] nodes;
//        /** 当前范围下的结点总数 */
//        private int size;
//        /** 当前范围下的子对象数组总数 */
//        private int length;
//        /** 当前结点范围的父对象，仅根可为null */
//        private NodeRange parent;
//        /** 当前结点范围的子对象数组，不可扩容 */
//        private NodeRange<K, V>[] nodeChildrenRanges;
//
//        /**
//         * 指定范围对象中的所属子结点数组大小进行构造，构造结果为顶级/虚结点范围对象
//         *
//         * <p>顶级/虚结点范围对象为所有结点范围对象的祖宗，它没有任何实际数据可操作意义，
//         * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行切割，
//         * 直到切割数量达到({@code NodeRange#nodeArrayLength} + 1)，即满足分裂条件。
//         * 当满足分裂条件后，{@code NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
//         *
//         * <p>持续分割会维持一个B+Tree的模型。
//         *
//         * @param nodeArrayLength 范围对象中的所属子结点数组大小
//         */
//        public NodeRange(int nodeArrayLength) {
//            this.nodeArrayLength = nodeArrayLength;
//            init(nodeArrayLength, 0, Integer.MAX_VALUE, null);
//        }
//
//        /**
//         * 内部使用结点间范围对象构造
//         *
//         * @param nodeArrayLength 范围对象中的所属结点数组大小
//         * @param start           结点所属范围起始位置，初始默认0
//         * @param end             结点所属范围起始位置，初始默认无穷大
//         * @param nodeRange       当前结点范围对象的父对象，仅根可为null
//         */
//        NodeRange(int nodeArrayLength, int start, int end, NodeRange nodeRange) {
//            init(nodeArrayLength, start, end, nodeRange);
//        }
//
//        /**
//         * 结点间范围对象初始化
//         *
//         * @param nodeArrayLength 范围对象中的所属结点数组大小
//         * @param start           结点所属范围起始位置，初始默认0
//         * @param end             结点所属范围起始位置，初始默认无穷大
//         * @param nodeRange       当前结点范围对象的父对象，仅根可为null
//         */
//        @SuppressWarnings("unchecked")
//        void init(int nodeArrayLength, int start, int end, NodeRange nodeRange) {
//            this.nodeArrayLength = nodeArrayLength;
//            this.start = start;
//            this.end = end;
//            this.size = 0;
//            this.length = 0;
//            parent = nodeRange;
//            // 结点数组根据设定执行初始化
//            nodes = (Node<K, V>[]) new Node[nodeArrayLength];
//            // B-Tree的最大度/子range集合的数量必为结点集合的大小+1
//            maxDegree = nodeArrayLength + 1;
//            nodeChildrenRanges = new NodeRange[maxDegree];
//        }
//
//        @Override
//        public int size() {
//            return size;
//        }
//
//        @Override
//        public void setStart(int start) {
//            if (parent == null) {
//                throw new RuntimeException();
//            }
//            this.start = start;
//        }
//
//        @Override
//        public void setEnd(int end) {
//            this.end = end;
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @param key {@inheritDoc}
//         *
//         * @return {@inheritDoc}
//         *
//         * @throws IndexOutOfBoundsException {@inheritDoc}
//         */
//        @Override
//        public boolean inRange(int key) {
//            return key >= start && key < end;
//        }
//
//        @Override
//        public V get(Object key) {
//            return null;
//        }
//
//        @Override
//        public V put(K key, V value) {
//            return putVal(hash(key), key, value);
//        }
//
//        final V putVal(int hash, K key, V value) {
//            if (size >= 0 && size < nodeArrayLength) { // 如果子结点范围不足分裂
//                int start = -1; // 新起点的临时变量
//                for (int i = 1; i < length; i++) {
//                    if (start != -1) { // 如果新起点发生改变
//                        nodeChildrenRanges[i].setEnd(nodeChildrenRanges[i].start); // 将结点范围原来的起点变成现在的终点
//                        nodeChildrenRanges[i].setStart(start); // 重新设置结点范围的新起点
//                        // 新起点的临时变量被赋值为结点范围的终点
//                        start = nodeChildrenRanges[i].end;
//                    } else if (nodeChildrenRanges[i].inRange(hash)) { // 如果hash在当前范围内
//                        nodeChildrenRanges[i].setEnd(hash); // 当前结点范围的终点被重新赋值
//                        start = hash; // 新起点临时变量产生新值
//                    }
//                }
//                if (start != -1) { // 如果新起点发生改变，则说明原来已经存在若干子结点范围对象
//                    NodeRange<K, V> range = new NodeRange<>(maxDegree, start, Integer.MAX_VALUE, this);
//                    nodeChildrenRanges[length] = range;
//                    length++;
//                } else { // 如果新起点没有发生改变，则说明当前没有任何子结点范围对象
//                    NodeRange<K, V> range1 = new NodeRange<>(maxDegree, 0, hash, this);
//                    NodeRange<K, V> range2 = new NodeRange<>(maxDegree, hash, Integer.MAX_VALUE, this);
//                    nodeChildrenRanges[0] = range1;
//                    nodeChildrenRanges[1] = range2;
//                    length += 2;
//                }
//                Node<K, V> node = new Node<>(hash, key, value); // 定义新结点
//                nodes[size] = node; // 赋值新结点到结点数组最后位置
//                size++; // 结点数+1
//                return value;
//            } else { // 需要将新结点存入子结点范围集合中
//                for (NodeRange<K, V> range : nodeChildrenRanges) {
//                    if (range.inRange(hash) && range.size > 0) {
//                        return range.putVal(hash, key, value);
//                    }
//                }
//            }
//            return null;
//        }
//
//        @Override
//        public boolean contains(Object key) {
//            return false;
//        }
//
//    }
//
//    /**
//     * 结点对象
//     *
//     * @author Aberic on 2018/10/7 15:25
//     * @version 1.0
//     * @see NodeRange
//     * @since 1.0
//     */
//    static class Node<K, V> implements RangePair<K, V> {
//
//        /** 存储key */
//        final int hash;
//        /** 传入key */
//        final K key;
//        /** 存储Value */
//        V value;
//        /** 上一个结点对象的Key，首结点默认上一结点为-1 */
//        K preKey;
//        /** 下一个结点对象的Key，末结点默认下一结点为-1 */
//        K nextKey;
//
//        Node(int hash, K key, V value) {
//            this.hash = hash;
//            this.key = key;
//        }
//
//        @Override
//        public final K getKey() { return key; }
//
//        @Override
//        public final V getValue() { return value; }
//
//        @Override
//        public final V setValue(V newValue) {
//            V oldValue = value;
//            value = newValue;
//            return oldValue;
//        }
//
//    }
//
//}
