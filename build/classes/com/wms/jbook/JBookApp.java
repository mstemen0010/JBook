/*
 * JBookApp.java
 */

package com.wms.jbook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class JBookApp extends SingleFrameApplication {

enum LookAndFeel
    {
        Default,
        Basic,
        Metal,
        Nimbus,
        Synth,
        Multi
        ;

           
        private static final List<LookAndFeel> VALUES = Collections.unmodifiableList(Arrays.asList(values()));            
        private static final int SIZE = VALUES.size();            
        private static final Random RANDOM = new Random();
        
        public boolean isSet()
        {
            return this != Default;
        }
    
        static public LookAndFeel rand()
        {  
            return VALUES.get(RANDOM.nextInt(SIZE));           
        }
        
       
    }
    
    
    // boolean tryNimbus = true;
    LookAndFeel myLookAndFeel = LookAndFeel.rand();
    
    // myLookAndFeel = LookAndFeel.Nimbus;
    @Override
    protected void startup() {
        myLookAndFeel = LookAndFeel.Nimbus;
        if( myLookAndFeel.isSet() )    
        {
            try {
            
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if( myLookAndFeel.toString().equals(info.getName() )) {
                    // if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } 
            catch (Exception e) {
                // If Nimbus is not available, you can set the GUI to another look and feel.
            }
        }
        else
        {
            System.out.println("Look and Feel not Set...");
        }
        JBookView guiInst = new JBookView(this);
        // guiInst.getFrame().setTitle("Look and Feel is: " + myLookAndFeel.toString() );
        show( guiInst );
        guiInst.setStatus("Look and Feel is: " + this.myLookAndFeel.toString());
    }
    
 
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JBookApp
     */
    public static JBookApp getApplication() {
        return Application.getInstance(JBookApp.class);
    }
    
    public String lookAndFeelIs()
    {
        return this.myLookAndFeel.toString();
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(JBookApp.class, args);
    }
}
