package cn.aberic.trouble.db;

/**
 * 作者：Aberic on 2018/10/3 20:10
 * 邮箱：abericyang@gmail.com
 */
class Node<K> {

    /** 存储key */
    K k;
    /** 存储Value */
    NodeValue v;
    /** 当前结点所属结点范围 */
    NodeRange nodeRange;

    Node(K k, NodeValue v, NodeRange nodeRange) {
        this.k = k;
        this.v = v;
        this.nodeRange = nodeRange;
    }

}
