package org.joone.script;

import groovy.lang.*;
import org.codehaus.groovy.control.*;
import java.io.*;

import org.joone.log.*;

public class JooneGroovyScript {
    /**
     * Logger
     * */
    private static final ILogger log = LoggerFactory.getLogger(JooneGroovyScript.class);
    
    //
    // Groovy does not evaluate import statement as a single expression. As a workaround,
    // we need to append a list of import statements in front of every expression.
    //
    // For example, the following code will throw an exception
    //
    // groovyShell.evaluate("import groovy.swing.SwingBuilder");
    // groovyShell.evaluate("swing = new SwingBuilder()");
    //
    // while the following code works fine
    //
    // groovyShell.evaluate("import groovy.swing.SwingBuilder\nswing = new SwingBuilder()");
    //
    // In the future, groovy may support groovyShell.setImport("groovy.swing.SwingBuilder").
    // During that time, we should drop out the import prefix string workaround.
    //
    private static final String GROOVY_IMPORT_PREFIX =
    "import org.joone.engine.*\n"+
    "import org.joone.engine.learning.*\n"+
    "import org.joone.edit.*\n"+
    "import org.joone.util.*\n"+
    "import org.joone.net.*\n"+
    "import org.joone.io.*\n"+
    "import org.joone.script.*\n";
    
    private GroovyShell groovyShell;
    
    public JooneGroovyScript(){
        // constructor
    }
    
    private GroovyShell getShell() {
        if (groovyShell == null) {
            groovyShell = new GroovyShell();
        }
        return groovyShell;
    }
    
    public static void main(String [] args){
        if (args.length == 0) {
            System.out.println("Usage: java org.joone.script.JooneGroovyScript <script_file>");
        }
        else {
            JooneGroovyScript jScript = new JooneGroovyScript();
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
        try {
            getShell().run(new File(fileName), new String[0]) ;
        } catch (CompilationFailedException cfe) {
            log.error( "CompilationFailedException thrown. Message is : " + cfe.getMessage(),
            cfe);
        } catch (IOException ioe) {
            log.error( "IOException thrown. Message is : " + ioe.getMessage(),
            ioe);
        }
    }
    
    public void eval(String script){
        try {
            getShell().evaluate(GROOVY_IMPORT_PREFIX+script);
        } catch (CompilationFailedException cfe) {
            log.error( "CompilationFailedException thrown. Message is : " + cfe.getMessage(),
            cfe);
        } 
        catch (IOException ioe) {
            log.error( "IOException thrown. Message is : " + ioe.getMessage(),
            ioe);
        }
    }
    
    public void set(String name, Object jObject){
        getShell().setVariable(name, jObject);
    }
}
