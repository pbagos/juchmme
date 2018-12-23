package org.joone.net;

import org.joone.engine.NeuralLayer;
import org.joone.engine.InputPatternListener;
import org.joone.engine.OutputPatternListener;
import org.joone.engine.Monitor;

/**
 * Class to represent a network validation check error.
 * NetChecks of FATAL severity prevent the network from running,
 * NetChecks of WARNING severity do not.
 */
public class NetCheck implements Comparable {

    /** Fatal check severity. */
    public static final int FATAL = 0;

    /** Non-fatal check severity. */
    public static final int WARNING = 1;

    /** The check severity. */
    private int severity;

    /** The check message. */
    private String message;

    /**
     * The network source producing the check.
     */
    private Object source;

    /**
     * Constructor.
     * 
     * @param severityArg the severity of the check. Should be FATAL or WARNING.
     * @param messageArg the message assosiated with the check.
     * @param objectArg the network component producing the check.
     */
    public NetCheck(int severityArg, String messageArg, Object objectArg) {
        setSeverity(severityArg);
        setMessage(messageArg);
        setSource(objectArg);
    }

    /**
     * Produce a String representation of the check.
     *
     * @return the String representation of the check.
     */
    public String toString() {

        // Get the class name without the package extension.
        String className = getSource().getClass().getName();
        className = className.substring(1 + className.lastIndexOf("."));

        // Get the instance name. Try to find the Joone interface name if possible.
        String instanceName;
        if (getSource() instanceof NeuralLayer) {
            instanceName = ((NeuralLayer) getSource()).getLayerName();
        } else if (getSource() instanceof InputPatternListener) {
            instanceName = ((InputPatternListener) getSource()).getName();
        } else if (getSource() instanceof OutputPatternListener) {
            instanceName = ((OutputPatternListener) getSource()).getName();
        } else if (getSource() instanceof Monitor) {
            instanceName = "Monitor";
        } else {
            instanceName = getSource().toString();
        }

        // Build up the check message.
        StringBuffer checkMessage = new StringBuffer();
        if (isFatal()) {
            checkMessage.append("FATAL - ");
        } else if (isWarning()) {
            checkMessage.append("WARNING - ");
        }
        checkMessage.append(className);
        checkMessage.append(" - ");

        if (instanceName != null && !instanceName.trim().equals("")) {
            checkMessage.append(instanceName);
            checkMessage.append(" - ");
        }

        checkMessage.append(getMessage());

        return checkMessage.toString();
    }

    /**
     * Method to see if this check is a WARNING.
     *
     * @return true if warning.
     */
    public boolean isWarning() {
        return getSeverity() == WARNING;
    }

    /**
     * Method to see if this check is a FATAL.
     * 
     * @return true if error.
     */
    public boolean isFatal() {
        return getSeverity() == FATAL;
    }

    /**
     * Method to order by message when in TreeSet.
     *
     * @return true if error.
     */
    public int compareTo(Object o) {
        if (o instanceof NetCheck) {
            NetCheck nc = (NetCheck) o;
            return toString().compareTo(nc.toString());
        } else {
            return 0;
        }
    }
    
    /**
     * Getter for the object that caused the error
     * @return the source object
     */
    public Object getSource() {
        return source;
    }

    /**
     * Setter for the object that caused the error
     * @param source the source object 
     */
    public void setSource(Object source) {
        this.source = source;
    }

    /**
     * Getter for the error message
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the error message
     * @param message the error message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for the error severity
     * 
     * @return the error severity (either NetCheck.FATAL or NetCheck.WARNING)
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Setter for the error severity
     * 
     * @param severity the error severity (either NetCheck.FATAL or NetCheck.WARNING)
     */
    public void setSeverity(int severity) {
        this.severity = severity;
    }
}
