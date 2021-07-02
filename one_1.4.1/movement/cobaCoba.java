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
    private List<Integer> currentPath = new ArrayList<Integer>();
    private ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();

    private List<Double> pathDistances = new ArrayList<Double>();
    private double distance;
    private Map<Coord, List<Double>> jarakk = new HashMap<Coord, List<Double>>();
    private DecimalFormat df = new DecimalFormat("0.00");

    public cobaCoba(Settings settings) {
        super(settings);

        int[] coords = settings.getCsvInts("nodeLocation", 2);
        this.location = new Coord(coords[0], coords[1]);
        this.startLoc = new Coord(coords[0], coords[1]);

        int a = tujuan.size();
        adjacencyMatrix = new double[a][a];

        // generate adjacency matrix based on the vertices
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < a; j++) {
                adjacencyMatrix[i][j] = m.distanceTSP(tujuan.get(i), tujuan.get(j));
            }
        }

    }

    protected cobaCoba(cobaCoba rwp) {
        super(rwp);

        this.location = rwp.location;
        this.startLoc = rwp.startLoc;

        int a = tujuan.size();
        adjacencyMatrix = new double[a][a];

        // generate adjacency matrix based on the vertices
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < a; j++) {
                adjacencyMatrix[i][j] = m.distanceTSP(tujuan.get(i), tujuan.get(j));
            }
        }

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

        for (int i = 0; i < tujuan.size(); i++) {

            currentPath.add(i);
            System.out.println("tujuan3: " + tujuan.size());
            System.out.println("curen :" + currentPath);
        }
//        System.out.println("current : " + currentPath);
        ArrayList<Integer> newPath = new ArrayList<Integer>(currentPath);
        this.paths.add(newPath);
        System.out.println("paths :" + paths);

        // add a 0 at the beginning AND end of each path
//        for (int i = 0; i < paths.size(); i++) {
//            ArrayList<Integer> pathToModify = paths.get(i);
//            pathToModify.add(0, 0);
//            pathToModify.add(0);
//            this.paths.set(i, pathToModify);
//        }
        /*
                selama list tujuan masih ada isinya maka dia akan dijalankan
                untuk mengambil koordinat ke dalam waypoint
         */
        for (int i = 0; i < paths.size(); i++) {
            pathDistances = generatePathDistances(this.paths, getAdjacencyMatrix());
            System.out.println("isi pathDist : " + pathDistances);
        }

        while (tujuan.size() != 0) {

            Coord min = null;
            double bebas = Double.MAX_VALUE;

            for (Map.Entry<Coord, List<Double>> entry : jarakk.entrySet()) {
                Coord key = entry.getKey();
                List<Double> value = entry.getValue();

            }

//            System.out.println("coord : " + min + " jarak :" + jaraknya.get(min));
            p.addWaypoint(min);
            tujuan.remove(min);
        }

        this.lastWaypoint = location;
        p.addWaypoint(this.startLoc);
        return p;
    }

//    public List<Double> generatePathDistances(List<ArrayList<Integer>> paths, double[][] adjacencyMatrix) {
//        System.out.println("tesstt");
////        ArrayList<Double> pathDistances = new ArrayList<Double>();
//        double currentDistance = 0.0;
//        String roundedDistance = "";
//
//        for (int i = 0; i < this.paths.size(); i++) {
//            System.out.println("pathGenerate i : " + this.paths.size() );
//            for (int j = 0; j < this.paths.get(i).size() - 1; j++) {
//                System.out.println("pathGenerate j : " + (this.paths.get(i).size() - 1));
//                int first = this.paths.get(i).get(j);
//                System.out.println("isi First :" + paths.get(i).get(j));
//                int second = this.paths.get(i).get(j + 1);
//                System.out.println("isi kedua :" + paths.get(i).get(j + 1));
//                currentDistance += adjacencyMatrix[first][second];
//                System.out.println("CurrentDist : " + currentDistance);
//            }
//            roundedDistance = String.format("%.4g%n", currentDistance);
//            currentDistance = Double.parseDouble(roundedDistance);
//            pathDistances.add(currentDistance);
//            currentDistance = 0.0;
//        }
//        System.out.println("isi pathDist : " + pathDistances);
//        return pathDistances;
//    }
    public ArrayList<Double> generatePathDistances(ArrayList<ArrayList<Integer>> paths, double[][] adjacencyMatrix) {
        System.out.println("TEssrt");
        ArrayList<Double> pathDistances = new ArrayList<Double>();
        double currentDistance = 0.0;
        String roundedDistance = "";
        for (int i = 0; i < this.paths.size(); i++) {
            System.out.println(" panjang paths : " + this.paths.size());
            System.out.println("pathGenerate i : " + this.paths.get(i));
            for (int j = 0; j < this.paths.get(i).size() - 1; j++) {
                System.out.println("pathGenerate j : " + (this.paths.get(i).size() - 1));
                int first = this.paths.get(i).get(j);
                System.out.println("isi First :" + first);
                int second = this.paths.get(i).get(j);
                System.out.println("isi kedua :" + second);
                currentDistance += adjacencyMatrix[first][second];
                System.out.println("isi CurrDist : " + currentDistance);
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

    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }
}

//            for (Map.Entry<Coord, Double> entry : jaraknya.entrySet()) {
//                Coord b = entry.getKey();
//                Double value = entry.getValue();
//
//                System.out.println("b : " + b + " value : " + value);
//                if (min == null) {
//                    min = entry.getKey();
//                } else {
//                    if (entry.getValue() < jaraknya.get(min)) {
//
//                        min = entry.getKey();
//                    }
//                }
//
//            }
