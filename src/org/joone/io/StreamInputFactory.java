
package org.joone.io;


import java.io.*;

public class StreamInputFactory {
    
    public StreamInputFactory() {}
    
    public StreamInputSynapse getInput(String streamName) throws IOException {
        if ((streamName.startsWith("http://")) || (streamName.startsWith("ftp://"))) {
            URLInputSynapse uis = new URLInputSynapse();
            uis.setURL(streamName);
            return uis;
        } else {
            FileInputSynapse fis = new FileInputSynapse();
            fis.setInputFile(new File(streamName));
            return fis;
        }
        
    }
}