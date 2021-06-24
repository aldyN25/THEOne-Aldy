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
import java.text.DecimalFormat;
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
    private Coord m;
    private Coord startLoc;
    private Coord location;
    private double[][] adjacencyMatrix;
    private List<Coord> tujuan = new ArrayList<Coord>();
    static ArrayList<Integer> currentPath = new ArrayList<Integer>();
    static ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();

    static ArrayList<Double> pathDistances = new ArrayList<Double>();
    static double distance;
    private Map<Coord, Double> jaraknya = new HashMap<Coord, Double>();
    static DecimalFormat df = new DecimalFormat("0.00");

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
//        tujuan.add(this.startLoc);
        return this.location;

    }

    @Override
    public Path getPath() {

        //jika list tujuan masih kosong
        if (tujuan.isEmpty()) {
            System.out.println("test");
            //menjalankan method ambil tujuan
            ambilSemuaTujuan();
            System.out.println("tujuanBaru : " + tujuan.size());
        }

        Path p;
        p = new Path(generateSpeed());
        Coord c = lastWaypoint;
        int a = tujuan.size();
        adjacencyMatrix = new double[a][a];

        // generate adjacency matrix based on the vertices
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < a; j++) {
                adjacencyMatrix[i][j] = m.distanceTSP(tujuan.get(i), tujuan.get(j));
            }
        }
        
        for (int i = 0; i < tujuan.size(); i++) {
            currentPath.add(i);
            System.out.println("tujuan3: " + tujuan.size());
            System.out.println("curen :" + currentPath);
        }
//        System.out.println("current : " + currentPath);
        ArrayList<Integer> newPath = new ArrayList<Integer>(currentPath);
        paths.add(newPath);
        System.out.println("paths :" + paths);

        /*
                selama list tujuan masih ada isinya maka dia akan dijalankan
                untuk mengambil koordinat ke dalam waypoint
         */
        Map<Coord, Double> jaraknya = new HashMap<Coord, Double>();
        for (int i = 0; i < paths.size(); i++) {

            System.out.println("isi pathDist : " + paths.size());
            pathDistances = generatePathDistances(paths, getAdjacencyMatrix());
        }

        while (tujuan.size() != 0) {

            Coord min = null;
            double bebas = Double.MAX_VALUE;

            for (Map.Entry<Coord, Double> entry : jaraknya.entrySet()) {
                Coord b = entry.getKey();
                Double value = entry.getValue();

                System.out.println("b : " + b + " value : " + value);
                if (min == null) {
                    min = entry.getKey();
                } else {
                    if (entry.getValue() < jaraknya.get(min)) {

                        min = entry.getKey();
                    }
                }

            }
//            System.out.println("coord : " + min + " jarak :" + jaraknya.get(min));

            p.addWaypoint(min);
            tujuan.remove(min);
        }

        this.lastWaypoint = location;
        p.addWaypoint(this.startLoc);
        return p;
    }

    public ArrayList<Double> generatePathDistances(ArrayList<ArrayList<Integer>> paths, double[][] adjacencyMatrix) {
        ArrayList<Double> pathDistances = new ArrayList<Double>();
        double currentDistance = 0.0;
        String roundedDistance = "";

        for (int i = 0; i < paths.size(); i++) {

            for (int j = 0; j < paths.get(i).size() - 1; j++) {
                int first = paths.get(i).get(j);
                int second = paths.get(i).get(j + 1);
                currentDistance += adjacencyMatrix[first][second];
            }
            roundedDistance = String.format("%.4g%n", currentDistance);
            currentDistance = Double.parseDouble(roundedDistance);
            pathDistances.add(currentDistance);
            currentDistance = 0.0;
        }
        return pathDistances;
    }

    public void ambilSemuaTujuan() {
        //membaca semua node di dalam skenario, disimpan ke dalam list semuaNodes
        List<DTNHost> semuaNodes = SimScenario.getInstance().getHosts();

        //membaca isi semuaNodes satu per satu
        for (DTNHost host : semuaNodes) {
            //mencari node yang bernama kota
            System.out.println("semmua node: " + semuaNodes.size());
            if (host.toString().startsWith("kota")) {
                //mengambil lokasi node kota tersebut ke dalam list
                tujuan.add(host.getLocation());

            }
        }
        tujuan.add(this.startLoc);

    }

    @Override
    public cobaCoba replicate() {
        return new cobaCoba(this);
    }
}
