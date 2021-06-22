/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movement;

import core.Coord;
import core.DTNHost;
import core.Settings;
import core.SimScenario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author WINDOWS_X
 */
public class cobaCoba extends MovementModel {

    private Coord lastWaypoint;
    int n;
    private Coord startLoc;
    private Coord location;
    private double[][] adjacencyMatrix;
    private List<Coord> tujuan = new ArrayList<Coord>();

    private Map<Coord, Double> jaraknya = new HashMap<Coord, Double>();

    public cobaCoba(Settings settings) {
        super(settings);

        int[] coords = settings.getCsvInts("nodeLocation", 2);
        this.location = new Coord(coords[0], coords[1]);
        this.startLoc = new Coord(coords[0], coords[1]);

    }

    protected cobaCoba(cobaCoba rwp) {
        super(rwp);
        this.location = rwp.location;
        this.startLoc = rwp.startLoc;
    }

    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    @Override
    public Coord getInitialLocation() {
        this.lastWaypoint = this.location;
        tujuan.add(this.startLoc);
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

        adjacencyMatrix = new double[tujuan.size()][tujuan.size()];

        // generate adjacency matrix based on the vertices
        for (int i = 0; i < tujuan.size(); i++) {
            for (int j = 0; j < tujuan.size(); j++) {
                adjacencyMatrix[i][j] = c.distanceTSP(tujuan.get(i), tujuan.get(j));
            }
        }
        /*
                selama list tujuan masih ada isinya maka dia akan dijalankan
                untuk mengambil koordinat ke dalam waypoint
         */
        while (tujuan.size() != 0) {

            Map<Coord, Double> jaraknya = new HashMap<Coord, Double>();

//            for (Coord zi : tujuan) {
//                z = c.distance(zi);
//                jaraknya.put(zi, z);
////                System.out.println(zi + "jarak : " + jaraknya.get(zi));
//            }
//            Entry<Coord, Double> minimum = null;
            Coord min = null;
            double bebas = Double.MAX_VALUE;

            for (Map.Entry<Coord, Double> entry : jaraknya.entrySet()) {
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

    @Override
    public cobaCoba replicate() {
        return new cobaCoba(this);
    }
}
