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

package cn.aberic.trouble.db.core;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author Aberic on 2018/10/12 14:21
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TDConfig {

    /** 默认区块文件前缀 */
    private final static String TDB_BLOCK_FILE_START = "block_file_";
    /** 默认区块文件后缀 */
    private final static String TDB_BLOCK_FILE_END = ".block";
    private final static String TDB_INDEX_NAME = "index";
    private final static String TDB_INDEX_END = ".tdb";
    /** 默认区块文件存储路径 测试/生产 */
    private final static String TROUBLE_DB_FILE_DIR = "/Users/Aberic/Documents/tmp/troubleDB/";
    // private final static String TROUBLE_DB_FILE_DIR = "/data/trouble/troubleDB/";

    /** B-Tree的层 - n */
    private int treeMaxLevel = 0;
    /** 结点范围对象中的所属子结点数组大小 - x */
    private int nodeArrayLength = 0;
    /** TDB数据库的默认存储目录 */
    private String dbPath;

    /**
     * 存储Key-Value映射关系
     *
     * @param dbPath           TDB数据库的默认存储目录
     * @param unit             hash表的数组下标，此处即一级目录
     * @param level            B-Tree的层级，此处即二级目录 - m
     * @param rangeLevelDegree B-Tree结点范围对象在整层中的所在度，此处即三级目录 - v
     * @param rangeDegree      B-Tree结点范围对象在上一级结点范围对象中所在度，此处即文件名称
     *
     * @return 索引文件路径
     */
    public static final String storageIndexFilePath(String dbPath, String tableName, int unit, int level,
                                                    int rangeLevelDegree, int rangeDegree) {
        if (StringUtils.isEmpty(dbPath)) {
            dbPath = TROUBLE_DB_FILE_DIR;
        }
        return String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s", dbPath, File.separator, tableName, File.separator,
                TDB_INDEX_NAME, File.separator, unit, File.separator, level, File.separator, rangeLevelDegree,
                File.separator, rangeDegree, TDB_INDEX_END);
    }

    public static final String storageBlockFilePath(String dbPath, String tableName, int unit, int level,
                                                    int rangeLevelDegree, int rangeDegree) {
        if (StringUtils.isEmpty(dbPath)) {
            dbPath = TROUBLE_DB_FILE_DIR;
        }
        return String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s", dbPath, File.separator, tableName, File.separator,
                TDB_INDEX_NAME, File.separator, unit, File.separator, level, File.separator, rangeLevelDegree,
                File.separator, TDB_BLOCK_FILE_START, rangeDegree, TDB_BLOCK_FILE_END);
    }

    public TDConfig setTree(int treeMaxLevel, int nodeArrayLength) {
        this.treeMaxLevel = treeMaxLevel;
        this.nodeArrayLength = nodeArrayLength;
        return this;
    }

    public TDConfig setDBPath(String dbPath) {
        this.dbPath = dbPath;
        return this;
    }

    public int getTreeMaxLevel() {
        return treeMaxLevel;
    }

    public int getNodeArrayLength() {
        return nodeArrayLength;
    }

    public String getDbPath() {
        return dbPath;
    }

}
