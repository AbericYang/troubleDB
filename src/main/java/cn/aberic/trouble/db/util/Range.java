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

import cn.aberic.trouble.db.core.TDConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.LinkedList;

import static cn.aberic.trouble.db.util.AbstractTreeMap.file;

/**
 * B-tree的层对象。
 *
 * <p>B-tree中每一个层对象{@code Range}都包含至少一个映射项{@link Map.RangePair}，
 * 且两者内的泛型元素保持一致。
 *
 * @see AbstractTreeMap
 * @see TreeMemoryMap
 * @since 1.0
 */
abstract class Range<K, V> extends Pair {

    /** key在当前结点内的下标 - z */
    private int keyIndexInNode = 0;
    /** 当前结点范围对象在整层度中的顺序位置 - v */
    private int degreeForOneLevelNow = 1;
    /** y^(m-1) */
    private int yPowM1;
    /** 结点数组首个对象下必须对应的数字 */
    private int firstNodeNum;
    /** 结点数组，不可扩容 */
    Map.RangePair<K, V>[] nodes;
    /** 当前结点范围的子对象数组，不可扩容 */
    Range<K, V>[] nodeChildrenRanges;

    /**
     * 指定范围对象中的所属子结点数组大小进行构造，构造结果为顶级/虚结点范围对象
     *
     * <p>顶级/虚结点范围对象为所有结点范围对象的祖宗，它没有任何实际数据可操作意义，
     * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行切割，
     * 直到切割数量达到({@code MemoryRange#NODE_ARRAY_LENGTH} + 1)，即满足分裂条件。
     * 当满足分裂条件后，{@code MemoryRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
     *
     * <p>持续分割会维持一个B-Tree的模型。
     */
    Range() {
        this(0, 0);
    }

    Range(int treeMaxLevel, int nodeArrayLength) {
        this(-1, 0, 1, treeMaxLevel, nodeArrayLength);
    }

    /**
     * 内部使用结点间范围对象构造
     *
     * @param levelNow             当前结点范围对象所在B-Tree的层（节点默认值=4） - m
     * @param keyIndexInNode       key在当前结点内的下标 - z
     * @param degreeForOneLevelNow 当前结点范围对象在整层度中的顺序位置 - v
     */
    Range(int levelNow, int keyIndexInNode, int degreeForOneLevelNow, int treeMaxLevel, int nodeArrayLength) {
        super(treeMaxLevel, nodeArrayLength);
        this.keyIndexInNode = keyIndexInNode;
        this.degreeForOneLevelNow = degreeForOneLevelNow;
        init(levelNow);
    }

    /**
     * 结点间范围对象初始化，并指定其当前所在层
     *
     * @param levelNow 结点范围对象被指定的层
     */
    @SuppressWarnings("unchecked")
    private void init(int levelNow) {
        this.levelNow = levelNow == -1 ? treeMaxLevel : levelNow;
        this.yPowM1 = (int) Math.pow(treeMaxDegree, this.levelNow - 1);
        // B-Tree的最大度/子range集合的数量必为结点集合的大小+1
        nodeChildrenRanges = new Range[treeMaxDegree];
        // 结点数组根据设定执行初始化
        nodes = new Map.RangePair[nodeArrayLength];
        // real = (1 + z)(y^(m - 1)) + (v - 1)(y^m)
        firstNodeNum = (1 + keyIndexInNode) * yPowM1 + (degreeForOneLevelNow - 1) * (int) Math.pow(treeMaxDegree, this.levelNow);
//            System.out.println("firstNodeNum = " + firstNodeNum);
    }

    /**
     * 获取不可扩容的结点数组，并非强制重写。
     * 如果要使用{@link Range}自身的{@link Range#contains(int, int)}和{@link Range#get(int, int, Object)}方法，则必须重写。
     * 否则，重写{@link Range#contains(int, int)}和{@link Range#get(int, int, Object)}方法以完善子类信息。
     *
     * @return 不可扩容的结点数组
     */
    Map.RangePair<K, V>[] nodes() {
        return null;
    }

    /**
     * 获取不可扩容的结点范围的子对象数组，并非强制重写。
     * 如果要使用{@link Range}自身的{@link Range#contains(int, int)}和{@link Range#get(int, int, Object)}方法，则必须重写。
     * 否则，重写{@link Range#contains(int, int)}和{@link Range#get(int, int, Object)}方法以完善子类信息。
     *
     * @return 不可扩容的结点范围的子对象数组
     */
    Range<K, V>[] nodeChildrenRanges() {
        return null;
    }

    /**
     * 如果Range包含指定的元素，则返回 true。
     * 更确切地讲，当且仅当Range包含满足 <tt>(key==null ? e==null : key.equals(e))</tt> 的元素 <tt>e</tt> 时返回 <tt>true</tt> 。
     *
     * @param unit      传入key当前Hash数组中要访问的下标
     * @param storeHash 要测试此Range中是否存在的元素
     * @return 如果此Range包含指定的元素，则返回<tt>true</tt>
     * @throws ClassCastException   如果指定元素的类型与此Range不兼容（可选）
     * @throws NullPointerException 如果指定的元素为null并且此Range不允许null元素（可选）
     */
    boolean contains(int unit, int storeHash) {
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
     * @param unit 传入key当前Hash数组中要访问的下标
     * @param key  要返回其关联值的键
     * @return 指定键所映射的值；如果此映射不包含该键的映射关系，则返回{@code null}
     * @throws ClassCastException   如果该键对于此映射是不合适的类型（可选）
     * @throws NullPointerException 如果指定键为 null 并且此映射不允许 null 键（可选）
     */
    V get(int unit, int storeHash, K key) {
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
     * 则用指定值替换旧值（当且仅当{@link #contains(int, int) m.contains(k)}返回 <tt>true</tt> 时，
     * 才能说映射 <tt>m</tt> 包含键 <tt>k</tt> 的映射关系）。
     *
     * @param unit  传入key当前Hash数组中要访问的下标
     * @param key   与指定值关联的键
     * @param value 与指定键关联的值
     * @return 以前与 <tt>key</tt> 关联的值，如果没有针对 <tt>key</tt> 的映射关系，则返回 <tt>null</tt> 。
     * （如果该实现支持 <tt>null</tt> 值，则返回 <tt>null</tt> 也可能表示此映射以前将 <tt>null</tt> 与 <tt>key</tt> 关联）
     * @throws UnsupportedOperationException 如果此映射不支持 <tt>putM</tt> 操作
     * @throws ClassCastException            如果指定键或值的类不允许将其存储在此映射中
     * @throws NullPointerException          如果指定键或值为 <tt>null</tt> ，并且此映射不允许 <tt>null</tt> 键或值
     * @throws IllegalArgumentException      如果指定键或值的某些属性不允许将其存储在此映射中
     */
    V put(int unit, int storeHash, K key, V value) {
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
        for (int i = m; i < treeMaxLevel; i++) { // 从下至上开始push当前值的层层结点范围对象所在整层度中的顺序位置
            temV = (temV - 1) / treeMaxDegree + 1;
//                System.out.println("temV in = " + temV);
            vDeque.push(temV);
        }
        vDeque.pop();
        return putExec(vDeque, real, key, value, m, v);
    }

    /**
     * 处理存入操作并获取存入结果返回值，并非强制重写。
     * 该方法会在{@link TreeMemoryMap}中进行重写，{@code TreeMemoryMap}并非磁盘存储对象，而是内存缓存对象。
     * 理论上，该方法仅内存缓存对象重写即可。
     *
     * @param vDeque 用于存放自下而上每一父结点范围对象在整层度中顺序位置的栈
     * @param real   真实存入的键
     * @param key    传入的key
     * @param value  传入的value
     * @param m      结点范围对象所在B-Tree的层
     * @param v      结点范围对象在整层度中的顺序位置
     * @return 计划返回的是旧的值，如果有的话。当没有旧值的时候，就返回当前新存入的值
     */
    V putExec(Deque<Integer> vDeque, int real, K key, V value, int m, int v) {
        return null;
    }

    V get(String name, TDConfig config, int unit, int storeHash, K key) {
        Range.Position position = position(unit, storeHash, key, null);
        File file = file(TDConfig.storageIndexFilePath(config.getDbPath(), name, position.unit, position.level,
                position.rangeLevelDegree, position.rangeDegree, position.nodeDegree));
        try {
            String fileContent = Files.asCharSource(file, Charset.forName("UTF-8")).read();
            if (StringUtils.isEmpty(fileContent)) {
                file.delete();
                return null;
            } else {
                return JSON.parseObject(fileContent, new TypeReference<V>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
        }
        return null;
    }

    final Position position(int unit, int storeHash, K key, V value) {
        int m = calculateLevelNow(storeHash); // 当前结点范围对象所在B-Tree的层
        int v = calculateDegreeForOneLevelNow(storeHash, m); // 当前结点范围对象在整层度中的顺序位置
        int real = calculateReal(storeHash, m, v); // 当前key在B-Tree中的真实数字
        int rangeV = v - ((v - 1) / treeMaxDegree) * treeMaxDegree;
        // 结点对象在结点范围对象中的度，此处即为存储行号
        int minV = (int) ((real - (v - 1) * Math.pow(treeMaxDegree, m)) / Math.pow(treeMaxDegree, m - 1));
//        System.out.println("unit = " + unit + " | storeHash = " + storeHash + " | y = " + treeMaxDegree + " | m = " + m +
//                " | n = " + treeMaxLevel + " | v = " + v + " | rangeV = " + rangeV + " | minV = " + minV +
//                " | key = " + key + " | real = " + real);
        return new Position(unit, m, v, rangeV, minV, value);
    }

    class Position {
        /** hash表的数组下标，此处即一级目录 */
        int unit;
        /** B-Tree的层级，此处即二级目录 - m */
        int level;
        /** B-Tree结点范围对象在整层中的所在度，此处即三级目录 - v */
        int rangeLevelDegree;
        /** B-Tree结点范围对象在上一级结点范围对象中所在度，此处即文件名称 */
        int rangeDegree;
        /** B-Tree结点对象在结点范围对象中的度，此处即为存储行号 */
        int nodeDegree;
        V value;

        Position(int unit, int level, int rangeLevelDegree, int rangeDegree, int nodeDegree, V value) {
            this.unit = unit;
            this.level = level;
            this.rangeLevelDegree = rangeLevelDegree;
            this.rangeDegree = rangeDegree;
            this.nodeDegree = nodeDegree;
            this.value = value;
        }
    }

}