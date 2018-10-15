///*
// * MIT License
// *
// * Copyright (c) 2018 Aberic Yang
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package cn.aberic.trouble.db.util;
//
//import cn.aberic.trouble.db.block.TroubleBlock;
//import cn.aberic.trouble.db.core.TDConfig;
//
//import java.io.Serializable;
//
///**
// * @author Aberic on 2018/10/12 10:35
// * @version 1.0
// * @see
// * @since 1.0
// */
//public class HashBlockMap<K, V extends TroubleBlock> extends AbstractHashMap<K, V> implements Serializable {
//
//    private static final long serialVersionUID = 660419343050609348L;
//
//    private TreeBlockMap<K, V> blockTreeMap;
//
//    public HashBlockMap(String name) {
//        blockTreeMap = new TreeBlockMap<>(name);
//        treeMaxLength = blockTreeMap.range().treeMaxLength;
//    }
//
//    public HashBlockMap(String name, TDConfig config) {
//        blockTreeMap = new TreeBlockMap<>(name, config);
//        treeMaxLength = blockTreeMap.range().treeMaxLength;
//    }
//
//    @Override
//    public boolean containsMKey(K key) {
//        int hash = checkHashByKey(key);
//        int unit = unit(hash);
//        return blockTreeMap.containsMKey(unit, storeHash(hash, unit));
//    }
//
//    @Override
//    public V getM(K key) {
//        int hash = checkHashByKey(key);
//        int unit = unit(hash);
//        return blockTreeMap.getM(unit, storeHash(hash, unit), key);
//    }
//
//    @Override
//    public V putM(K key, V value) {
//        int hash = checkHashByKey(key);
//        int unit = unit(hash);
//        return blockTreeMap.putM(unit, storeHash(hash, unit), key, value);
//    }
//
//    @Override
//    int reHash(int hash) {
//        hash += blockTreeMap.range().treeMaxLength;
//        if (hash < 0) {
//            return reHash(hash);
//        }
//        return hash;
//    }
//}
