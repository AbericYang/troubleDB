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

import java.io.Serializable;
import java.util.Deque;

/**
 * n <====> tree level 0+
 * m <====> tree now level 0+
 * x <====> node count 1+
 * y <====> tree degree 0+ <====> x + 1
 * b <====> now degree 0+
 * z <====> now node index 0+
 * v <====> now full level degree 1+
 * <p>
 * calculate node value
 * <p>
 * real = (1 + z)(y^(m - 1)) + (v - 1)(y^m)
 * <p>
 * z = (real - (v - 1)(y^m))/(y^(m - 1)) - 1
 * <p>
 * int to real
 * <p>
 * real = (y^(m-1))int -(y^(m-1))(y^(n-m) - v)
 * <p>
 * calculate v by m
 * v = (int - x(y^?))/x - 1(?)
 *
 * @author Aberic on 2018/10/8 22:07
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
public class IntegerTreeMap<V> extends AbstractTMap<Integer, V> implements TMap<Integer, V>, Serializable {

    private static final long serialVersionUID = 8565247786674084606L;

    static final int hash(Object key) {
        if (key instanceof Integer) {
            return (Integer) key;
        }
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /** 当前结点范围对象的根对象，祖宗结点 */
    private NodeRange<Integer, V> root;

    /**
     * 指定范围对象中的所属子结点数组大小进行构造，构造中初始化一个顶级结点范围对象
     *
     * <p>顶级结点范围对象为所有结点范围对象的祖宗结点，
     * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行传入赋值或切割，
     * 直到切割数量达到{@code {@link NodeRange#NODE_ARRAY_LENGTH } + 1}，即满足分裂条件。
     * 当满足分裂条件后，{@code RangeTreeMap.NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
     *
     * <p>初始化时结点所属范围起始位置默认0，结点所属范围终止位置默认无穷大
     *
     * <p>持续分割会维持一个B+Tree的模型。
     */
    IntegerTreeMap() {
        root = new NodeRange<>();
    }

    @Override
    public int size() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Range<Integer, V> range() {
        return root;
    }

    static class NodeRange<K, V> extends Range<K, V> {

        NodeRange() {
        }

        NodeRange(int levelNow, int keyIndexInNode, int degreeForOneLevelNow) {
            super(levelNow, keyIndexInNode, degreeForOneLevelNow);
        }

        @Override
        V putExec(Deque<Integer> vDeque, int temV, Integer real, K key, V value, int m, int v) {
            NodeRange<K, V> p = this, c = this;
            int selfV;
            // System.out.println("p.start = " + p.start + " | p.end = " + p.end);
            while (null != vDeque.peek()) {
                temV = vDeque.pop();
                selfV = (temV - 1) - ((temV - 1) / TREE_MAX_DEGREE) * TREE_MAX_DEGREE;
//                System.out.println("temV out = " + temV + " | selfV out = " + selfV);
                c = (NodeRange<K, V>) p.nodeChildrenRanges[selfV];
                if (null == c) {
                    c = new NodeRange<>(p.levelNow - 1, 0, temV);
                    p.nodeChildrenRanges[selfV] = c;
                }
                p = c;
            }
            // z = (real - (v - 1)(y^m))/(y^(m - 1)) - 1
            int minV = (int) ((real - (v - 1) * Math.pow(TREE_MAX_DEGREE, m)) / Math.pow(TREE_MAX_DEGREE, m - 1) - 1);
//            System.out.println("y = " + TREE_MAX_DEGREE + " | m = " + m + " | n = " + TREE_MAX_LEVEL + " | v = " + v + " | minV = " + minV + " | key = " + key + " | real = " + real);
            Node<K, V> node = (Node<K, V>) c.nodes[minV];
            if (null == node) {
                node = new Node<>(real, value);
                c.nodes[minV] = node;
            } else {
                value = node.setValue(value);
            }
            return value;
        }

        @Override
        RangePair<K, V>[] nodes() {
            return nodes;
        }

        @Override
        Range<K, V>[] nodeChildrenRanges() {
            return nodeChildrenRanges;
        }

    }

    /**
     * 结点对象
     *
     * @author Aberic on 2018/10/7 15:25
     * @version 1.0
     * @see NodeRange
     * @since 1.0
     */
    static class Node<K, V> implements RangePair<K, V> {

        /** 传入key */
        final int hash;
        K key;
        /** 存储Value */
        V value;

        Node(int hash, V value) {
            this.hash = hash;
            this.value = value;
        }

        @Override
        public final K getKey() { return key; }

        @Override
        public final V getValue() { return value; }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

    }

}
