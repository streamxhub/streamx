/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.console.base.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The file utils.
 */
public class FileUtils {

    /**
     * Read the end of the file.
     *
     * @param file    The file
     * @param maxSize Maximum size of read file
     * @return The file content
     * @throws IOException
     */
    public static byte[] readEndOfFile(File file, long maxSize) throws IOException {
        long readSize = maxSize;
        RandomAccessFile raFile = new RandomAccessFile(file, "r");
        if (raFile.length() > maxSize) {
            raFile.seek(raFile.length() - maxSize);
        } else if (raFile.length() < maxSize) {
            readSize = (int) raFile.length();
        }
        byte[] fileContent = new byte[(int) readSize];
        raFile.read(fileContent);
        return fileContent;
    }

}
