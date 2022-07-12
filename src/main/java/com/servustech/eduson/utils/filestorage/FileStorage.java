package com.servustech.eduson.utils.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Interface for file storage systems. Defines CRUD-like functionality for
 * dealing with file storage
 */
public interface FileStorage {

    /**
     * Saves the received multipart file and returns a key that can be used to
     * access the file. Uses the file name to generate the key
     *
     * @param file file to be stored
     * @return key to identify the stored file (full path for FS storage)
     **/
    String store(MultipartFile file);

    String store(java.io.File file);

    /**
     * Returns a File instance of the file corresponding to the provided key
     */
    InputStream retrieve(String key) throws FileStorageException;

    /**
     * Removes from file storage the file corresponding to the provided key
     */
    boolean delete(String key) throws FileStorageException;

    String getFileUrl(String fileName);

    void deleteFile(String path);

}
