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
import cn.aberic.trouble.db.block.TroubleTransaction;
import cn.aberic.trouble.db.block.TroubleValueWrite;
import cn.aberic.trouble.db.core.TDConfig;
import cn.aberic.trouble.db.core.TDManager;

import java.io.Serializable;

/**
 * @author Aberic on 2018/10/16 10:33
 * @version 1.0
 * @see
 * @since 1.0
 */
class TreeBlockMap<K> extends AbstractTreeMap<K, TroubleBlock> implements Serializable {

    private static final long serialVersionUID = 8884164220134360560L;

    /** 当前结点范围对象的根对象，祖宗结点 */
    private BlockRange<K> root;

    TreeBlockMap(String name) {
        root = new BlockRange<>(name);
    }

    TreeBlockMap(String name, TDConfig config) {
        root = new BlockRange<>(name, config);
    }

    @Override
    public Range<K, TroubleBlock> range() {
        return root;
    }

    static class BlockRange<K> extends Range<K, TroubleBlock> {

        private TDConfig config;
        private String name;

        BlockRange(String name) {
            super();
            init(name, new TDConfig());
        }

        BlockRange(String name, TDConfig config) {
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
        TroubleBlock get(int unit, int storeHash, K key) {
            return getValue(name, config, unit, storeHash, key);
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        TroubleBlock put(int unit, int storeHash, K key, TroubleBlock value) {
            TDManager.obtain().createDTable(name);
            // 将写集KV写入磁盘库
            value.getBody().getTransactions().forEach(transaction ->
                    ((TroubleTransaction) transaction).getRwSet().getWrites().forEach(write ->
                            TDManager.obtain().putD(name, ((TroubleValueWrite) write).getKey(), ((TroubleValueWrite) write).getValue())));
            return putValue(name, config, unit, storeHash, key, value);
        }

    }

}
