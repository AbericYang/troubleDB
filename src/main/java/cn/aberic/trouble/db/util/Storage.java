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

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Aberic on 2018/10/17 11:19
 * @version 1.0
 * @see
 * @since 1.0
 */
public class Storage<V> {

    private static final long ALIVE_TIME = 6000L;
    private long lastInvokeTime = 0L;
    private boolean inUsed;
    private File file;

    Storage(String path) {
        this.file = file(path);
    }

    V write(V value) {
        inUsed = true;
        try {
            Files.asCharSink(file, Charset.forName("UTF-8")).write(JSON.toJSONString(value));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            inUsed = false;
            lastInvokeTime = System.currentTimeMillis();
        }
        return value;
    }

    public boolean out() {
        return !inUsed && System.currentTimeMillis() - lastInvokeTime > ALIVE_TIME;
    }

    /**
     * 获取一个文件对象，如果没有，则创建出来并获取
     *
     * @param filePath 文件完整路径及文件名
     * @return 创建的文件
     */
    static final File file(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                Files.createParentDirs(file); // 创建新文件的父目录
                file.createNewFile(); // 创建新文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
