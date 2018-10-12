package cn.aberic.trouble.db.core;

import cn.aberic.trouble.db.util.HashStorageMap;

/**
 * @author Aberic on 2018/10/12 12:03
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TDBTable<K, V> {

    private HashStorageMap<K, V> map;

    TDBTable(TDBConfig config) {
        map = new HashStorageMap<>(config.treeMaxLevel, config.nodeArrayLength);
    }

    boolean containsKey(K key) {
        return map.containsKey(key);
    }

    V get(K key) {
        return map.get(key);
    }

    V put(K key, V value) {
        return map.put(key, value);
    }

}
