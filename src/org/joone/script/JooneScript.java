package org.joone.script;

import bsh.*;
import java.io.*;

import org.joone.log.*;

public class JooneScript 
{
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger (JooneScript.class);
    
    private Interpreter jShell;
    
    public JooneScript(){
        // constructor
    }
    
    private Interpreter getShell() {
        if (jShell == null) {
            jShell = new Interpreter();
            // set up all required stuff to run Joone
            try {
                jShell.eval("import org.joone.engine.*;");
                jShell.eval("import org.joone.engine.learning.*;");
                jShell.eval("import org.joone.edit.*;");
                jShell.eval("import org.joone.util.*;");
                jShell.eval("import org.joone.net.*;");
                jShell.eval("import org.joone.io.*;");
                jShell.eval("import org.joone.script.*;");
                
            } catch (EvalError err)
            {
                // TO DO :
                //      Evaluate if an simple System.out is not 
                //      more approprate here ?
                log.error ( "EvalError thrown. Message is " + err.getMessage(),
                            err);
                return null;
            }
        }
        return jShell;        
    }
    
    public static void main(String [] args){
        if (args.length == 0) {
            System.out.println("Usage: java org.joone.script.JooneScript <script_file>");
        }
        else {
            JooneScript jScript = new JooneScript();
            jScript.source(args[0]);
        }
        
    }
    
    //
    // TO DO :
    //   Check if it is not better to leave a simple 
    //                  System.out or 
    //                  System.err
    // 
    public void source(String fileName){
        try 
        {
            getShell().source(fileName);
        } catch (FileNotFoundException fnfe)  
        {            
            log.error ( "FileNotFoundException thrown. Message is : " + fnfe.getMessage(), 
                         fnfe);                    
        } catch (IOException ioe)  
        {            
            log.error ( "IOException thrown. Message is : " + ioe.getMessage(), 
                         ioe);                    
        } catch (EvalError err)  
        {
            log.warn ( "EvalError thrown. Message is : " + err.getMessage(), 
                         err );
            System.out.println("Invalid BeanShell code!");         
        }        
    }
    
    public void eval(String script){
        try {
            getShell().eval(script);
        } catch (EvalError err)
        {
            log.warn ( "Error while evaluating. Message is : " + err.getMessage(),
                       err );
            System.out.println("Invalid BeanShell code!");
            err.printStackTrace();
        }
    }
    
    public void set(String name, Object jObject){
        try {
            getShell().set(name, jObject);
        }
        catch(EvalError err){
            err.printStackTrace();
        }
    }
}