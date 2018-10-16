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

import java.io.Serializable;

/**
 * @author Aberic on 2018/10/14 20:41
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
class TreeDiskMap<K, V> extends AbstractTreeMap<K, V> implements Serializable {

    private static final long serialVersionUID = 5666542770113713739L;

    /** 当前结点范围对象的根对象，祖宗结点 */
    private DiskRange<K, V> root;

    TreeDiskMap(String name) {
        root = new DiskRange<>(name);
    }

    TreeDiskMap(String name, TDConfig config) {
        root = new DiskRange<>(name, config);
    }

    @Override
    public Range<K, V> range() {
        return root;
    }

    static class DiskRange<K, V> extends Range<K, V> {

        private TDConfig config;
        private String name;

        DiskRange(String name) {
            super();
            init(name, new TDConfig());
        }

        DiskRange(String name, TDConfig config) {
            super(config.getTreeMaxLevel(), config.getNodeArrayLength());
            init(name, config);
        }

        private void init(String name, TDConfig config) {
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
            Position position = position(unit, storeHash, null, null);
            String path = TDConfig.storageIndexFilePath(config.getDbPath(), name, position.unit, position.level,
                    position.rangeLevelDegree, position.rangeDegree, position.nodeDegree);
            return fileExist(path);
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        V get(int unit, int storeHash, K key) {
            return getValue(name, config, unit, storeHash, key);
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        V put(int unit, int storeHash, K key, V value) {
            return putValue(name, config, unit, storeHash, key, value);
        }

    }

}
