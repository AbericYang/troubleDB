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

import java.util.Deque;
import java.util.LinkedList;

/**
 * B-tree的层对象。
 *
 * <p>B-tree中每一个层对象{@code Range}都包含至少一个映射项{@link Map.RangePair}或{@link BlockPair}，
 * 且两者内的泛型元素保持一致。
 *
 * @see //RangeTreeMap
 * @see //BlockTreeMap
 * @see //RangeTreeMap.NodeRange
 * @see //BlockTreeMap.NodeRange
 * @since 1.0
 */
abstract class Range<K, V> {

    /** B-Tree的层 - n */
    private final static int TREE_MAX_LEVEL = 3;
    /** 结点范围对象中的所属子结点数组大小 - x */
//        protected final static int NODE_ARRAY_LENGTH = 3;
    final static int NODE_ARRAY_LENGTH = 100;
    final static int TREE_MAX_LENGTH = 1030300;
    /** B-Tree的最大度，即结点范围结点拥有子树的数目 - y */
    final static int TREE_MAX_DEGREE = NODE_ARRAY_LENGTH + 1;
    /** key在当前结点内的下标 - z */
    private int keyIndexInNode = 0;
    /** 当前结点范围对象在整层度中的顺序位置 - v */
    private int degreeForOneLevelNow = 1;
    /** 每一层的末尾终结位置 */
    private static int[] levelEveryRangeLastIndexArray;
    /** 当前结点范围对象所在B-Tree的层（节点默认值=4） - m */
    int levelNow;
    /** y^(m-1) */
    private int yPowM1;
    /** 结点数组首个对象下必须对应的数字 */
    private int firstNodeNum;
    /** 结点数组，不可扩容 */
    Map.RangePair<K, V>[] nodes;
    /** 当前结点范围的子对象数组，不可扩容 */
    Range<K, V>[] nodeChildrenRanges;

    private static void initLevelEveryRangeLastIndex() {
        levelEveryRangeLastIndexArray = new int[TREE_MAX_LEVEL + 1];
        levelEveryRangeLastIndexArray[0] = 0;
        for (int i = 1; i <= TREE_MAX_LEVEL; i++) {
            levelEveryRangeLastIndexArray[i] = NODE_ARRAY_LENGTH * (int) Math.pow(TREE_MAX_DEGREE, i - 1) + levelEveryRangeLastIndexArray[i - 1];
        }
//            System.out.println("levelEveryRangeLastIndexArray = " + Arrays.toString(levelEveryRangeLastIndexArray));
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
    Range() {
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
    Range(int levelNow, int keyIndexInNode, int degreeForOneLevelNow) {
        this.keyIndexInNode = keyIndexInNode;
        this.degreeForOneLevelNow = degreeForOneLevelNow;
        init(levelNow);
    }

    /**
     * 结点间范围对象初始化
     */
    @SuppressWarnings("unchecked")
    private void init(int levelNow) {
        this.levelNow = levelNow;
        this.yPowM1 = (int) Math.pow(TREE_MAX_DEGREE, this.levelNow - 1);
        // B-Tree的最大度/子range集合的数量必为结点集合的大小+1
        nodeChildrenRanges = new Range[TREE_MAX_DEGREE];
        // 结点数组根据设定执行初始化
        nodes = new Map.RangePair[NODE_ARRAY_LENGTH];
        // real = (1 + z)(y^(m - 1)) + (v - 1)(y^m)
        firstNodeNum = (1 + keyIndexInNode) * yPowM1 + (degreeForOneLevelNow - 1) * (int) Math.pow(TREE_MAX_DEGREE, this.levelNow);
//            System.out.println("firstNodeNum = " + firstNodeNum);
    }

    /** 结点数组，不可扩容 */
    abstract Map.RangePair<K, V>[] nodes();

    /** 当前结点范围的子对象数组，不可扩容 */
    abstract Range<K, V>[] nodeChildrenRanges();

    /**
     * 如果Range包含指定的元素，则返回 true。
     * 更确切地讲，当且仅当Range包含满足 <tt>(key==null ? e==null : key.equals(e))</tt> 的元素 <tt>e</tt> 时返回 <tt>true</tt> 。
     *
     * @param key 要测试此Range中是否存在的元素
     * @return 如果此Range包含指定的元素，则返回<tt>true</tt>
     * @throws ClassCastException   如果指定元素的类型与此Range不兼容（可选）
     * @throws NullPointerException 如果指定的元素为null并且此Range不允许null元素（可选）
     */
    boolean contains(int storeHash, K key) {
        return containsByKey(this, real(storeHash));
    }

    private boolean containsByKey(Range<K, V> range, int real) {
        int gap = real - firstNodeNum;
        int index;
        if (gap < 0) { // 为子范围集合中首个
            index = 0;
        } else {
            int yPowM1 = range.yPowM1;
            index = gap / yPowM1;
            if (gap % yPowM1 == 0) { // 为子结点集合其中之一
                Map.RangePair<K, V> node = nodes()[index];
                return null != node;
            } else { // 为子范围集合中首个以外的其中之一
                index += 1;
            }
        }
        Range<K, V> rangeNext = range.nodeChildrenRanges()[index];
        return null != rangeNext && rangeNext.containsByKey(rangeNext, real);
    }

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
    V get(int storeHash, K key) {
        return getVByKey(this, real(storeHash), key);
    }

    private V getVByKey(Range<K, V> range, int real, K key) {
        int gap = real - firstNodeNum;
        int index;
        if (gap < 0) { // 为子范围集合中首个
            index = 0;
        } else {
            int yPowM1 = range.yPowM1;
            index = gap / yPowM1;
            if (gap % yPowM1 == 0) { // 为子结点集合其中之一
                Map.RangePair<K, V> node = nodes()[index];
                return null != node ? node.getValue(key) : null;
            } else { // 为子范围集合中首个以外的其中之一
                index += 1;
            }
        }
        Range<K, V> rangeNext = range.nodeChildrenRanges()[index];
        return null != rangeNext ? rangeNext.getVByKey(rangeNext, real, key) : null;
    }

    /**
     * 将指定的值与此映射中的指定键关联（可选操作）。
     * 如果此映射以前包含一个该键的映射关系，
     * 则用指定值替换旧值（当且仅当{@link #contains(int, K) m.contains(k)}返回 <tt>true</tt> 时，
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
    V put(int storeHash, K key, V value) {
        int m = calculateLevelNow(storeHash); // 当前结点范围对象所在B-Tree的层
        int v = calculateDegreeForOneLevelNow(storeHash, m); // 当前结点范围对象在整层度中的顺序位置
        int real = calculateReal(storeHash, m, v); // 当前key在B-Tree中的真实数字
        return putReal(real, key, value, m, v);
    }

    private V putReal(int real, K key, V value, int m, int v) {
        // 新建一个栈，用于存放自下而上每一父结点范围对象在整层度中的顺序位置
        Deque<Integer> vDeque = new LinkedList<>();
        vDeque.push(v); // 先把自己push到最底层
        int temV = v;
        for (int i = m; i < TREE_MAX_LEVEL; i++) { // 从下至上开始push当前值的层层结点范围对象所在整层度中的顺序位置
            temV = (temV - 1) / TREE_MAX_DEGREE + 1;
//                System.out.println("temV in = " + temV);
            vDeque.push(temV);
        }
        vDeque.pop();
        return putExec(vDeque, temV, real, key, value, m, v);
    }

    abstract V putExec(Deque<Integer> vDeque, int temV, int real, K key, V value, int m, int v);

    private int real(int storeHash) {
        int m = calculateLevelNow(storeHash); // 当前结点范围对象所在B-Tree的层
        int v = calculateDegreeForOneLevelNow(storeHash, m); // 当前结点范围对象在整层度中的顺序位置
        return calculateReal(storeHash, m, v); // 当前key在B-Tree中的真实数字
    }

    /**
     * 计算当前传入key所在的层 - m
     *
     * @param key key
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
     * @return 真实数字
     */
    private int calculateReal(int key, int m, int v) {
        // real = (y^(m-1))int -(y^(m-1))(y^(n-m) - v)
        return (int) (Math.pow(TREE_MAX_DEGREE, m - 1) * (key + v - Math.pow(TREE_MAX_DEGREE, TREE_MAX_LEVEL - m)));
    }

}