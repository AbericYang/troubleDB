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
 * @author Aberic on 2018/10/12 01:21
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
class Pair {

    /** B-Tree的层 - n */
    final static int TREE_MAX_LEVEL = 3;
    /** 结点范围对象中的所属子结点数组大小 - x */
//        protected final static int NODE_ARRAY_LENGTH = 3;
    final static int NODE_ARRAY_LENGTH = 100;
    final static int TREE_MAX_LENGTH = 1030300;
    /** B-Tree的最大度，即结点范围结点拥有子树的数目 - y */
    final static int TREE_MAX_DEGREE = NODE_ARRAY_LENGTH + 1;
    /** 每一层的末尾终结位置 */
    private static int[] levelEveryRangeLastIndexArray;
    /** 当前结点范围对象所在B-Tree的层（节点默认值=4） - m */
    int levelNow;

    static void initLevelEveryRangeLastIndex() {
        levelEveryRangeLastIndexArray = new int[TREE_MAX_LEVEL + 1];
        levelEveryRangeLastIndexArray[0] = 0;
        for (int i = 1; i <= TREE_MAX_LEVEL; i++) {
            levelEveryRangeLastIndexArray[i] = NODE_ARRAY_LENGTH * (int) Math.pow(TREE_MAX_DEGREE, i - 1) + levelEveryRangeLastIndexArray[i - 1];
        }
//            System.out.println("levelEveryRangeLastIndexArray = " + Arrays.toString(levelEveryRangeLastIndexArray));
    }

    int real(int storeHash) {
        int m = calculateLevelNow(storeHash); // 当前结点范围对象所在B-Tree的层
        int v = calculateDegreeForOneLevelNow(storeHash, m); // 当前结点范围对象在整层度中的顺序位置
        return calculateReal(storeHash, m, v); // 当前key在B-Tree中的真实数字
    }

    /**
     * 计算当前传入key所在的层 - m
     *
     * @param key key
     *
     * @return 所在层
     */
    int calculateLevelNow(int key) {
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
    int calculateDegreeForOneLevelNow(int key, int levelNow) {
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
    int calculateReal(int key, int m, int v) {
        // real = (y^(m-1))int -(y^(m-1))(y^(n-m) - v)
        return (int) (Math.pow(TREE_MAX_DEGREE, m - 1) * (key + v - Math.pow(TREE_MAX_DEGREE, TREE_MAX_LEVEL - m)));
    }

}
