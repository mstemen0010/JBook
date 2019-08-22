/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbook;

import java.io.File;

/**
 *
 * @author stemenm
 */
public interface JBookLibraryInterface {
    
    public JBookKey addLocalBook( String fileName );
    
    public JBookKey addLocalBook( File file );
    
    public JBook getBook( JBookKey key );
    
    
    
}
