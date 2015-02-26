/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbook;

import java.io.File;
import nl.siegmann.epublib.epub.EpubReader;

/**
 *
 * @author stemenm
 */
public class JBookLibrarian implements JLibrarianInterface {
    
    
    EpubReader jReader = null;
    JBookLibraryInterface jLibrary = null;
    
    public JBookLibrarian()
    {
        jReader = new EpubReader();
        jLibrary = new JBookLibrary();
        
    }
   
    public JBookKey addBook(String fileName ) {
        return jLibrary.addLocalBook(fileName);
    }
    
    public JBookKey addBook( File file )
    {
        return jLibrary.addLocalBook(file);
    }

    public JBook getBook(JBookKey key) {
        return jLibrary.getBook(key);
    }
    
    
    
}
