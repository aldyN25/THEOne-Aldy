/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aldyn
 */
public class SuccesRateReport extends Report implements MessageListener {

    private Map<DTNHost, Integer> creation;
    private Map<DTNHost, Integer> delivery;

    public SuccesRateReport() {
        init();
    }

    protected void init() {
        super.init();
        this.creation = new HashMap<DTNHost, Integer>();
        this.delivery = new HashMap<DTNHost, Integer>();
    }

    @Override
    public void newMessage(Message m) {
        if (isWarmup()) {
            addWarmupID(m.getAppID());
            return;
        }
        if (creation.containsKey(m.getFrom())) {
            creation.put(m.getFrom(), creation.get(m.getFrom()) + 1);
        } else {
            creation.put(m.getFrom(), 1);
        }
    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {

    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        if (isWarmup()) {
            addWarmupID(m.getId());
            return;
        }

        if (firstDelivery) {
            if (delivery.containsKey(m.getFrom())) {
                delivery.put(m.getFrom(), delivery.get(m.getFrom()) + 1);
            } else {
                delivery.put(m.getFrom(), 1);
            }
        }
    }

    @Override
    public void done() {
        for (Map.Entry<DTNHost, Integer> entry : creation.entrySet()) {
            DTNHost h = entry.getKey();
            int c = entry.getValue();
            int s;

            if (delivery.containsKey(h)) {
                s = delivery.get(h);
            } else {
                s = 0;
            }

            write(h.toString() + "\t Pesan terkirim " + s + '/' + c);

        }
        super.done();
    }
}
