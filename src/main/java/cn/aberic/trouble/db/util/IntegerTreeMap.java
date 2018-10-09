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

/**
 * n <====> tree level 0+
 * m <====> tree now level 0+
 * x <====> node count 1+
 * y <====> tree degree 0+ <====> x + 1
 * b <====> now degree 0+
 * z <====> now node index 0+
 * v <====> now full level degree 1+
 * <p>
 * (1 + z)(y^(m - 1)) + (v - 1)(y^m)
 * <p>
 * <p>
 * int to real
 * <p>
 * real = (y^(m-1))int -(y^(m-1))(y^(n-m) - v)
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
     *
     * @param length 范围对象中的所属子结点数组大小
     */
    public IntegerTreeMap(int length) {
        root = new NodeRange<>();
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

        /** 结点范围对象中的所属子结点数组大小 - x */
        private final static int NODE_ARRAY_LENGTH = 64;
        /** B-Tree的最大度，即结点范围结点拥有子树的数目 - y */
        private final static int TREE_MAX_DEGREE = 65;
        /** B-Tree的层 - n */
        private final static int TREE_MAX_LEVEL = 4;
        /** 每一层的末尾终结位置 */
        private static int[] levelEveryRange;
        /** 当前结点范围对象所在B-Tree的层（节点默认值=4） - m */
        private int levelNow;
        /** 当前结点范围对象在整层度中的顺序位置（首节点默认值=0） - v */
        private int degreeForOneLevelNow;
        /** 结点数组，不可扩容 */
        private Node<V>[] nodes;
        /** 当前范围下的结点总数 */
        private int size;
        /** 当前范围下的子对象数组总数 */
        private int length;
        /** 当前结点范围的父对象，仅根可为null */
        private NodeRange parent;
        /** 当前结点范围的子对象数组，不可扩容 */
        private NodeRange<V>[] nodeChildrenRanges;

        static void initLevelErevyRange() {
            levelEveryRange = new int[TREE_MAX_LEVEL];
            levelEveryRange[0] = NODE_ARRAY_LENGTH;
            levelEveryRange[1] = NODE_ARRAY_LENGTH * TREE_MAX_DEGREE + levelEveryRange[0];
            levelEveryRange[2] = NODE_ARRAY_LENGTH * (int) Math.pow(TREE_MAX_DEGREE, 2) + levelEveryRange[1];
            levelEveryRange[3] = NODE_ARRAY_LENGTH * (int) Math.pow(TREE_MAX_DEGREE, 3) + levelEveryRange[2];
            System.out.println("levelEveryRange = " + Arrays.toString(levelEveryRange));
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
        public NodeRange() {
            init(4, 0, null);
        }

        /**
         * 内部使用结点间范围对象构造
         *
         * @param nodeRange 当前结点范围对象的父对象，仅根可为null
         */
        NodeRange(int levelNow, int degreeForOneLevelNow, NodeRange nodeRange) {
            init(levelNow, degreeForOneLevelNow, nodeRange);
        }

        /**
         * 结点间范围对象初始化
         *
         * @param nodeRange 当前结点范围对象的父对象，仅根可为null
         */
        @SuppressWarnings("unchecked")
        void init(int levelNow, int degreeForOneLevelNow, NodeRange nodeRange) {
            this.levelNow = levelNow;
            this.degreeForOneLevelNow = degreeForOneLevelNow;
            this.size = 0;
            this.length = 0;
            parent = nodeRange;
            // 结点数组根据设定执行初始化
            nodes = new Node[NODE_ARRAY_LENGTH];
            // B-Tree的最大度/子range集合的数量必为结点集合的大小+1
            nodeChildrenRanges = new NodeRange[TREE_MAX_DEGREE];
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public V get(Object key) {
            return null;
        }

        @Override
        public V put(Integer key, V value) {
            if (key < levelEveryRange[0]) {

            } else if (key == levelEveryRange[0]) {

            } else if (key < levelEveryRange[1]) {

            } else if (key == levelEveryRange[1]) {

            } else if (key < levelEveryRange[2]) {

            } else if (key == levelEveryRange[2]) {

            } else if (key < levelEveryRange[3]) {

            } else if (key == levelEveryRange[3]) {

            }
            return value;
        }

        @Override
        public boolean contains(Object key) {
            return false;
        }

        private int calculateDegreeForOneLevelNow(int key) {
            return 0;
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
        /** 上一个结点对象的Key，首结点默认上一结点为-1 */
        Integer preKey;
        /** 下一个结点对象的Key，末结点默认下一结点为-1 */
        Integer nextKey;

        Node(Integer key, V value) {
            this.key = key;
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
