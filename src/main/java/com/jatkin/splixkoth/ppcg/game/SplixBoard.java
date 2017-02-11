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
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.Stacks;

import java.util.Random;
import java.util.function.Predicate;

/**
 * Basic explanation.
 * A splix map is described by a map of 2d points and a splix point.
 * Each 2d point shows where the splix point is located, and the splix
 * point shows what is happening at that point. A splix point `getTypeOfClaimer`
 * shows that the line is part of the trail of that player. This is
 * only set when a player crosses area that is not owned by him. When
 * he has a trail and reattaches to himself, all points that have
 * `getTypeOfClaimer` are converted to normal points and a flood
 * fill is triggered.
 *
 * Created by Jarrett on 02/01/17.
 */
public class SplixBoard extends AdjacencyGraphMap<Point2D, SplixPoint> {
    private MutableMap<Submission<SplixPlayer>, Point2D> playerPositions = Maps.mutable.empty();

    private final SquareBounds selfBounds;
    private final MutableSet<Point2D> borderPoints;

    public SplixBoard(SquareBounds bounds) {
        super(bounds, new VonNeumannNeighborhood());
        selfBounds = bounds;
        borderPoints =
            locations().select(p -> p.getX() == selfBounds.getLeft()
                                    || p.getX() == selfBounds.getRight()
                                    || p.getY() == selfBounds.getTop()
                                    || p.getY() == selfBounds.getBottom());

        fillMapWithDefault();
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
    
    protected MutableMap<Submission<SplixPlayer>, Point2D> getPlayerPositions() {
        return playerPositions.clone();
    }

    /**
     * Takes a list of players and positions and places each player on the position
     * given. Also fills in a 5x5 base around the player.
     * @param players
     * @param random
     */
    protected void initPlayers(MutableMap<Submission<SplixPlayer>, Point2D> playerPositions_) {

        for (Submission<SplixPlayer> player : playerPositions_.keySet()) {
            Point2D position = playerPositions_.get(player);
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
     * Do not use except for testing. Moves the player from a position to the location. Does
     * not take into account the trail it might leave behind.
     */
    protected void putPlayerInPosition(Submission<SplixPlayer> player, Point2D pos) {playerPositions.put(player, pos);}

    /**
     * Get a view of a board. Primarily used for giving a player a view around him.
     * @param bounds
     * @return
     */
    public MutableMap<Point2D, SplixPoint> getSubset(SquareBounds bounds) {
        MutableMap<Point2D, SplixPoint> ret = Maps.mutable.empty();
        for (int x = bounds.getLeft(); x <= bounds.getRight(); x++) {
            for (int y = bounds.getBottom(); y <= bounds.getTop(); y++) {
                Point2D point = new Point2D(x, y);
                ret.put(point, get(point));
            }
        }
        return ret;
    }

    /**
     * Remove all remnants of a player from the board. Triggers a flood fill
     * run for all players.
     * @param players
     */
    public void killPlayers(MutableSet<Submission<SplixPlayer>> players) {
        for (Submission<SplixPlayer> player : players) {
            // this makes me sad. so inefficient.
            for (Point2D point : locations().select(x -> get(x).getTypeOfOwner() == player))
                get(point).setTypeOfOwner(null);
            for (Point2D point : locations().select(x -> get(x).getTypeOfClaimer() == player))
                get(point).setTypeOfClaimer(null);

            playerPositions.remove(player);
        }
        playerPositions.keySet().forEach(this::fillPlayerCapturedArea);
    }

    /**
     * Looks at all players and identifies if any of them have a finished trail.
     * If they do the trail is converted to normal line. Triggers a fill for any
     * player who connected.
     */
    public void checkPlayerTrailsConnected() {
        for (Submission<SplixPlayer> player : playerPositions.keySet()) {
            Point2D pos = playerPositions.get(player);
            if (get(pos).getTypeOfOwner() == player) {// player has entered his own territory
                MutableSet<Point2D> trail = floodSearch(pos, p -> get(p).getTypeOfClaimer() == player);
                trail.forEach(t -> get(t).setTypeOfClaimer(null));
                trail.forEach(t -> get(t).setTypeOfOwner(player));
                if (trail.notEmpty())
                    fillPlayerCapturedArea(player);
            }
        }
    }

    /**
     * Looks at the land owned by a player and identifies empty spaces that
     * need to be filled. Incredibly inefficient, so use sparingly.
     *
     * @param whoToCheck
     */
    public void fillPlayerCapturedArea(Submission<SplixPlayer> whoToCheck) {
        MutableSet<Point2D> allPoints = locations();
        MutableSet<Point2D> alreadyOwnedSpace = allPoints.select(x -> get(x).getTypeOfOwner() == whoToCheck);
        MutableSet<Point2D> checkSpace = allPoints.difference(alreadyOwnedSpace);
        MutableSet<MutableSet<Point2D>> spacesToExamine = Sets.mutable.empty();

        while (checkSpace.notEmpty()) {
            Point2D start = checkSpace.iterator().next();
            MutableSet<Point2D> connectedPoints = floodSearch(start, point -> get(point).getTypeOfOwner() != whoToCheck);
            spacesToExamine.add(connectedPoints);
            checkSpace.removeAll(connectedPoints);
        }

        // efficiency concern, if only one check space exists then we can return
        if (spacesToExamine.size() == 1) return;

        for (MutableSet<Point2D> space : spacesToExamine) {
            // no points intersect boarder - no area that can be filled can intersect the boarder
            if (space.intersect(borderPoints).size() == 0) {
                // if the space intersects anything at all, we can't fill in
                // set contains true if we can't fill in
                boolean canFillIn = !space.collect(x -> {
                    SplixPoint p = get(x);
                    return p.getTypeOfOwner() != null || p.getTypeOfClaimer() != null ;
                }).contains(Boolean.TRUE);

                if (canFillIn) {
                    space.forEach(p -> get(p).setTypeOfOwner(whoToCheck));
                }
            }
        }
    }

    public int countPointsOwnedByPlayer(Submission<SplixPlayer> player) {
        return locations().count(p -> get(p).getTypeOfOwner() == player);
    }

    public MutableSet<Point2D> floodSearch(Point2D start, Predicate<Point2D> isAccepted) {
        MutableSet<Point2D> ret = Sets.mutable.empty();
        MutableStack<Point2D> nodesToExamine = Stacks.mutable.of(start);
        while (nodesToExamine.notEmpty()) {
            Point2D node = nodesToExamine.pop();
            ret.add(node);
            MutableSet<Point2D> validNeighbors = getNeighbors(node).select(n -> !ret.contains(n) && inBounds(n));
            validNeighbors.select(isAccepted::test).forEach(nodesToExamine::push);
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
        MutableMap<Submission<SplixPlayer>, Submission<SplixPlayer>> deadPlayers =
                Maps.mutable.empty();

        MutableList<Submission<SplixPlayer>> players = playerPositions.keysView().toList();
        players.forEach(player -> newPlayerPositions.put(player,
                        Utils.addPoints(playerPositions.get(player), playerMoves.get(player).vector)));
        
        MutableSet<Submission<SplixPlayer>> playersThatCanDie = players.select(p -> get(newPlayerPositions.get(p)).getTypeOfOwner() != p).toSet();

        for (int i = 0; i < players.size(); i++) {

            Submission<SplixPlayer> currentPlayer = players.get(i);
            
            // check trail intersection
            Submission<SplixPlayer> newPositionClaimer = get(playerPositions.get(currentPlayer)).getTypeOfClaimer();
            if (newPositionClaimer != null && playersThatCanDie.contains(newPositionClaimer)) {
                deadPlayers.put(newPositionClaimer, currentPlayer);
            }
            
            if (outOfBounds(newPlayerPositions.get(currentPlayer)))
                deadPlayers.put(currentPlayer, currentPlayer);

            // all combos of players
            // used to check if any of their positions are close enough to kill
            for (int j = i+1; j < players.size(); j++) {
                // need to check for 3 types of death: death by swapping head butt, death
                // by hitting same spot, and death by being hit just behind the head. Death
                // by being hit just behind the head has an interesting edge case when the
                // new position is inside a player's area but his old position is occupied
                // by another player. This is rolled into `playersThatCanDie`.
                
                Submission<SplixPlayer> otherPlayer = players.get(j);
                // point old, point new
                Point2D currPlayerO = playerPositions.get(currentPlayer);
                Point2D otherPlayerO = playerPositions.get(otherPlayer);
                Point2D currPlayerN = newPlayerPositions.get(currentPlayer);
                Point2D otherPlayerN = newPlayerPositions.get(otherPlayer);

                if (Utils.realMovementDist(currPlayerO, otherPlayerO) < 3) {// they actually have a chance at colliding
                    if (currPlayerO.equals(otherPlayerO)) {// normal head butt, both should die
                        if (playersThatCanDie.contains(currentPlayer))
                            deadPlayers.put(currentPlayer, otherPlayer);
                        if (playersThatCanDie.contains(otherPlayer))
                            deadPlayers.put(otherPlayer, currentPlayer);
                    }

                    // hit behind head, could be swapping head butt
                    if (otherPlayerN.equals(currPlayerO) && playersThatCanDie.contains(currentPlayer))
                        deadPlayers.put(currentPlayer, otherPlayer);
                    
                    if (currPlayerN.equals(otherPlayerO) && playersThatCanDie.contains(otherPlayer)) 
                        deadPlayers.put(otherPlayer, currentPlayer);
                }
            }
        }
        return deadPlayers;
    }

    /**
     * Apply the moves given. If the original position of a player was not in his area,
     * the position is updated to say that it is claimed by the player.
     * @param playerMoves
     */
    public void applyMoves(MutableMap<Submission<?>, Direction> playerMoves) {
        MutableMap<Submission<SplixPlayer>, Point2D> newPlayerPositions = Maps.mutable.empty();

        MutableList<Submission<SplixPlayer>> players = playerPositions.keysView().toList();
        players.forEach(player -> newPlayerPositions.put(player,
                Utils.addPoints(playerPositions.get(player), playerMoves.get(player).vector)));

        newPlayerPositions.forEach((player, nPos) -> {
            Point2D oPos = playerPositions.get(player);
            if (get(oPos).getTypeOfOwner() != player)
                get(oPos).setTypeOfClaimer(player);

            playerPositions.put(player, nPos);
        });
    }

    public SquareBounds getBounds() {return selfBounds;}

    public boolean putSafe(Point2D point, SplixPoint item) {
        try {
            super.put(point, item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
