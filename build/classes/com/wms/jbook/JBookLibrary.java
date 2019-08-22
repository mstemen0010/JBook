/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wms.jbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 *
 * @author stemenm
 */
public class JBookLibrary implements JBookLibraryInterface {

    HashMap< JBookKey, JBook> JLibrary = new HashMap<>();
    EpubReader ePubReader = new EpubReader();
    FileInputStream fs = null;
    Path libraryPath = null;
    
    public JBookLibrary( Path jBookPath )
    {
        this.libraryPath = jBookPath;
    }

    public JBookKey addLocalBook(String fileName, int w, int h) {
        JBookKey newKey = null;
        try {
            FileReader reader = new FileReader(fileName);
            fs = new FileInputStream(fileName);
            if (reader != null) {
                try {
                    // Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));\
                    Book readBook = ePubReader.readEpub(fs);
                    JBook tempBook = new JBook(readBook, this.libraryPath, w, h);
                    if (tempBook != null) {
                        newKey = tempBook.getJBookKey();
                        JLibrary.put(newKey, tempBook);
                    }
                } catch (IOException ex) {
                }
            }
        } catch (FileNotFoundException ex) {
        }

        return newKey;
    }

    @Override
    public JBook getBook(JBookKey key) {
        return this.JLibrary.get(key);
    }

    @Override
    public JBookKey addLocalBook(File file, int w, int h) {
        JBookKey newKey = null;
        try {
            FileReader reader = new FileReader(file);
            if (reader != null) {
                try {
                    // Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));
                    Book readBook = ePubReader.readEpub(fs);
                    JBook tempBook = new JBook(readBook, this.libraryPath, w, h );
                    if (tempBook != null) {
                        newKey = tempBook.getJBookKey();
                        JLibrary.put(newKey, tempBook);
                    }
                } catch (IOException ex) {
                }
            }
        } catch (FileNotFoundException ex) {
        }
        return newKey;
    }
}
