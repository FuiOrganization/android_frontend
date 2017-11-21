package br.com.fui.fuiapplication.cache;

import java.io.File;

/**
 * Created by guilherme on 21/11/17.
 */

public class DiskCache {
    /**
     * Deletes a directory tree recursively.
     */
    public static void deleteDirectoryTree(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteDirectoryTree(child);
            }
        }

        fileOrDirectory.delete();
    }
}
