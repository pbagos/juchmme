package org.joone.util;

import java.beans.PropertyEditorSupport;


/**
 * A property editor for the <code>format</code> property
 * of a <code>SnapshotRecorder</code> plugin.
 * 
 * @see org.joone.util.SnapshotRecorder
 *
 * @author Olivier Hussenet
 */
public class SnapshotFormatEditor extends PropertyEditorSupport {

    /** The supported snapshot formats */
    public final String [] formats =
    { /*SnapshotRecorder.VISAD_FORMAT,*/ SnapshotRecorder.JOONE_FORMAT };

    /**
     * Get an array of legal string values.
     *
     * @return an array of all legal string values
     */
    public String [] getTags () {
        return formats;
    }
}
