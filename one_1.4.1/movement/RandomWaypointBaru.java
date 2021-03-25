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
import java.util.Arrays;
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
    List<Coord> sudahDilewati = new ArrayList<>();
    List<Coord> tujuan = new ArrayList<>();
    Coord dest;
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
//        assert rng != null : "MovementModel not initialized!";
//        Coord b = randomCoord();
//
//        this.lastWaypoint = b;
//        return b;

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

                int randomIndex = rand.nextInt(tujuan.size());
                dest = this.tujuan.get(randomIndex);
                for (Coord loc : this.tujuan) {
                    c = dest;
//                    c = loc;
                    p.addWaypoint(loc);
                    this.sudahDilewati.add(loc);

                }
//                System.out.println(tujuan.remove(c));
            }
            //  tujuan.remove(dest);
            System.out.println(tujuan.remove(c));
            this.lastWaypoint = c;
        } else {
            p.addWaypoint(getInitialLocation());
        }

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
//        System.out.println(semuaNodes);
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
