/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;
import core.SimClock;
import core.UpdateListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static report.BufferOccupancyReportKu.BUFFER_REPORT_INTERVAL;
import static report.BufferOccupancyReportKu.DEFAULT_BUFFER_REPORT_INTERVAL;

/**
 *
 * @author aldyn
 */
public class CopyPesan extends Report implements MessageListener, UpdateListener {

    public static final String BUFFER_REPORT_INTERVAL = "occupancyInterval";
    private double lastRecord = Double.MIN_VALUE;
    public static final int DEFAULT_REPORT_INTERVAL = 1000;
    private int interval;
    private int updateCounter;
    private int nrofRelayed;

    private Map<Integer, Integer> jmlhCopyPesan = new HashMap<Integer, Integer>();

    @Override
    public void newMessage(Message m) {
//        if (isWarmup()) {
//            addWarmupID(m.getId());
//            return;
//        }
    }

    public CopyPesan() {
        super();
        this.nrofRelayed = 0;
        this.updateCounter = 0;
        Settings settings = getSettings();
        if (settings.contains(BUFFER_REPORT_INTERVAL)) {
            interval = settings.getInt(BUFFER_REPORT_INTERVAL);
        } else {
            interval = -1; /* not found; use default */

        }

        if (interval < 0) { /* not found or invalid value -> use default */

            interval = DEFAULT_BUFFER_REPORT_INTERVAL;
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
        this.nrofRelayed++;
    }

    @Override
    public void updated(List<DTNHost> hosts) {
        if (isWarmup()) {
            return;
        }
        if (SimClock.getTime() - lastRecord >= interval) {
            lastRecord = SimClock.getTime();
            printLine();
            updateCounter++;
        }
    }

    private void printLine() {
        jmlhCopyPesan.put(updateCounter, nrofRelayed);
    }

    @Override
    public void done() {
        write("Time\tEpidemic\tMessage\n");
        for (Map.Entry<Integer, Integer> entry : jmlhCopyPesan.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            String hasil = key + "\t" + value;
            write(hasil);
        }
        super.done();
    }
}
