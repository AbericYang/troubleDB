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

package cn.aberic.trouble.db.third;

import cn.aberic.trouble.db.third.service.IDBExec;
import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 作者：Aberic on 2018/09/03 14:28
 * <p>
 * 邮箱：abericyang@gmail.com
 */
class RocksDBExec implements IDBExec {

    @Override
    public void put(String key, String value) {
        try (RocksDB db = getSystemContractHash()) {
            db.put(key.getBytes("UTF-8"), value.getBytes("UTF-8"));
        } catch (IOException | RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(Map<String, String> map) {
        try (RocksDB db = getSystemContractHash();
             WriteOptions writeOpt = new WriteOptions();
             WriteBatch writeBatch = new WriteBatch()) {
            map.forEach((ket, value) -> {
                try {
                    writeBatch.put(ket.getBytes("UTF-8"), value.getBytes("UTF-8"));
                } catch (RocksDBException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            db.write(writeOpt, writeBatch);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String key) {
        try (RocksDB db = getSystemContractHash()) {
            return new String(db.get(key.getBytes("UTF-8")), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void put(String contractHash, String key, String value) {
        try (RocksDB db = getCustomContractHash(contractHash)) {
            db.put(key.getBytes("UTF-8"), value.getBytes("UTF-8"));
        } catch (IOException | RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String contractHash, Map<String, String> map) {
        try (RocksDB db = getCustomContractHash(contractHash);
             WriteOptions writeOpt = new WriteOptions();
             WriteBatch writeBatch = new WriteBatch()) {
            map.forEach((ket, value) -> {
                try {
                    writeBatch.put(ket.getBytes("UTF-8"), value.getBytes("UTF-8"));
                } catch (RocksDBException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            db.write(writeOpt, writeBatch);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String contractHash, String key) {
        try (RocksDB db = getCustomContractHash(contractHash)) {
            return new String(db.get(key.getBytes("UTF-8")), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    private RocksDB getSystemContractHash() throws RocksDBException {
        return RocksDB.open(getOptions(), "/Users/Aberic/Documents/tmp/troubleDB/rocks");
    }

    private RocksDB getCustomContractHash(String contractHash) throws RocksDBException {
        return RocksDB.open(getOptions(), String.format("%s/%s", "/Users/Aberic/Documents/tmp/troubleDB/rocks", contractHash));
    }

    private Options getOptions() {
        Options options = new Options();
        Statistics stats = new Statistics();
        RateLimiter rateLimiter = new RateLimiter(10000000, 10000, 10);

        BlockBasedTableConfig table_options = new BlockBasedTableConfig();
        Filter bloomFilter = new BloomFilter(10);
        table_options.setBlockCacheSize(64 * SizeUnit.KB)
                .setFilter(bloomFilter)
                .setCacheNumShardBits(6)
                .setBlockSizeDeviation(5)
                .setBlockRestartInterval(10)
                .setCacheIndexAndFilterBlocks(true)
                .setHashIndexAllowCollision(false)
                .setBlockCacheCompressedSize(64 * SizeUnit.KB)
                .setBlockCacheCompressedNumShardBits(10);

        return options
                // 如果为真，则在数据库丢失时将创建该数据库。
                .setCreateIfMissing(true)
                // 设置统计对象，该对象收集关于数据库操作的指标。统计数据对象不应该在DB实例之间共享，因为它不使用任何锁来防止并发更新。
                .setStatistics(stats)
                // 在将数据转换为已排序的磁盘文件之前，在内存中积累的数据量(由磁盘上未排序的日志支持)。
                // 更大的值可以提高性能，特别是在大容量负载期间。
                // max_write_buffer_number写缓冲区可能同时保存在内存中，因此您可能希望调整此参数以控制内存使用。
                // 此外，更大的写缓冲区将导致下一次打开数据库时更长的恢复时间。默认值:4 MB
                .setWriteBufferSize(64 * SizeUnit.MB)
                // 内存中建立的最大写缓冲区数。默认值是2，因此当一个写缓冲区被刷新到存储中时，新的写可以继续到另一个写缓冲区。默认值:2
                .setMaxWriteBufferNumber(3)
                // 指定提交到默认低优先级线程池的并发后台压缩作业的最大数量。
                // 如果要增加线程数，也要考虑增加低优先级线程池中的线程数。
                .setMaxBackgroundCompactions(10)
                // 使用指定的压缩算法压缩块。这个参数可以动态更改。默认:SNAPPY_COMPRESSION，它提供轻量级但快速的压缩。
                .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
                // 为DB设置压缩样式。默认值:LEVEL.
                .setCompactionStyle(CompactionStyle.UNIVERSAL)
                // 内存表配置用于配置RocksDB的内部内存表。
                .setMemTableConfig(
                        // 哈希跳跃表内存表配置。
                        // 这样的内存表表示包含一个按大小排序的bucket数组，其中每个bucket指向一个skiplist(如果bucket是空的，则为null)。
                        new SkipListMemTableConfig())
                // 设置表格式的配置
                // 允许操作系统mmap文件来读取sst表。默认值:false
                .setAllowMmapReads(true)
                // 用于控制刷新和压缩的写速率。刷新的优先级高于压缩。如果nullptr，则禁用速率限制。默认值:nullptr
                .setRateLimiter(rateLimiter)
                .setTableFormatConfig(table_options);
    }

}
