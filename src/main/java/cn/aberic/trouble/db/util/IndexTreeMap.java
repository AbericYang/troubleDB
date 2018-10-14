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

import cn.aberic.trouble.db.core.TDBConfig;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aberic on 2018/10/14 20:41
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
public class IndexTreeMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

    private static final long serialVersionUID = 5666542770113713739L;

    /** 当前结点范围对象的根对象，祖宗结点 */
    private StorageRange<K, V> root;

    IndexTreeMap(String name) {
        root = new StorageRange<>(name);
    }

    IndexTreeMap(String name, TDBConfig config) {
        root = new StorageRange<>(name, config);
    }

    @Override
    public Range<K, V> range() {
        return root;
    }

    static class StorageRange<K, V> extends Range<K, V> {

        private TDBConfig config;
        private String name;

        StorageRange(String name) {
            super();
            this.name = name;
            this.config = new TDBConfig();
        }

        StorageRange(String name, TDBConfig config) {
            super(config.getTreeMaxLevel(), config.getNodeArrayLength());
            this.name = name;
            this.config = config;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        boolean contains(int unit, int storeHash) {
            int m = calculateLevelNow(storeHash); // 当前结点范围对象所在B-Tree的层
            int v = calculateDegreeForOneLevelNow(storeHash, m); // 当前结点范围对象在整层度中的顺序位置
            int real = calculateReal(storeHash, m, v); // 当前key在B-Tree中的真实数字
            int rangeV = v - ((v - 1) / treeMaxDegree) * treeMaxDegree;
            int minV = (int) ((real - (v - 1) * Math.pow(treeMaxDegree, m)) / Math.pow(treeMaxDegree, m - 1));
//            System.out.println("y = " + treeMaxDegree + " | m = " + m + " | n = " + treeMaxLevel + " | v = " + v +
//                    " | rangeV = " + rangeV + " | minV = " + minV + " | real = " + real);
            return false;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        V get(int unit, int storeHash, K key) {
            int m = calculateLevelNow(storeHash); // 当前结点范围对象所在B-Tree的层
            int v = calculateDegreeForOneLevelNow(storeHash, m); // 当前结点范围对象在整层度中的顺序位置
            int real = calculateReal(storeHash, m, v); // 当前key在B-Tree中的真实数字
            int rangeV = v - ((v - 1) / treeMaxDegree) * treeMaxDegree;
            // B-Tree结点对象在结点范围对象中的度，此处即为存储行号
            int minV = (int) ((real - (v - 1) * Math.pow(treeMaxDegree, m)) / Math.pow(treeMaxDegree, m - 1));
            System.out.println("y = " + treeMaxDegree + " | m = " + m + " | n = " + treeMaxLevel + " | v = " + v +
                    " | rangeV = " + rangeV + " | minV = " + minV + " | key = " + key + " | real = " + real);
            return null;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        V put(int unit, int storeHash, K key, V value) {
            int m = calculateLevelNow(storeHash); // 当前结点范围对象所在B-Tree的层
            int v = calculateDegreeForOneLevelNow(storeHash, m); // 当前结点范围对象在整层度中的顺序位置
            int real = calculateReal(storeHash, m, v); // 当前key在B-Tree中的真实数字
            int rangeV = v - ((v - 1) / treeMaxDegree) * treeMaxDegree;
            // 结点对象在结点范围对象中的度，此处即为存储行号
            int minV = (int) ((real - (v - 1) * Math.pow(treeMaxDegree, m)) / Math.pow(treeMaxDegree, m - 1));
//            System.out.println("y = " + treeMaxDegree + " | m = " + m + " | n = " + treeMaxLevel + " | v = " + v +
//                    " | rangeV = " + rangeV + " | minV = " + minV + " | key = " + key + " | real = " + real);
            return null;
        }

        private void saveKVIndex() {

        }
    }
}
