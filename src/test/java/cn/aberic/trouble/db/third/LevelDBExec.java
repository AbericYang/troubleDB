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
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 作者：Aberic on 2018/9/1 15:12
 * <p>
 * 邮箱：abericyang@gmail.com
 */
class LevelDBExec implements IDBExec {

    @Override
    public void put(String key, String value) {
        try (DB db = getSystemContractHash()) {
            db.put(key.getBytes("UTF-8"), value.getBytes("UTF-8"), new WriteOptions().sync(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(Map<String, String> map) {
        try (DB db = getSystemContractHash()) {
            WriteBatch writeBatch = db.createWriteBatch();
            map.forEach((ket, value) -> {
                try {
                    writeBatch.put(ket.getBytes("UTF-8"), value.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            db.write(writeBatch);
            writeBatch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String key) {
        try (DB db = getSystemContractHash()) {
            return new String(db.get(key.getBytes("UTF-8")), "UTF-8");
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public void put(String contractHash, String key, String value) {
        try (DB db = getCustomContractHash(contractHash)) {
            db.put(key.getBytes("UTF-8"), value.getBytes("UTF-8"), new WriteOptions().sync(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String contractHash, Map<String, String> map) {
        try (DB db = getCustomContractHash(contractHash)) {
            WriteBatch writeBatch = db.createWriteBatch();
            map.forEach((key, value) -> {
                try {
                    writeBatch.put(key.getBytes("UTF-8"), key.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            db.write(writeBatch);
            writeBatch.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String contractHash, String key) {
        try (DB db = getCustomContractHash(contractHash)) {
            return new String(db.get(key.getBytes("UTF-8")), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DB getSystemContractHash() throws IOException {
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        options.createIfMissing(true);
        return factory.open(new File("/Users/Aberic/Documents/tmp/troubleDB/level"), options);
    }

    private DB getCustomContractHash(String contractHash) throws IOException {
        String path = String.format("%s/%s", "/Users/Aberic/Documents/tmp/troubleDB/level", contractHash);
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        options.createIfMissing(true);
        return factory.open(new File(path), options);
    }

}
