/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wms.jbook;

import java.io.File;
import java.nio.file.Path;
import nl.siegmann.epublib.epub.EpubReader;

/**
 *
 * @author stemenm
 */
public class JBookLibrarian implements JLibrarianInterface {
    
    
    EpubReader jReader = null;
    JBookLibraryInterface jLibrary = null;
    Path libraryPath = null;
    
    public JBookLibrarian( Path libraryPath )
    {
        this.libraryPath = libraryPath;
        jReader = new EpubReader();
        jLibrary = new JBookLibrary( libraryPath );
        
    }
   
    public JBookKey addBook(String fileName, int w, int h ) {
        return jLibrary.addLocalBook(fileName, w, h);
    }
    
    public JBookKey addBook( File file, int w, int h )
    {
        return jLibrary.addLocalBook(file, w, h);
    }

    public JBook getBook(JBookKey key) {
        return jLibrary.getBook(key);
    }

    @Override
    public JBookKey createBook(String JBookPath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}
