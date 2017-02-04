package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.AdjacencyGraphMap;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.maps.graphmaps.neighborhoods.VonNeumannNeighborhood;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Random;
import java.util.function.Predicate;

/**
 * Basic explanation.
 * A splix map is described by a map of 2d points and a splix point.
 * Each 2d point shows where the splix point is located, and the splix
 * point shows what is happening at that point. A splix point `isTrail`
 * shows if it is in a partial fill state where the player can be
 * killed by crossing it. This is only set when a player crosses area
 * that is not owned by him. When he has a trail and reattaches to
 * himself, all points that have `isTrail` are converted to normal points
 * and a flood fill is triggered.
 *
 * Created by Jarrett on 02/01/17.
 */
public class SplixBoard extends AdjacencyGraphMap<Point2D, SplixPoint> {
    private MutableMap<Submission<SplixPlayer>, Point2D> playerPositions;

    private final SquareBounds selfBounds;

    public SplixBoard(SquareBounds bounds) {
        super(bounds, new VonNeumannNeighborhood());
        selfBounds = bounds;
    }

    /**
     * Fill a board to the bounds with a default SplixPoint
     */
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

    /**
     * Takes a list of players and puts them in random locations on the board
     * using the random it is given. Also fills in a 5x5 base around the player.
     * @param players
     * @param random
     */
    protected void initPlayers(MutableList<Submission<SplixPlayer>> players, Random random) {
        for (Submission<SplixPlayer> player : players) {
            Point2D position = new Point2D(random.nextInt(selfBounds.getRight()),
                                            random.nextInt(selfBounds.getBottom()));
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    Point2D pointOfBase = Utils.addPoints(new Point2D(x, y), position);
                    if (!outOfBounds(pointOfBase))
                        get(pointOfBase).setTypeOfOwner(player);
                }
            }

            playerPositions.put(player, position);
        }
    }


    /**
     * Get a view of a board. Primarily used for giving a player a view around him.
     * @param bounds
     * @return
     */
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

    /**
     * Remove all remnants of a player from the board
     * @param players
     */
    public void killPlayers(MutableSet<Submission<SplixPlayer>> players) {
        for (Submission<SplixPlayer> player : players) {
            for (Point2D point : getPointsOwnedBy(player)) {
                get(point).setTypeOfOwner(null);
                get(point).setTrail(false);
            }

            playerPositions.remove(player);
        }
    }

    /**
     * Looks at all players and identifies if any of them have a finished trail.
     */
    public void checkPlayerTrailsConnected() {
        for (Submission<SplixPlayer> player : playerPositions.keySet()) {
            Point2D pos = playerPositions.get(player);
            if (!get(pos).isTrail()) {// player has entered his own territory
                MutableSet<Point2D> trail = followLine(pos, p -> get(p).isTrail() && get(p).getTypeOfOwner() == player);
                for (Point2D trailPoint : trail) {
                    get(trailPoint).setTrail(false);
                }
            }
        }
    }

    /**
     * An attempt at efficiency. Allows a subset of the board to be searched
     * when looking for a trail. Includes the start point in the return.
     *
     * Unsure of which searching algorithm is used...
     */
    private MutableSet<Point2D> followLine(Point2D start, Predicate<Point2D> isPartOfLine) {
        MutableSet<Point2D> ret = Sets.mutable.empty();
        MutableList<Point2D> nodesToExamine = Lists.mutable.of(start);
        while (nodesToExamine.notEmpty()) {
            Point2D node = nodesToExamine.get(0);
            MutableSet<Point2D> neighbors = getNeighbors(node);
            ret.add(node);

            for (Point2D neighbor : neighbors) {
                if (isPartOfLine.test(neighbor))
                    nodesToExamine.add(neighbor);
            }
            nodesToExamine.remove(0);
        }
        return ret;
    }

    /**
     * Return all the points owned by a player
     * @param player
     * @return
     */
    public MutableSet<Point2D> getPointsOwnedBy(Submission<SplixPlayer> player) {
        MutableSet<Point2D> ret = Sets.mutable.empty();
        for (Point2D point : locations()) {
            if (get(point).getTypeOfOwner() == player)
                ret.add(point);
        }

        return ret;
    }

    /**
     * Returns a map of players : who killed them.
     * @param playerMoves Possible moves.
     * @return
     */
    protected MutableMap<Submission<SplixPlayer>, Submission<SplixPlayer>> getDeathsFromMoves(MutableMap<Submission<?>, Direction> playerMoves) {
        MutableMap<Submission<?>, Point2D> newPlayerPositions = Maps.mutable.empty();
        MutableMap<Submission<SplixPlayer>, Submission<SplixPlayer>> ret =
                Maps.mutable.empty();

        MutableList<Submission<SplixPlayer>> players = playerPositions.keysView().toList();
        players.forEach(player -> newPlayerPositions.put(player,
                        Utils.addPoints(playerPositions.get(player), playerMoves.get(player).vector)));

        for (int i = 0; i < players.size(); i++) {

            Submission<SplixPlayer> player1 = players.get(i);
            // check trail intersection
            if (get(playerPositions.get(player1)).isTrail()) {
                ret.put(get(playerPositions.get(player1)).getTypeOfOwner(), player1);
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
                            ret.put(player1, player2);
                        if (get(pn2).getTypeOfOwner() != player2)// in his land?
                            ret.put(player2, player1);
                    }

                    if (po1.equals(pn2) && po2.equals(pn1)) {// swapping head butt, or player hitting just behind other person's head
                        ret.put(player1, player2);
                        ret.put(player2, player1);
                    }
                }
            }
        }
        return ret;
    }

    public boolean putSafe(Point2D point, SplixPoint item) {
        try {
            super.put(point, item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
