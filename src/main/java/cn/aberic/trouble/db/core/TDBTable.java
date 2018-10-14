package cn.aberic.trouble.db.core;

import cn.aberic.trouble.db.block.TroubleBlock;
import cn.aberic.trouble.db.util.HashBlockMap;

/**
 * @author Aberic on 2018/10/12 12:03
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TDBTable<K, V extends TroubleBlock> {

    private HashBlockMap<K, V> map;

    TDBTable(String name, TDBConfig config) {
        map = new HashBlockMap<>(name, config);
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
