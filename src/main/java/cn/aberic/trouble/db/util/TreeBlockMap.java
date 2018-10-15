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
import cn.aberic.trouble.db.exception.BlockLineUnMatchException;
import com.alibaba.fastjson.JSON;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aberic on 2018/10/11 23:29
 * @see HashMemoryMap
 * @since 1.0
 */
public class TreeBlockMap<K, V extends TroubleBlock> extends AbstractTreeMap<K, V> implements Serializable {

    private static final long serialVersionUID = 5666542770113713739L;

    /** 当前结点范围对象的根对象，祖宗结点 */
    private StorageRange<K, V> root;

    TreeBlockMap(String name) {
        root = new StorageRange<>(name);
    }

    TreeBlockMap(String name, TDConfig config) {
        root = new StorageRange<>(name, config);
    }

    @Override
    public Range<K, V> range() {
        return root;
    }

    static class StorageRange<K, V> extends Range<K, V> {

        private TDConfig config;
        private String name;

        StorageRange(String name) {
            super();
            this.name = name;
            this.config = new TDConfig();
        }

        StorageRange(String name, TDConfig config) {
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
            Position position = position(unit, storeHash, null, null);
            return false;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        V get(int unit, int storeHash, K key) {
            Position position = position(unit, storeHash, key, null);
            return null;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        @Override
        V put(int unit, int storeHash, K key, V value) {
            Position position = position(unit, storeHash, key, value);
            return null;
        }

        @Override
        V putBlock(int unit, int storeHash, K key, V value) throws BlockLineUnMatchException {
            Position position = position(unit, storeHash, key, value);
            saveBlock(position);
            return null;
        }

        /**
         * 存储区块对象到指定的区块文件中
         *
         * @param unit             hash表的数组下标，此处即一级目录
         * @param level            B-Tree的层级，此处即二级目录 - m
         * @param rangeLevelDegree B-Tree结点范围对象在整层中的所在度，此处即三级目录 - v
         * @param rangeDegree      B-Tree结点范围对象在上一级结点范围对象中所在度，此处即文件名称
         * @param nodeDegree       B-Tree结点对象在结点范围对象中的度，此处即为存储行号
         */
        @SuppressWarnings("all")
        private void saveBlock(Position position) throws BlockLineUnMatchException {
            File file = file(TDConfig.storageBlockFilePath(config.getDbPath(), name, position.unit, position.level, position.rangeLevelDegree, position.rangeDegree));
            int lines = lines(file);
            if (lines + 1 == position.nodeDegree) { // 如果待存入区块对象所在行号与当前顺位预留行号一致，则存入
                try {
                    if (lines == 0) {
                        Files.asCharSink(file, Charset.forName("UTF-8"), FileWriteMode.APPEND).write(JSON.toJSONString(position.value));
                    } else {
                        Files.asCharSink(file, Charset.forName("UTF-8"), FileWriteMode.APPEND).write(String.format("\r\n%s", JSON.toJSONString(position.value)));
                    }
                    List<TroubleValueWrite> writes = new ArrayList<>();
                    ((TroubleBlock) position.value).getBody().getTransactions().forEach(transaction -> {
                        ((TroubleTransaction) transaction).getRwSet().getWrites().forEach(write -> {
                            writes.add((TroubleValueWrite) write);
                        });
                    });
                    // saveBlockIndex(position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new BlockLineUnMatchException("block line is " + position.nodeDegree + " , File last line is " + lines);
            }
        }

        private void saveKVIndex() {

        }
    }

}
