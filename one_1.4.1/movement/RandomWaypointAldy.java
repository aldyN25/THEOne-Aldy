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
import java.util.List;
import java.util.Random;
import static movement.MovementModel.rng;

/**
 * Random waypoint movement model. Creates zig-zag paths within the simulation
 * area.
 */
public class RandomWaypointAldy extends MovementModel {

    /**
     * how many waypoints should there be per path
     */
    private static final int PATH_LENGTH = 1;
    private Coord lastWaypoint;
    private Coord startLoc;
//        private Random rand;

    private Coord location;

    private List<Coord> tujuan = new ArrayList<Coord>();
//        private List<Coord> sudah = new ArrayList<Coord>();

    public RandomWaypointAldy(Settings settings) {
        super(settings);

        int[] coords = settings.getCsvInts("nodeLocation", 2);
        this.location = new Coord(coords[0], coords[1]);
        this.startLoc = new Coord(coords[0], coords[1]);
    }

    protected RandomWaypointAldy(RandomWaypointAldy rwp) {
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
//        tujuan.get(0).distance(c);
        /*
                selama list tujuan masih ada isinya maka dia akan dijalankan
                untuk mengambil koordinat ke dalam waypoint
         */
        while (tujuan.size() != 0) {
            //index, untuk mengambil koordinat dari list tujuan, didapat secara random
            int i = randomTujuan(tujuan.size() - 1);

            //mengambil koordinat random dari tujuan disimpan ke c
            c = tujuan.get(i);
            System.out.println(c);
            //masukkan koordinat ke path
            p.addWaypoint(c);

            //menghapus koordinat tujuan dari list, ketika sudah ditambahkan ke path
            tujuan.remove(i);
        }

        this.lastWaypoint = location;
        return p;
    }

    @Override
    public RandomWaypointAldy replicate() {
        return new RandomWaypointAldy(this);
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
        tujuan.add(this.startLoc);
    }

    //mencari index random dari dalam list
    public int randomTujuan(int max) {
        return (int) ((max - 0) * rng.nextDouble() + 0);
    }
}
