/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbook;

/**
 *
 * @author stemenm
 */
public interface JLibrarianInterface {
    
    
    public JBookKey addBook( String fileName );
    public JBook getBook( JBookKey key );
    
    
}
