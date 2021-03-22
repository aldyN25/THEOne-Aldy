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

/**
 * Random waypoint movement model. Creates zig-zag paths within the simulation
 * area.
 */
public class RandomWaypointBaru extends MovementModel {

    /**
     * how many waypoints should there be per path
     */
    private static final int PATH_LENGTH = 1;

    private Coord lastWaypoint;

    private List<Coord> sudahDilewati = new ArrayList<>();

    List<Coord> tujuan = new ArrayList<>();
    List<Coord> dest = new ArrayList<>();
    private Coord location;
    Random rand = new Random();

    public RandomWaypointBaru(Settings settings) {
        super(settings);
        int[] coords = settings.getCsvInts("nodeLocation", 2);
        this.location = new Coord(coords[0], coords[1]);
//        System.out.println(this.getInitialLocation());
    }

    protected RandomWaypointBaru(RandomWaypointBaru rwp) {
        super(rwp);
        this.location = rwp.location;
    }

    @Override
    public Coord getInitialLocation() {
        this.lastWaypoint = this.location;
        return this.location;

    }

    @Override
    public Path getPath() {
//        for (int i = 1; i < PATH_LENGTH; i++) {
//            int rand = j.nextInt(4);
//            System.out.println(rand);
//            dest = GetNextPath().get(rand);
//            c = dest.getLocation();
        ambilSemuaTujuan();
        Path p = new Path(generateSpeed());
        p.addWaypoint(this.lastWaypoint.clone());
        if (this.sudahDilewati.size() != this.tujuan.size()) {
            Coord c = this.lastWaypoint;
            for (int i = 0; i < this.tujuan.size(); i++) {
                 int random = (i) * rng.nextInt(1) + 0;
                 Coord dest = this.tujuan.get(random);
                 
                
//                for (Coord loc : this.dest) {
//                    c = loc;
//                    p.addWaypoint(c);
//                    this.sudahDilewati.add(loc);
//                }
            }
//            System.out.println(this.dest);
            this.lastWaypoint = c;
        } else {
            p.addWaypoint(getInitialLocation());
        }
        System.out.println(p);
        return p;
    }

    @Override
    public RandomWaypointBaru replicate() {
        return new RandomWaypointBaru(this);
    }

    protected Coord randomCoord() {
        return new Coord(rng.nextDouble() * getMaxX(), rng
                .nextDouble() * getMaxY());
    }

    public void ambilSemuaTujuan() {
        List<DTNHost> semuaNodes = SimScenario.getInstance().getHosts();
        List<DTNHost> tujuanNodes = new ArrayList<>();
        System.out.println(semuaNodes);
        for (DTNHost host : semuaNodes) {
            if (host.toString().startsWith("kota")) {
                tujuanNodes.add(host);
            }
        }
        for (DTNHost a : tujuanNodes) {
            this.tujuan.add(a.getLocation());
        }
    }
}
