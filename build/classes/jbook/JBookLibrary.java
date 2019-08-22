/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import nl.siegmann.epublib.epub.EpubReader;

/**
 *
 * @author stemenm
 */
public class JBookLibrary implements JBookLibraryInterface {

    HashMap< JBookKey, JBook> JLibrary = new HashMap<>();
    EpubReader ePubReader = new EpubReader();
    FileInputStream fs = null;

    public JBookKey addLocalBook(String fileName) {
        JBookKey newKey = null;
        try {
            FileReader reader = new FileReader(fileName);
            fs = new FileInputStream(fileName);
            if (reader != null) {
                try {
                    // Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));
                    JBook tempBook = new JBook(ePubReader.readEpub(fs));
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
    public JBookKey addLocalBook(File file) {
        JBookKey newKey = null;
        try {
            FileReader reader = new FileReader(file);
            if (reader != null) {
                try {
                    // Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));
                    JBook tempBook = new JBook(ePubReader.readEpub(fs));
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
