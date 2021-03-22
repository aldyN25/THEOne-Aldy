/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.DTNHost;
import core.Settings;
import core.SimClock;
import core.UpdateListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aldyn
 */
public class BufferOccupancyReportKu extends Report implements UpdateListener {

    /**
     * Record occupancy every nth second -setting id ({@value}). Defines the
     * interval how often (seconds) a new snapshot of buffer occupancy is taken
     */
    public static final String BUFFER_REPORT_INTERVAL = "occupancyInterval";
    /**
     * Default value for the snapshot interval
     */
    public static final int DEFAULT_BUFFER_REPORT_INTERVAL = 5;

    private double lastRecord = Double.MIN_VALUE;
    private int interval;
    private Map<DTNHost, Double> bufferNode = new HashMap<DTNHost, Double>();
    private Map<DTNHost, LinkedList<Double>> bufferCounts = new HashMap<DTNHost, LinkedList<Double>>();
    private int updateCounter = 0;

    private Map<DTNHost, Double> mapofBuffer;
    private HashMap<DTNHost, LinkedList<Double>> nodeofInterval = new HashMap<DTNHost, LinkedList<Double>>();

    /**
     * Creates a new BufferOccupancyReport instance.
     */
    public BufferOccupancyReportKu() {
        super();
        mapofBuffer = new HashMap<>();

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
    public void updated(List<DTNHost> hosts) {
        if (isWarmup()) {
            return;
        }
        if (SimClock.getTime() - lastRecord >= interval) {
            lastRecord = SimClock.getTime();
            printLine(hosts);
            updateCounter++;
        }
    }

    /**
     * Prints a snapshot of the average buffer occupancy
     *
     * @param hosts The list of hosts in the simulation
     */
    private void printLine(List<DTNHost> hosts) {
        for (DTNHost h : hosts) {
            double tmp = h.getBufferOccupancy();
            tmp = (tmp <= 100.0) ? (tmp) : (100.0);

            LinkedList<Double> sampleBuffer;
            if (!bufferCounts.containsKey(h)) {
                sampleBuffer = new LinkedList<>();

            } else {
                sampleBuffer = bufferCounts.get(h);
            }
            sampleBuffer.add(tmp);
            bufferCounts.put(h, sampleBuffer);
        }
//        double bufferOccupancy = 0.0;
//        double bo2 = 0.0;
//        // example 1
//        for (DTNHost h : hosts) {
//            double tmp = h.getBufferOccupancy();
//            tmp = (tmp <= 100.0) ? (tmp) : (100.0);
//            if (bufferNode.containsKey(h)) {
//                bufferNode.put(h, bufferNode.get(h) + tmp);
//            } else {
//                bufferNode.put(h, tmp);
//            }
////
//            //example 2
//            mapofBuffer.put(h, !mapofBuffer.containsKey(h) ? tmp: mapofBuffer.get(h) + tmp);
//        }
    }

    @Override
    public void done() {
        
//        write("Node\tAverage Buffer");
//        // example 1
//        for (Map.Entry<DTNHost, Double> entrySet : bufferNode.entrySet()) {
//            DTNHost key = entrySet.getKey();
//            Double value = entrySet.getValue();
//            Double avgBuffer = entrySet.getValue() / updateCounter;
//            write(key + "\t" + avgBuffer);
//
//        }
//        // example 2
//        write("\n");
//        write("Node\tAverage Buffer");
//        for (Map.Entry<DTNHost, Double> entry : mapofBuffer.entrySet()){
//            DTNHost key = entry.getKey();
//            Double value = entry.getValue();
//            write(key + "\t" + value);
//        }
        // example 3 
        write("Latian BufferOccupancyPerNodeReport");
        for (Map.Entry<DTNHost, LinkedList<Double>> entry : bufferCounts.entrySet()) {
            DTNHost key = entry.getKey();
            String printLn = key + "";
            for ( Double occValue : entry.getValue()) {
                printLn += "\t" + occValue;
            }
//            LinkedList<Double> occValue = entry.getValue();
//            printLn += key + " \t " + occValue;
            write(printLn);
        }
        super.done();
    }

}
