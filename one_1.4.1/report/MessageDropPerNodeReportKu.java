/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.DTNHost;
import core.Message;
import core.MessageListener;

/**
 * Report for generating different kind of total statistics about message
 * relaying performance. Messages that were created during the warm up period
 * are ignored.
 * <P>
 * <strong>Note:</strong> if some statistics could not be created (e.g. overhead
 * ratio if no messages were delivered) "NaN" is reported for double values and
 * zero for integer median(s).
 */
public class MessageDropPerNodeReportKu extends Report implements MessageListener {

    private Map<DTNHost, Integer> droppedNode;

    /**
     * Constructor.
     */
    public MessageDropPerNodeReportKu() {
        init();
    }

    @Override
    protected void init() {
        super.init();
        this.droppedNode = new HashMap<DTNHost, Integer>();
    }

   
    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
    }
    
    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
        if (dropped) {
        
            if ( droppedNode.containsKey(where)) {
                droppedNode.put(where, droppedNode.get(where) + 1);
            } else {
                droppedNode.put(where, 1);
            }
        }
    }
      
    @Override
    public void newMessage(Message m) {
    }
 
    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
    }
  
    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
    }

    @Override
    public void done() {
       String println = "Host\tTotal Drop\n";
       for (Map.Entry<DTNHost, Integer> entry : droppedNode.entrySet()){
           DTNHost key = entry.getKey();
           Integer value = entry.getValue();
           println += key + "\t" + value + "\n";
       } 

        write(println);
        super.done();
    }


}
