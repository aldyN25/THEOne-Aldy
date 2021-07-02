/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import core.Coord;
import core.DTNHost;
import core.Settings;
import core.SimScenario;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    private double[][] jrk;
    private List<Coord> tujuan = new ArrayList<Coord>();
//    private Map<Coord, Double> jaraknya = new HashMap<Coord, Double>();
    private Map<Coord, List<Double>> jarakk = new HashMap<Coord, List<Double>>();
    private DecimalFormat df = new DecimalFormat("#.##");

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
        return this.location;

    }

    @Override
    public Path getPath() {

        //jika list tujuan masih kosong
        if (tujuan.isEmpty()) {
            //menjalankan method ambil tujuan
            ambilSemuaTujuan();
        }
        Matrix();
        Path p;
        p = new Path(generateSpeed());
        Coord c = lastWaypoint;


        /*
                selama list tujuan masih ada isinya maka dia akan dijalankan
                untuk mengambil koordinat ke dalam waypoint
         */
        while (tujuan.size() != 0) {

            List<Double> dl = new ArrayList<Double>();
            for (int i = 0; i < tujuan.size(); i++) {

                for (int j = 0; j < tujuan.size(); j++) {
                    dl.add(jrk[i][j]);

                }

                jarakk.put(tujuan.get(i), dl);
                System.out.println("isi DL: " + dl);
                System.out.println("isi Tujuan get i:" + tujuan.get(i));
//                System.out.println("jarak baru: " + jarakk.get(i));
//                jarakk.get(tujuan.get(i));
//                System.out.println("ISI JARAK mAP : " + jarakk.get(tujuan.get(i)));
            }

            for (int index = 0; index < jarakk.size(); index++) {
                List<Double> d = jarakk.get(index);

//                System.out.println("jarak baru: " + d);
            }

            List<Double> dll = new ArrayList<Double>();
            dll = jarakk.get(c);

            double bebas = Double.MAX_VALUE;
            System.out.println("jarak get C: " + dll);

            for (int i = 0; i < tujuan.size(); i++) {

                if (bebas > dll.get(i) && dll.get(i) != 0) {
                    bebas = dll.get(i);

                }
                System.out.println("isi Bebas : " + bebas + "Isi DLL :" + dll.get(i));

            }

//            Entry<Coord, List<Double>> minn = null;
////            System.out.println("isi Bebas : " + bebas);
//            for (int i = 0; i < 11; i++) {
//                int i = jrk[i][i].
//                if (jrk[][]) {
//                        
//                    }
//            }
            Coord min = null;

            for (Map.Entry<Coord, List<Double>> entry : jarakk.entrySet()) {
                Coord key = entry.getKey();
                List<Double> value = entry.getValue();

                for (int i = 0; i < value.size(); i++) {
                    if (bebas == value.get(i)) {
//                        System.out.println(" isi Value : " + value.get(i));
                        min = entry.getKey();
                    }
                }

            }
            System.out.println("isi Min : " + min);
            c = min;
            //masukkan koordinat ke path
            p.addWaypoint(min);

            //menghapus koordinat tujuan dari list, ketika sudah ditambahkan ke path
            tujuan.remove(min);
            jarakk.remove(min);

        }
        this.lastWaypoint = location;
        p.addWaypoint(this.startLoc);
        return p;
    }

    public double[][] Matrix() {
        int a = tujuan.size();
        jrk = new double[a][a];
        System.out.println("jrk :" + jrk.length);
        Coord corr = null;

        for (int i = 0; i < a; i++) {
//            System.out.println(" isi tujuan : " + a);
            corr = tujuan.get(i);

            for (int j = 0; j < a; j++) {
                if ((tujuan.get(i).getX() == tujuan.get(j).getX()) && (tujuan.get(i).getY() == tujuan.get(j).getY())) {
                    jrk[i][j] = 0;
                }
                jrk[i][j] = corr.distance(tujuan.get(j));
            }
        }
        for (int i = 0; i < tujuan.size(); i++) {
            corr = tujuan.get(i);

//            System.out.println(corr.getX() + " " + corr.getY());
            for (int j = 0; j < tujuan.size(); j++) {
                System.out.println(tujuan.get(j).getX() + " " + tujuan.get(j).getY());

//                System.out.println(i + " Menuju " + j + " " + jrk[i][j]);
            }
        }
        return jrk;
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
//            for (Map.Entry<Coord, List<Double>> entry : jarakk.entrySet()) {
//                Coord key = entry.getKey();
//                List<Double> value = entry.getValue();
////                double[][] value = entry.getValue();
//
//                if (min == null) {
//                    min = entry.getKey();
////                    ds = entry.getValue();
//
//                } else {
////                    if (value < jarakk.get(min)) {
//                    min = entry.getKey();
//                }
////                    min = hamiltonianCycle;
////                    d = entry.getValue();
////                }
//
////                jarakk.put(min, ds);
//            }
//                System.out.println("hamiltonCycle: " + hamiltonianCycle);
//            }
