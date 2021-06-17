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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    public boolean[] visitCity;
    public int currPos, cities, count, cost, hamiltonianCycle;
    public double d;
    private int distance[][];
    private Coord c = lastWaypoint;

    private List<Coord> tujuan = new ArrayList<Coord>();

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
        c = lastWaypoint;
        /*
                selama list tujuan masih ada isinya maka dia akan dijalankan
                untuk mengambil koordinat ke dalam waypoint
         */
        while (tujuan.size() != 0) {

            Map<Coord, Double> jaraknya = new HashMap<Coord, Double>();

            for (Coord zi : tujuan) {

                d = zi.distanceTSP(c);
                jaraknya.put(zi, d);
                System.out.println( "Distance From Node : " + zi + " To Node : " + zi +" jarak : " + d);

            }
            Coord min = null;
            double bebas = Double.MAX_VALUE;
            for (Entry<Coord, Double> entry : jaraknya.entrySet()) {

                Coord b = entry.getKey();
                Double value = entry.getValue();

                System.out.println("b : " + b + " value : " + value);
//                if ( value < bebas) {
//                    min = entry.getKey();
//                    bebas = entry.getValue();
//                    
//                }
                if (min == null) {
                    min = entry.getKey();
                } else {
                    if (entry.getValue() < jaraknya.get(min)) {
//                        System.out.println("entry = " + entry.getValue());
//                        System.out.println("min = " + jaraknya.get(min));

                        min = entry.getKey();
                    }
//                    System.out.println("entry 2 = " + entry.getValue());
//                    System.out.println("min 2 = " + jaraknya.get(min));
                }

            }
            System.out.println("coord : " + min + " jarak :" + jaraknya.get(min));

            p.addWaypoint(min);
            tujuan.remove(min);
            //masukkan koordinat ke path
            //menghapus koordinat tujuan dari list, ketika sudah ditambahkan ke path
        }
        this.lastWaypoint = location;
        p.addWaypoint(this.startLoc);
        return p;
    }
//
//    public int findHamiltonCycle() {
//        if (count == tujuan.size() && distance[currPos][0] > 0) {
//            hamiltonianCycle = (int) Math.min(hamiltonianCycle, cost + distance[currPos][0]);
//            return hamiltonianCycle;
//        }
//
//        for (int i = 0; i < tujuan.size(); i++) {
//            if (visitCity[i] == false && distance[currPos][i] > 0) {
//
//                // Mark as visited  
//                visitCity[i] = true;
//                hamiltonianCycle = findHamiltonCycle();
//
//                // Mark ith node as unvisited  
//                visitCity[i] = false;
//            }
//        }
//        return hamiltonianCycle;
//
//    }
//
//    public double distanceTSP() {
//        double dist = 0;
//        if (dist == 0) {
//            double tourDistance = 0;
//            for
//        }
//        return 0;
//        
//    }

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
