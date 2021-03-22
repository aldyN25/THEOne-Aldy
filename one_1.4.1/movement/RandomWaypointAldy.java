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
import java.util.Iterator;
import java.util.*;
import static movement.StationaryMovement.LOCATION_S;

/**
 * Random waypoint movement model. Creates zig-zag paths within the simulation
 * area.
 */
public class RandomWaypointAldy extends MovementModel {

    DTNHost dest;
    /**
     * how many waypoints should there be per path
     */
    private static final int PATH_LENGTH = 4;
    private Coord lastWaypoint;
    private Coord nextDestination;
    private Coord moveVector;
    private Path p;
    public List<DTNHost> destination;
    private Coord loc;

    public RandomWaypointAldy(Settings settings) {
        super(settings);
        //   destination = new ArrayList<DTNHost>();
        int coords[];

        coords = settings.getCsvInts(LOCATION_S, 2);
        this.loc = new Coord(coords[0], coords[1]);
//                this.destination = GetNextPath();
    }

    protected RandomWaypointAldy(RandomWaypointAldy rwp) {

        super(rwp);
        this.loc = rwp.loc;

    }

    /**
     * Returns a possible (random) placement for a host
     *
     * @return Random position on the map
     */
    @Override
    public Coord getInitialLocation() {
        this.lastWaypoint = loc;
        return loc;
    }

    public List<DTNHost> GetNextPath() {
        List<DTNHost> dest = new ArrayList<DTNHost>();
        List<DTNHost> all = SimScenario.getInstance().getHosts();
        Iterator<DTNHost> iter = all.iterator();
        while (iter.hasNext()) {
            DTNHost city = iter.next();
            if (city.toString().startsWith("kota")) {
                dest.add(city);
//                System.out.println(city);

            }
        }

        return dest;

    }

    @Override
    public Path getPath() {

        p = new Path(generateSpeed());
        p.addWaypoint(lastWaypoint.clone());
        Coord c = lastWaypoint;

        System.out.println(GetNextPath().size());
        Random j = new Random();
        for (int i = 1; i < PATH_LENGTH; i++) {
            int rand = j.nextInt(4);
            System.out.println(rand);
            dest = GetNextPath().get(rand);
            c = dest.getLocation();

        }
        GetNextPath().remove(dest);
        p.addWaypoint(c);

        this.lastWaypoint = c;
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

}