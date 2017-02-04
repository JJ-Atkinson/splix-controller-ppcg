package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.AdjacencyGraphMap;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.Bounds;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.maps.graphmaps.neighborhoods.VonNeumannNeighborhood;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Random;

/**
 * Created by Jarrett on 02/01/17.
 */
public class SplixBoard extends AdjacencyGraphMap<Point2D, SplixPoint> {
    private MutableMap<Submission<SplixPlayer>, Point2D> playerPositions;

    public SplixBoard(Bounds<Point2D> bounds) {
        super(bounds, new VonNeumannNeighborhood());
    }

    private void fillMapWithDefault() {
        for (int x = 0; true; x++) {
            for (int y = 0; true; y++) {
                Point2D point = new Point2D(x, y);
                if (outOfBounds(point))
                   break;

               put(point, new SplixPoint());
            }
            if (outOfBounds(new Point2D(x, 0)))
                break;
        }
    }

    protected void initPlayers(Submission<SplixPlayer> players, Random random) {

    }


    public MutableMap<Point2D, SplixPoint> getSubset(SquareBounds bounds) {
        MutableMap<Point2D, SplixPoint> ret = Maps.mutable.empty();
        for (int x = bounds.getLeft(); x <= bounds.getRight(); x++) {
            for (int y = bounds.getTop(); y <= bounds.getBottom(); y++) {
                Point2D point = new Point2D(x, y);
                ret.put(point, get(point));
            }
        }

        return ret;
    }

    protected MutableSet<Submission<SplixPlayer>> getDeathsFromMoves(MutableMap<Submission<?>, Direction> playerMoves) {
        MutableMap<Submission<?>, Point2D> newPlayerPositions = Maps.mutable.empty();
        MutableSet<Submission<SplixPlayer>> ret = Sets.mutable.empty();

        MutableList<Submission<SplixPlayer>> players = playerPositions.keysView().toList();
        players.forEach(player ->
                newPlayerPositions.put(player,
                        Utils.addPoints(playerPositions.get(player), playerMoves.get(player).vector)));

        for (int i = 0; i < players.size(); i++) {
            Submission<SplixPlayer> player1 = players.get(i);
            // check trail intersection
            if (get(playerPositions.get(player1)).isTrail()) {
                ret.add(get(playerPositions.get(player1)).getTypeOfOwner());
            }

            // all combos of players
            for (int j = i+1; j < players.size(); j++) {
                Submission<SplixPlayer> player2 = players.get(j);
                // point old, point new
                Point2D po1 = playerPositions.get(player1);
                Point2D po2 = playerPositions.get(player2);
                Point2D pn1 = newPlayerPositions.get(player1);
                Point2D pn2 = newPlayerPositions.get(player2);

                if (Utils.realMovementDist(po1, po2) < 3) {// they actually have a chance at colliding
                    if (po1.equals(po2)) {// head butt
                        if (get(pn1).getTypeOfOwner() != player1)// in his land?
                            ret.add(player1);
                        if (get(pn2).getTypeOfOwner() != player2)// in his land?
                            ret.add(player2);
                    }

                    if (po1.equals(pn2) && po2.equals(pn1)) {// swapping head butt, or player hitting just behind other person's head
                        ret.add(player1);
                        ret.add(player2);
                    }
                }
            }
        }
        return ret;
    }
}
