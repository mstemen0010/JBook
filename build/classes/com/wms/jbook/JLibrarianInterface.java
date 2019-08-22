/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wms.jbook;

/**
 *
 * @author stemenm
 */
public interface JLibrarianInterface {
        
    public JBookKey addBook( String fileName, int w, int h );
    public JBook getBook( JBookKey key );
    public JBookKey createBook( String JBookPath );
    
}
