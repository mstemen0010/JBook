/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wms.jbook;

import java.io.File;

/**
 *
 * @author stemenm
 */
public interface JBookLibraryInterface {
    
    public JBookKey addLocalBook( String fileName, int w, int h );
    
    public JBookKey addLocalBook( File file, int w, int h );
    
    public JBook getBook( JBookKey key );
    
    
    
}
