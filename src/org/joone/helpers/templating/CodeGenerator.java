/*
 * CodeGenerator.java
 *
 * Created on September 21, 2005, 3:53 PM
 */

package org.joone.helpers.templating;
import java.io.StringWriter;
import java.util.Properties;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.joone.helpers.structure.NeuralNetMatrix;
import org.joone.net.NeuralNet;
import org.joone.net.NeuralNetLoader;

/**
 * This Class generates the source code to build the neural network passed
 * as parameter to the getCode method. It searches the file containing the 
 * template either in the file system or in the classpath.
 *
 * @author Paolo Marrone
 */
public class CodeGenerator {
    
    VelocityContext context = null;
    
    /** Creates a new instance of CodeGenerator */
    public CodeGenerator() throws Exception {
        context = init();
    }
    
    public CodeGenerator(Properties props) throws Exception {
        context = init(props);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        CodeGenerator me = new CodeGenerator();
        NeuralNetLoader loader = new NeuralNetLoader("xor.snet");
        NeuralNet nnet = loader.getNeuralNet();
        String code = me.getCode(nnet, 
                "codeTemplate.vm",
                "org.joone.test.templating",
                "TestClass");
        System.out.println(code);
    }
    
    protected VelocityContext init() throws Exception {
        Properties props = new Properties();
        // Specify two resource loaders to use: file and class 
        // TODO: Get the following settings from an external property file?
        props.setProperty("resource.loader","file, class");
        props.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
        props.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        props.setProperty("file.resource.loader.path", ".");
        props.setProperty("file.resource.loader.cache", "false");
        props.setProperty("file.resource.loader.modificationCheckInterval", "0");
        props.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
        props.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"); 
        return init(props);
    }
    
    protected VelocityContext init(Properties props) throws Exception {
        Velocity.init(props);
        return new VelocityContext();
    }
    
    public String getCode(NeuralNet nnet, 
            String templateName, 
            String packageName, 
            String className) {
        String message;
        StringWriter sw = new StringWriter();
        try {
            NeuralNetMatrix nMatrix = new NeuralNetMatrix(nnet.cloneNet());
            context.put("netDescriptor", nMatrix);
            context.put("package", packageName);
            context.put("class", className);
            
            Template template = null;
            
            try {
                template = Velocity.getTemplate(templateName);
                template.merge( context, sw );
            } catch( ResourceNotFoundException rnfe ) {
                message = "couldn't find the template";
                throw new Exception(message, rnfe);
            } catch( ParseErrorException pee ) {
                message = "syntax error : problem parsing the template";
                throw new Exception(message, pee);
            } catch( MethodInvocationException mie ) {
                message = "Exception threw in the template code";
                throw new Exception(message, mie);
            } catch( Exception e ) {
                e.printStackTrace();
            }
            
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return sw.toString().trim();
    }
}
