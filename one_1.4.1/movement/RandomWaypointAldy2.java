/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import core.Coord;
import core.DTNHost;
import core.Settings;
import core.SimScenario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static movement.MovementModel.rng;

/**
 * Random waypoint movement model. Creates zig-zag paths within the simulation
 * area.
 */
public class RandomWaypointAldy2 extends MovementModel {

    /**
     * how many waypoints should there be per path
     */
    private static final int PATH_LENGTH = 1;
    private Coord lastWaypoint;
    private Coord startLoc;
    private Coord location;

    private List<Coord> tujuan = new ArrayList<Coord>();
    private Map<Coord, Double> jaraknya = new HashMap<Coord, Double>();

    public RandomWaypointAldy2(Settings settings) {
        super(settings);

        int[] coords = settings.getCsvInts("nodeLocation", 2);
        this.location = new Coord(coords[0], coords[1]);
        this.startLoc = new Coord(coords[0], coords[1]);

    }

    protected RandomWaypointAldy2(RandomWaypointAldy2 rwp) {
        super(rwp);
        this.location = rwp.location;
        this.startLoc = rwp.startLoc;
    }

    /**
     * Returns a possible (random) placement for a host
     *
     * @return Random position on the map
     */
    @Override
    public Coord getInitialLocation() {
        this.lastWaypoint = this.location;
//        tujuan.add(this.startLoc);
        return this.location;

    }

    @Override
    public Path getPath() {

        //jika list tujuan masih kosong
        if (tujuan.isEmpty()) {
            //menjalankan method ambil tujuan
            ambilSemuaTujuan();
        }

        Path p;
        p = new Path(generateSpeed());
        Coord c = lastWaypoint;
        double d = 0;

        /*
                selama list tujuan masih ada isinya maka dia akan dijalankan
                untuk mengambil koordinat ke dalam waypoint
         */
        while (tujuan.size() != 0) {

            for (Coord zi : tujuan) {
                d = c.distance(zi);

                jaraknya.put(zi, d);
            }
//            // create an array of type boolean to check if a node has been visited or not  
            boolean[] visitCity = new boolean[tujuan.size()];
//
//            // by default, we make the first city visited  
            visitCity[0] = true;

            double hamiltonianCycle = Double.MAX_VALUE;

            Coord min = null;
            double bebas = Double.MAX_VALUE;
            hamiltonianCycle = findHamiltonianCycle(d, visitCity, 0, tujuan.size(), 1, 0, hamiltonianCycle);

            for (Map.Entry<Coord, Double> entry : jaraknya.entrySet()) {
                Coord b = entry.getKey();
                Double value = entry.getValue();

                if (min == null) {
                    min = entry.getKey();
                    d = entry.getValue();
                } else {
                    if (entry.getValue() < jaraknya.get(hamiltonianCycle)) {

                        min = entry.getKey();

                    }
//                    min = hamiltonianCycle;
//                    d = entry.getValue();
                }

                jaraknya.put(min, bebas);
                System.out.println("hamiltonCycle: " + hamiltonianCycle);
            }
            //masukkan koordinat ke path
            p.addWaypoint(min);

            //menghapus koordinat tujuan dari list, ketika sudah ditambahkan ke path
            tujuan.remove(min);

        }
        this.lastWaypoint = location;
//        p.addWaypoint(this.startLoc);
        return p;
    }

    // create findHamiltonianCycle() method to get minimum weighted cycle   
    public double findHamiltonianCycle(double distance, boolean[] visitCity, int currPos, int cities, int count, int cost, double hamiltonianCycle) {
      double d = 0;
      
        for (Coord zi : tujuan) {
            d = location.distance(zi);

            jaraknya.put(zi, d);
        }
        if (count == tujuan.size() && d > 0) {
            hamiltonianCycle = Math.min(hamiltonianCycle, cost + d);
//            hamiltonianCycle = Math.min(hamiltonianCycle, cost + distance);
            return hamiltonianCycle;
        }

        // BACKTRACKING STEP  
        for (int i = 0; i < tujuan.size(); i++) {
            if (visitCity[i] == false && d > 0) {

                // Mark as visited  
                visitCity[i] = true;
                hamiltonianCycle = findHamiltonianCycle(d, visitCity, i, tujuan.size(), count + 1, cost, hamiltonianCycle);

                // Mark ith node as unvisited  
                visitCity[i] = false;
            }
        }
        return hamiltonianCycle;
    }

    @Override
    public RandomWaypointAldy2 replicate() {
        return new RandomWaypointAldy2(this);
    }

    protected Coord randomCoord() {
        return new Coord(rng.nextDouble() * getMaxX(),
                rng.nextDouble() * getMaxY());
    }

    //mengambil semua lokasi node dari dalam skenario dan menyimpan ke list tujuan
    public void ambilSemuaTujuan() {
        //membaca semua node di dalam skenario, disimpan ke dalam list semuaNodes
        List<DTNHost> semuaNodes = SimScenario.getInstance().getHosts();

        //membaca isi semuaNodes satu per satu
        for (DTNHost host : semuaNodes) {
            //mencari node yang bernama kota
            if (host.toString().startsWith("kota")) {
                //mengambil lokasi node kota tersebut ke dalam list
                tujuan.add(host.getLocation());

            }
        }
        //memasukkan lokasi asal ke list tujuan
        // this.location = this.startLoc;
        tujuan.add(this.startLoc);
    }

    //mencari index random dari dalam list
    public int randomTujuan(int max) {
        return (int) ((max - 0) * rng.nextDouble() + 0);
    }
}

//            for (Map.Entry<Coord, Double> entry : jaraknya.entrySet()) {
//                Coord b = entry.getKey();
//                Double value = entry.getValue();
//
//                System.out.println("b : " + b + " value : " + value);
////                if ( value < bebas) {
////                    min = entry.getKey();
////                    bebas = entry.getValue();
////                    
////                }
//                if (min == null) {
//                    min = entry.getKey();
//                } else {
//                    if (entry.getValue() < jaraknya.get(min)) {
////                        System.out.println("entry = " + entry.getValue());
////                        System.out.println("min = " + jaraknya.get(min));
//
//                        min = entry.getKey();
//                    }
////                    System.out.println("entry 2 = " + entry.getValue());
////                    System.out.println("min 2 = " + jaraknya.get(min));
//                }
//
//            }
//            System.out.println("coord : " + min + " jarak :" + jaraknya.get(min));
//        int dest = 0;
//        for (int i = 0; i < tujuan.size(); i++) {
//            Coord starting = getCoord(i);
//            if (i + 1 < tujuan.size()) {
//                other = getCoord(i + 1);
//            } else {
//                other = getCoord(0);
//            }
//            dest += starting.distanceTSP(other);

