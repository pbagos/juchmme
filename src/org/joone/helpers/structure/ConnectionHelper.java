/*
 * ConnectionHelper.java
 *
 * Created on March 16, 2006, 5:11 PM
 *
 * Copyright @2005 by Paolo Marrone and the Joone team
 * Licensed under the Lesser General Public License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.joone.helpers.structure;

import java.util.Vector;
import org.joone.engine.InputPatternListener;
import org.joone.engine.Layer;
import org.joone.engine.OutputSwitchSynapse;
import org.joone.engine.Synapse;
import org.joone.engine.learning.ComparingElement;
import org.joone.io.InputConnector;
import org.joone.io.InputSwitchSynapse;
import org.joone.io.StreamInputSynapse;
import org.joone.io.StreamOutputSynapse;
import org.joone.util.AbstractConverterPlugIn;
import org.joone.util.ConverterPlugIn;
import org.joone.util.LearningSwitch;
import org.joone.util.OutputConverterPlugIn;

/**
 * This class permits to easily make connections between elements of a neural network.
 * In this class all the needed logic is already implemented.
 *
 * @author P.Marrone
 */
public class ConnectionHelper {
    
    /**
     * Checks if two elements of a neural network can be attached
     * Warning: This method is sensitive to the order of the elements.
     * Example:
     * canConnect(Layer, StreamInputSynapse) returns false
     * canConnect(StreamInputSynapse, Layer) returns true
     * @param source he source element
     * @param target The target element
     * @return true if the connection can be established
     */
    public static boolean canConnect(Object source, Object target) {
        boolean retValue = false;
       
        if (source == target) {
            // An object cannot connect to itself
            return false;
        }
        
        if (target instanceof InputConnector) {
            if (source instanceof LearningSwitch) {
                if (((LearningSwitch)source).getValidationSet() == null)
                    if (!((StreamInputSynapse)target).isInputFull())
                        retValue = true;
                return retValue;
            }
            if (!((InputConnector)target).isOutputFull())
                if (source instanceof StreamInputSynapse)
                    retValue = true;
            return retValue;
        }
        
        if (target instanceof LearningSwitch) {
            if (((LearningSwitch)target).getTrainingSet() == null)
                if (source instanceof StreamInputSynapse)
                    if (!((StreamInputSynapse)source).isInputFull())
                        retValue = true;
            return retValue;
        }
        
        if (target instanceof InputSwitchSynapse) {
            if (source instanceof StreamInputSynapse)
                if (!((StreamInputSynapse)source).isInputFull())
                    retValue = true;
            return retValue;
        }
        
        
        if (target instanceof Layer) {
            if (source instanceof Layer)
                retValue = true;
            if ((source instanceof InputPatternListener) &&
                    !(source instanceof StreamOutputSynapse))
                if (!((InputPatternListener)source).isInputFull())
                    retValue = true;
            return retValue;
        }
        
        if (target instanceof StreamInputSynapse) {
            if (source instanceof LearningSwitch)
                if (((LearningSwitch)source).getValidationSet() == null)
                    if (!((StreamInputSynapse)target).isInputFull())
                        retValue = true;
            if (source instanceof ConverterPlugIn)
                if (!((ConverterPlugIn)source).isConnected())
                    retValue = true;
            return retValue;
        }
        
        if (target instanceof StreamOutputSynapse) {
            StreamOutputSynapse sos = (StreamOutputSynapse)target;
            if (!sos.isOutputFull()) {
                if (source instanceof Layer)
                    retValue = true;
                if (source instanceof ComparingElement)
                    retValue = true;
                if (source instanceof OutputConverterPlugIn)
                    if (!((OutputConverterPlugIn)source).isConnected())
                        retValue = true;
                if (source instanceof OutputSwitchSynapse)
                    retValue = true;
            }
            return retValue;
        }
        
        if (target instanceof ComparingElement) {
            if (source instanceof Layer)
                if (!((ComparingElement)target).isOutputFull())
                    retValue = true;
            if (source instanceof StreamInputSynapse)
                if (((ComparingElement)target).getDesired() == null)
                    if (!((StreamInputSynapse)source).isInputFull())
                        retValue = true;
            return retValue;
        }
        
        if (target instanceof AbstractConverterPlugIn) {
            if (source instanceof ConverterPlugIn)
                if (!((ConverterPlugIn)source).isConnected())
                    retValue = true;
            return retValue;
        }
        
        if (target instanceof OutputSwitchSynapse) {
            OutputSwitchSynapse oss = (OutputSwitchSynapse)target;
            if (!oss.isOutputFull()) {
                if (source instanceof Layer)
                    retValue = true;
                if (source instanceof ComparingElement)
                    retValue = true;
                if (source instanceof OutputSwitchSynapse)
                    retValue = true;
            }
        }
        return retValue;
    }
    
    /**
     * Connects two elements of a neural network
     * Warning: This method is sensitive to the order of the elements.
     * Example:
     * connect(Layer, null, StreamInputSynapse) returns false
     * connect(StreamInputSynapse, null, Layer) returns true
     *
     * @param source The source element
     * @param target The target element
     * @param media If both source and target are Layers, this parameter contains the Synapse to use to connect them, otherwise null
     * @return true if the connection has been established
     */
    public static boolean connect(Object source, Object media, Object target) {
        boolean retValue = false;
        if (target instanceof InputConnector) {
            if (source instanceof LearningSwitch) {
                return ((LearningSwitch)source).addValidationSet((StreamInputSynapse)target);
            }
            if (source instanceof StreamInputSynapse)
                retValue = ((InputConnector)target).setInputSynapse((StreamInputSynapse)source);
            return retValue;
        }
        
        if (target instanceof LearningSwitch) {
            if (source instanceof StreamInputSynapse)
                retValue = ((LearningSwitch)target).addTrainingSet((StreamInputSynapse)source);
            return retValue;
        }
        
        if (target instanceof InputSwitchSynapse) {
            if (source instanceof StreamInputSynapse)
                retValue = ((InputSwitchSynapse)target).addInputSynapse((StreamInputSynapse)source);
            return retValue;
        }
        
        
        if (target instanceof Layer) {
            retValue = connectToLayer(source, media, (Layer)target);
            return retValue;
        }
        
        if (target instanceof StreamInputSynapse) {
            if (source instanceof LearningSwitch) {
                retValue = ((LearningSwitch)source).addValidationSet((StreamInputSynapse)target);
            }
            if (source instanceof ConverterPlugIn) {
                retValue = ((StreamInputSynapse)target).addPlugIn((ConverterPlugIn)source);
            }
            return retValue;
        }
        
        if (target instanceof StreamOutputSynapse) {
            retValue = connectToStreamOutputSynapse(source, (StreamOutputSynapse)target);
            return retValue;
        }
        
        if (target instanceof ComparingElement) {
            retValue = connectToComparingElement(source, (ComparingElement)target);
            return retValue;
        }
        
        if (target instanceof AbstractConverterPlugIn) {
            if (source instanceof ConverterPlugIn) {
                retValue = ((AbstractConverterPlugIn)target).addPlugIn((ConverterPlugIn)source);
            }
            return retValue;
        }
        
        if (target instanceof OutputSwitchSynapse) {
            retValue = connectToOutputSwitchSynapse(source, (OutputSwitchSynapse)target);
        }
        return retValue;
    }
    
    private static boolean connectToLayer(Object source, Object media, Layer target) {
        boolean retValue = false;
        if (source instanceof Layer) {
            if ((media != null) && (media instanceof Synapse)) {
                if (((Layer)source).addOutputSynapse((Synapse)media)) {
                    retValue = target.addInputSynapse((Synapse)media);
                }
            }
        }
        if (source instanceof InputPatternListener) {
            retValue = target.addInputSynapse((InputPatternListener)source);
        }
        
        return retValue;
    }
    
    private static boolean connectToStreamOutputSynapse(Object source, StreamOutputSynapse target) {
        boolean retValue = false;
        if (source instanceof Layer)
            retValue = ((Layer)source).addOutputSynapse(target);
        if (source instanceof ComparingElement)
            retValue = ((ComparingElement)source).addResultSynapse(target);
        if (source instanceof OutputConverterPlugIn)
            retValue = target.addPlugIn((OutputConverterPlugIn)source);
        if (source instanceof OutputSwitchSynapse)
            retValue = ((OutputSwitchSynapse)source).addOutputSynapse(target);
        return retValue;
    }
    
    private static boolean connectToComparingElement(Object source, ComparingElement target) {
        boolean retValue = false;
        if (source instanceof Layer)
            retValue = ((Layer)source).addOutputSynapse(target);
        if (source instanceof StreamInputSynapse)
            retValue = target.setDesired((StreamInputSynapse)source);
        return retValue;
    }
    
    private static boolean connectToOutputSwitchSynapse(Object source, OutputSwitchSynapse target) {
        boolean retValue = false;
        if (source instanceof Layer)
            retValue = ((Layer)source).addOutputSynapse(target);
        if (source instanceof ComparingElement)
            retValue = ((ComparingElement)source).addResultSynapse(target);
        if (source instanceof OutputSwitchSynapse)
            retValue = ((OutputSwitchSynapse)source).addOutputSynapse(target);
        return retValue;
    }
    
    /**
     * Disconnects two elements.
     * Warning: This method is sensitive to the order of the elements.
     * If this method returns false when called with (obj1, obj2) as parameters,
     * you should recall it using the parameters in reverse order (obj2, obj1)
     * @param source The source element to disconnect
     * @param target The target element to disconnect
     * @return true if the two elements have been disconnected
     */
    public static boolean disconnect(Object source, Object target) {
        boolean retValue = false;
        if (target instanceof InputConnector) {
            if (source instanceof StreamInputSynapse)
                retValue = ((InputConnector)target).setInputSynapse(null);
            return retValue;
        }
        
        if (target instanceof LearningSwitch) {
            if (source instanceof StreamInputSynapse) {
                if (((LearningSwitch)target).getTrainingSet() == source) {
                    ((LearningSwitch)target).removeTrainingSet();
                    retValue = true;
                }
                if (((LearningSwitch)target).getValidationSet() == source) {
                    ((LearningSwitch)target).removeValidationSet();
                    retValue = true;
                }
            }
            return retValue;
        }
        
        if (target instanceof InputSwitchSynapse) {
            if (source instanceof StreamInputSynapse)
                retValue = ((InputSwitchSynapse)target).removeInputSynapse(((StreamInputSynapse)source).getName());
            return retValue;
        }
        
        
        if (target instanceof Layer) {
            retValue = disconnectFromLayer(source, (Layer)target);
            return retValue;
        }
        
        if (target instanceof StreamInputSynapse) {
            if (source instanceof ConverterPlugIn) {
                retValue = ((StreamInputSynapse)target).addPlugIn(null);
            }
            return retValue;
        }
        
        if (target instanceof StreamOutputSynapse) {
            retValue = disconnectFromStreamOutputSynapse(source, (StreamOutputSynapse)target);
            return retValue;
        }
        
        if (target instanceof ComparingElement) {
            retValue = disconnectFromComparingElement(source, (ComparingElement)target);
            return retValue;
        }
        
        if (target instanceof AbstractConverterPlugIn) {
            if (source instanceof ConverterPlugIn) {
                retValue = ((AbstractConverterPlugIn)target).addPlugIn(null);
            }
            return retValue;
        }
        
        if (target instanceof OutputSwitchSynapse) {
            retValue = disconnectFromOutputSwitchSynapse(source, (OutputSwitchSynapse)target);
        }
        return retValue;
    }
    
    private static boolean disconnectFromLayer(Object source, Layer target) {
        boolean retValue = false;
        if (source instanceof Layer) {
            Object media = getConnection((Layer)source, target);
            if ((media != null) && (media instanceof Synapse)) {
                ((Layer)source).removeOutputSynapse((Synapse)media);
                target.removeInputSynapse((Synapse)media);
                retValue = true;
            }
        }
        if (source instanceof InputPatternListener) {
            target.removeInputSynapse((InputPatternListener)source);
            retValue = true;
        }
        return retValue;
    }
    
    private static boolean disconnectFromStreamOutputSynapse(Object source, StreamOutputSynapse target) {
        boolean retValue = false;
        if (source instanceof Layer) {
            ((Layer)source).removeOutputSynapse(target);
            retValue = true;
        }
        if (source instanceof ComparingElement) {
            ((ComparingElement)source).removeResultSynapse(target);
            retValue = true;
        }
        if (source instanceof OutputConverterPlugIn)
            retValue = target.addPlugIn(null);
        if (source instanceof OutputSwitchSynapse)
            retValue = ((OutputSwitchSynapse)source).removeOutputSynapse(target.getName());
        return retValue;
    }
    
    private static boolean disconnectFromComparingElement(Object source, ComparingElement target) {
        boolean retValue = false;
        if (source instanceof Layer) {
            ((Layer)source).removeOutputSynapse(target);
            retValue = true;
        }
        if (source instanceof StreamInputSynapse)
            retValue = target.setDesired(null);
        return retValue;
    }
    
    private static boolean disconnectFromOutputSwitchSynapse(Object source, OutputSwitchSynapse target) {
        boolean retValue = false;
        if (source instanceof Layer) {
            ((Layer)source).removeOutputSynapse(target);
            retValue = true;
        }
        if (source instanceof ComparingElement) {
            ((ComparingElement)source).removeResultSynapse(target);
            retValue = true;
        }
        if (source instanceof OutputSwitchSynapse)
            retValue = ((OutputSwitchSynapse)source).removeOutputSynapse(target.getName());
        return retValue;
    }
    
    // Searches the synapse that connects two Layers
    private static Object getConnection(Layer source, Layer target) {
        Object conn = null;
        Vector inps = target.getAllInputs();
        Vector outs = source.getAllOutputs();
        if ((inps != null) && (inps.size() > 0) && (outs != null) && (outs.size() > 0)) {
            for (int i=0; (conn == null) && (i < inps.size()); ++i) {
                Object cc = inps.elementAt(i);
                if (cc instanceof Synapse) {
                    for (int u=0; (conn == null) && (u < outs.size()); ++u) {
                        if (outs.elementAt(u) == cc) {
                            conn = cc;
                        }
                    }
                }
            }
        }
        return conn;
    }
}
