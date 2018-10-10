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
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

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

    /** 当前结点范围对象的根对象，祖宗结点 */
    private NodeRange<V> root;

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
    public IntegerTreeMap() {
        root = new NodeRange<>();
    }

    @Override
    public V get(Integer key) {
        return super.get(real(key));
    }

    @Override
    public boolean containsKey(Integer key) {
        return super.containsKey(real(key));
    }

    private int real(Integer key) {
        int m = root.calculateLevelNow(key); // 当前结点范围对象所在B-Tree的层
        int v = root.calculateDegreeForOneLevelNow(key, m); // 当前结点范围对象在整层度中的顺序位置
        return root.calculateReal(key, m, v); // 当前key在B-Tree中的真实数字
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

    static class NodeRange<V> implements Range<Integer, V> {

        /** B-Tree的层 - n */
        private final static int TREE_MAX_LEVEL = 3;
        /** 结点范围对象中的所属子结点数组大小 - x */
//        private final static int NODE_ARRAY_LENGTH = 3;
        private final static int NODE_ARRAY_LENGTH = 100;
        final static int TREE_MAX_LENGTH = 1030300;
        /** B-Tree的最大度，即结点范围结点拥有子树的数目 - y */
        private final static int TREE_MAX_DEGREE = NODE_ARRAY_LENGTH + 1;
        /** key在当前结点内的下标 - z */
        private int keyIndexInNode = 0;
        /** 当前结点范围对象在整层度中的顺序位置 - v */
        private int degreeForOneLevelNow = 1;
        /** 每一层的末尾终结位置 */
        private static int[] levelEveryRangeLastIndexArray;
        /** 当前结点范围对象所在B-Tree的层（节点默认值=4） - m */
        private int levelNow;
        /** y^m */
        private int yPowM;
        /** y^(m-1) */
        private int yPowM1;
        /** 结点数组首个对象下必须对应的数字 */
        private int firstNodeNum;
        /** 结点数组，不可扩容 */
        private Node<V>[] nodes;
        /** 当前结点范围的子对象数组，不可扩容 */
        private NodeRange<V>[] nodeChildrenRanges;

        static void initLevelEveryRangeLastIndex() {
            levelEveryRangeLastIndexArray = new int[TREE_MAX_LEVEL + 1];
            levelEveryRangeLastIndexArray[0] = 0;
            for (int i = 1; i <= TREE_MAX_LEVEL; i++) {
                levelEveryRangeLastIndexArray[i] = NODE_ARRAY_LENGTH * (int) Math.pow(TREE_MAX_DEGREE, i - 1) + levelEveryRangeLastIndexArray[i - 1];
            }
            System.out.println("levelEveryRangeLastIndexArray = " + Arrays.toString(levelEveryRangeLastIndexArray));
        }

        /**
         * 指定范围对象中的所属子结点数组大小进行构造，构造结果为顶级/虚结点范围对象
         *
         * <p>顶级/虚结点范围对象为所有结点范围对象的祖宗，它没有任何实际数据可操作意义，
         * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行切割，
         * 直到切割数量达到({@code NodeRange#NODE_ARRAY_LENGTH} + 1)，即满足分裂条件。
         * 当满足分裂条件后，{@code NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
         *
         * <p>持续分割会维持一个B-Tree的模型。
         */
        NodeRange() {
            initLevelEveryRangeLastIndex();
            init(TREE_MAX_LEVEL);
        }

        /**
         * 内部使用结点间范围对象构造
         *
         * @param levelNow             当前结点范围对象所在B-Tree的层（节点默认值=4） - m
         * @param keyIndexInNode       key在当前结点内的下标 - z
         * @param degreeForOneLevelNow 当前结点范围对象在整层度中的顺序位置 - v
         */
        private NodeRange(int levelNow, int keyIndexInNode, int degreeForOneLevelNow) {
            this.keyIndexInNode = keyIndexInNode;
            this.degreeForOneLevelNow = degreeForOneLevelNow;
            init(levelNow);
        }

        /**
         * 结点间范围对象初始化
         */
        @SuppressWarnings("unchecked")
        void init(int levelNow) {
            this.levelNow = levelNow;
            this.yPowM = (int) Math.pow(TREE_MAX_DEGREE, this.levelNow);
            this.yPowM1 = (int) Math.pow(TREE_MAX_DEGREE, this.levelNow - 1);
            // B-Tree的最大度/子range集合的数量必为结点集合的大小+1
            nodeChildrenRanges = new NodeRange[TREE_MAX_DEGREE];
            // 结点数组根据设定执行初始化
            nodes = new Node[NODE_ARRAY_LENGTH];
            // real = (1 + z)(y^(m - 1)) + (v - 1)(y^m)
            firstNodeNum = (1 + keyIndexInNode) * yPowM1 + (degreeForOneLevelNow - 1) * yPowM;
//            System.out.println("firstNodeNum = " + firstNodeNum);
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public V get(Integer key) {
            return getVByKey(this, key);
        }

        private V getVByKey(NodeRange<V> range, int real) {
            int gap = real - firstNodeNum;
            int index;
            if (gap < 0) { // 为子范围集合中首个
                index = 0;
            } else {
                int yPowM1 = range.yPowM1;
                index = gap / yPowM1;
                if (gap % yPowM1 == 0) { // 为子结点集合其中之一
                    Node<V> node = nodes[index];
                    return null != node ? node.getValue() : null;
                } else { // 为子范围集合中首个以外的其中之一
                    index += 1;
                }
            }
            NodeRange<V> rangeNext = range.nodeChildrenRanges[index];
            return null != rangeNext ? rangeNext.getVByKey(rangeNext, real) : null;
        }

        @Override
        public V put(Integer key, V value) {
            int m = calculateLevelNow(key); // 当前结点范围对象所在B-Tree的层
            int v = calculateDegreeForOneLevelNow(key, m); // 当前结点范围对象在整层度中的顺序位置
            int real = calculateReal(key, m, v); // 当前key在B-Tree中的真实数字
            return putReal(real, value, m, v);
        }

        private V putReal(Integer real, V value, int m, int v) {
            // 新建一个栈，用于存放自下而上每一父结点范围对象在整层度中的顺序位置
            Deque<Integer> vDeque = new LinkedList<>();
            vDeque.push(v); // 先把自己push到最底层
            int temV = v;
            for (int i = m; i < TREE_MAX_LEVEL; i++) { // 从下至上开始push当前值的层层结点范围对象所在整层度中的顺序位置
                temV = (temV - 1) / TREE_MAX_DEGREE + 1;
//                System.out.println("temV in = " + temV);
                vDeque.push(temV);
            }
            NodeRange<V> p = this, c = this;
            int selfV;
            vDeque.pop();
            // System.out.println("p.start = " + p.start + " | p.end = " + p.end);
            while (null != vDeque.peek()) {
                temV = vDeque.pop();
                selfV = (temV - 1) - ((temV - 1) / TREE_MAX_DEGREE) * TREE_MAX_DEGREE;
//                System.out.println("temV out = " + temV + " | selfV out = " + selfV);
                c = p.nodeChildrenRanges[selfV];
                if (null == c) {
                    c = new NodeRange<>(p.levelNow - 1, 0, temV);
                    p.nodeChildrenRanges[selfV] = c;
                }
                p = c;
            }
            // z = (real - (v - 1)(y^m))/(y^(m - 1)) - 1
            int minV = (int) ((real - (v - 1) * Math.pow(TREE_MAX_DEGREE, m)) / Math.pow(TREE_MAX_DEGREE, m - 1) - 1);
//            System.out.println("y = " + TREE_MAX_DEGREE + " | m = " + m + " | n = " + TREE_MAX_LEVEL + " | v = " + v + " | minV = " + minV + " | key = " + key + " | real = " + real);
            Node<V> node = c.nodes[minV];
            if (null == node) {
                node = new Node<>(real, value);
                c.nodes[minV] = node;
            } else {
                value = node.setValue(value);
            }
            return value;
        }

        @Override
        public boolean contains(Integer key) {
            return containsByKey(this, key);
        }

        private boolean containsByKey(NodeRange<V> range, int real) {
            int gap = real - firstNodeNum;
            int index;
            if (gap < 0) { // 为子范围集合中首个
                index = 0;
            } else {
                int yPowM1 = range.yPowM1;
                index = gap / yPowM1;
                if (gap % yPowM1 == 0) { // 为子结点集合其中之一
                    Node<V> node = nodes[index];
                    return null != node;
                } else { // 为子范围集合中首个以外的其中之一
                    index += 1;
                }
            }
            NodeRange<V> rangeNext = range.nodeChildrenRanges[index];
            return null != rangeNext && rangeNext.containsByKey(rangeNext, real);
        }

        /**
         * 计算当前传入key所在的层 - m
         *
         * @param key key
         *
         * @return 所在层
         */
        private int calculateLevelNow(int key) {
            for (int i = 0; i < TREE_MAX_LEVEL; i++) {
                if (key > levelEveryRangeLastIndexArray[i] && key <= levelEveryRangeLastIndexArray[i + 1]) {
                    return TREE_MAX_LEVEL - i;
                }
            }
            throw new RuntimeException();
        }

        /**
         * 计算当前结点范围对象在整层度中的顺序位置 - v
         *
         * @param key      key
         * @param levelNow 当前传入key所在的层
         *
         * @return 顺序位置
         */
        private int calculateDegreeForOneLevelNow(int key, int levelNow) {
            if ((key - levelEveryRangeLastIndexArray[this.levelNow - levelNow]) % NODE_ARRAY_LENGTH == 0) {
                // v = (int - x(y^?))/x - 1(?)
                return (key - levelEveryRangeLastIndexArray[this.levelNow - levelNow]) / NODE_ARRAY_LENGTH;
            }
            // v = (int - x(y^?))/x - 1(?)
            return (key - levelEveryRangeLastIndexArray[this.levelNow - levelNow]) / NODE_ARRAY_LENGTH + 1;
        }

        /**
         * 计算当前key在B-Tree中的真实数字
         *
         * @param key key
         * @param m   当前传入key所在的层
         * @param v   结点范围对象在整层度中的顺序位置
         *
         * @return 真实数字
         */
        private int calculateReal(int key, int m, int v) {
            // real = (y^(m-1))int -(y^(m-1))(y^(n-m) - v)
            return (int) (Math.pow(TREE_MAX_DEGREE, m - 1) * (key + v - Math.pow(TREE_MAX_DEGREE, TREE_MAX_LEVEL - m)));
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
    static class Node<V> implements RangePair<Integer, V> {

        /** 传入key */
        final Integer key;
        /** 存储Value */
        V value;

        Node(Integer key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public final Integer getKey() { return key; }

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
