package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.NeighborhoodGraphMap;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareRegion;
import com.nmerrill.kothcomm.game.maps.graphmaps.neighborhoods.VonNeumannNeighborhood;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Basic explanation.
 * A splix map is described by a map of 2d points and a splix point.
 * Each 2d point shows where the splix point is located, and the splix
 * point shows what is happening at that point. A splix point `getClaimer`
 * shows that the line is part of the trail of that player. This is
 * only set when a player crosses area that is not owned by him. When
 * he has a trail and reattaches to himself, all points that have
 * `getClaimer` are converted to normal points and a flood
 * fill is triggered.
 *
 * Created by Jarrett on 02/01/17.
 */
public class SplixBoard extends NeighborhoodGraphMap<Point2D, SplixPoint> {
    private MutableMap<SplixPlayer, Point2D> playerPositions = Maps.mutable.empty();

    private final SquareRegion selfBounds;
    private final MutableSet<Point2D> borderPoints;

    public SplixBoard(SquareRegion bounds) {
        super(bounds, new VonNeumannNeighborhood());
        selfBounds = bounds;

        fillMapWithDefault();
        
        borderPoints =
            locations().select(p -> p.getX() == selfBounds.getLeft()
                                    || p.getX() == selfBounds.getRight()
                                    || p.getY() == selfBounds.getTop()
                                    || p.getY() == selfBounds.getBottom());
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
    
    public MutableMap<SplixPlayer, Point2D> getPlayerPositions() {
        return playerPositions.clone();
    }

    /**
     * Takes a list of players and positions and places each player on the position
     * given. Also fills in a 5x5 base around the player.
     * @param players
     * @param random
     */
    protected void initPlayers(MutableMap<SplixPlayer, Point2D> playerPositions_) {

        for (SplixPlayer player : playerPositions_.keySet()) {
            Point2D position = playerPositions_.get(player);
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    Point2D pointOfBase = Utils.addPoints(new Point2D(x, y), position);
                    if (!outOfBounds(pointOfBase))
                        get(pointOfBase).setOwner(player);
                }
            }

            playerPositions.put(player, position);
        }
    }

    /**
     * Do not use except for testing. Moves the player from a position to the location. Does
     * not take into account the trail it might leave behind.
     */
    protected void putPlayerInPosition(SplixPlayer player, Point2D pos) {playerPositions.put(player, pos);}

    /**
     * Get a view of a board. Primarily used for giving a player a view around him.
     * Does not perform bound checking - will throw error if the given bounds are 
     * outside the are of the board.
     * @param bounds
     * @return
     */
    public MutableMap<Point2D, SplixPoint> getSubset(SquareRegion bounds) {
        MutableMap<Point2D, SplixPoint> ret = Maps.mutable.empty();
        for (int x = Math.max(bounds.getLeft(), selfBounds.getLeft()); x <= bounds.getRight() && x <= selfBounds.getRight(); x++) {
            for (int y = Math.max(bounds.getBottom(), selfBounds.getBottom()); y <= bounds.getTop() && y <= selfBounds.getTop(); y++) {
                Point2D point = new Point2D(x, y);
                ret.put(point, get(point));
            }
        }
        return ret;
    }

    /**
     * Remove all remnants of a player from the board. 
     * @param players
     */
    public void killPlayers(Set<SplixPlayer> players) {
        for (SplixPlayer player : players) {
            for (Point2D point : locations().select(x -> get(x).getOwner() == player))
                get(point).setOwner(null);
            for (Point2D point : locations().select(x -> get(x).getClaimer() == player))
                get(point).setClaimer(null);

            playerPositions.remove(player);
        }
//        playerPositions.keySet().forEach(this::fillPlayerCapturedArea);
    }

    /**
     * Looks at all players and identifies if any of them have a finished trail.
     * If they do the trail is converted to normal line. Triggers a fill for any
     * player who connected.
     */
//    public void checkPlayerTrailsConnected() {
//        for (SplixPlayer player : playerPositions.keySet()) {
//            Point2D pos = playerPositions.get(player);
//            if (get(pos).getOwner() == player) {// player has entered his own territory
//                Set<Point2D> trail = floodSearch(pos, p -> get(p).getClaimer() == player); // speed up conversion.  
//                trail.forEach(t -> get(t).setClaimer(null));
//                trail.forEach(t -> get(t).setOwner(player));
//                if (!trail.isEmpty())
//                    fillPlayerCapturedArea(player);
//            }
//        }
//    }
    
    /**
     * Looks at all players and identifies if any of them have a finished trail.
     * If they do the trail is converted to normal line. Triggers a fill for any
     * player who connected.
     */
    public void checkPlayerTrailsConnected() {
        for (SplixPlayer player : playerPositions.keySet()) {
            Point2D pos = playerPositions.get(player);
            if (get(pos).getOwner() == player) {// player has entered his own territory
                Set<Point2D> trail = floodSearch(pos, p -> get(p).getClaimer() == player); // speed up conversion.  
                trail.forEach(t -> get(t).setClaimer(null));
                trail.forEach(t -> get(t).setOwner(player));
                if (!trail.isEmpty())
                    fillPlayerCapturedArea(player, trail);
            }
        }
    }

    /**
     * Looks at the land owned by a player and identifies empty spaces that
     * need to be filled. Incredibly inefficient, so use sparingly.
     *
     * @param whoToCheck
     */
//    public void fillPlayerCapturedArea(SplixPlayer whoToCheck) {
//        MutableSet<Point2D> allPoints = locations();
//        MutableSet<Point2D> alreadyOwnedSpace = allPoints.select(x -> get(x).getOwner() == whoToCheck);
//        MutableSet<Point2D> checkSpace = allPoints.difference(alreadyOwnedSpace);
//        MutableSet<Set<Point2D>> spacesToExamine = Sets.mutable.empty();
//        MutableSet<Point2D> otherPlayerPositionsSet = Sets.mutable.ofAll(playerPositions.values());
//        otherPlayerPositionsSet.remove(playerPositions.get(whoToCheck));
//
//        while (checkSpace.notEmpty()) {
//            Point2D start = checkSpace.iterator().next();
//            Set<Point2D> connectedPoints = floodSearch(start, point -> get(point).getOwner() != whoToCheck);
//            spacesToExamine.add(connectedPoints);
//            checkSpace.removeAll(connectedPoints);
//        }
//
//        for (Set<Point2D> space : spacesToExamine) {
//            // no points intersect boarder - no area that can be filled can intersect the boarder
//            boolean anyIntersectBoarder = space.removeAll(borderPoints);
//            if (!anyIntersectBoarder) {
//                // if the space intersects any other players, we can't fill in
//                // set contains true if we can't fill in
//                boolean canFillIn = !space.removeAll(otherPlayerPositionsSet);
//
//                if (canFillIn) {
//                    space.forEach(p -> get(p).setOwner(whoToCheck));
//                }
//            }
//        }
//    }
    
    
    /**
     * Looks at the land owned by a player and identifies empty spaces that
     * need to be filled. Incredibly inefficient, so use sparingly.
     *
     * @param whoToCheck
     */
    public void fillPlayerCapturedArea(SplixPlayer whoToCheck, Set<Point2D> areaChanged) {
        MutableSet<Point2D> previouslyVisitedLocations = Sets.mutable.empty();
        MutableList<Point2D> checkSpace = getAdjacent(areaChanged);

        for (Point2D pt : checkSpace) {
            Set<Point2D> points = floodSearchHelperFillPlayerCapturedArea(pt, whoToCheck, previouslyVisitedLocations);
            if (points != null) {// valid fill that didn't hit wall
                points.forEach(x -> get(x).setOwner(whoToCheck));
            }
        }
    }

    public int countPointsOwnedByPlayer(SplixPlayer player) {
        return locations().count(p -> get(p).getOwner() == player);
    }

    /**
     * Perform a flood fill from the start node, determines if the point is a border by `!isAccepted.test(point)`
     * @param start
     * @param isAccepted
     * @return
     */
    private Set<Point2D> floodSearch(Point2D start, Predicate<Point2D> isAccepted) {
        Set<Point2D> ret = new HashSet<>();
        ArrayDeque<Point2D> nodesToExamine = new ArrayDeque<>();
        nodesToExamine.add(start);
        while (!nodesToExamine.isEmpty()) {
            Point2D node = nodesToExamine.pop();
            ret.add(node);
            Point2D p1 = new Point2D(node.getX()-1, node.getY());
            Point2D p2 = new Point2D(node.getX()+1, node.getY());
            Point2D p3 = new Point2D(node.getX(), node.getY()+1);
            Point2D p4 = new Point2D(node.getX(), node.getY()-1);

            if (!ret.contains(p1) && inBounds(p1) && isAccepted.test(p1)) nodesToExamine.push(p1);
            if (!ret.contains(p2) && inBounds(p2) && isAccepted.test(p2)) nodesToExamine.push(p2);
            if (!ret.contains(p3) && inBounds(p3) && isAccepted.test(p3)) nodesToExamine.push(p3);
            if (!ret.contains(p4) && inBounds(p4) && isAccepted.test(p4)) nodesToExamine.push(p4);
        }
        return ret;
    }

    /**
     * Specific implementation of floodSearch tailored for fillPlayerCapturedArea. Mutates knownInvalidLocations
     * @param start
     * @param boundary
     * @return
     */
    private Set<Point2D>  floodSearchHelperFillPlayerCapturedArea(Point2D start, SplixPlayer boundary, MutableSet<Point2D> previouslyVisitedLocations) {
        Set<Point2D> ret = new HashSet<>();
        MutableSet<Point2D> invalidFillPositions = playerPositions.valuesView().toSet();
        invalidFillPositions.remove(playerPositions.get(boundary));
        ArrayDeque<Point2D> nodesToExamine = new ArrayDeque<>();
        nodesToExamine.add(start);
        while (!nodesToExamine.isEmpty()) {
            Point2D node = nodesToExamine.pop();
            ret.add(node);

            int[][] dirs = {new int[]{1, 0}, new int[]{-1, 0}, new int[]{0, 1}, new int[]{0, -1}};
            for (int[] dir : dirs) {
                Point2D ptToCheck = new Point2D(node.getX() + dir[0], node.getY() + dir[1]);
                boolean intersectsOldSpace = previouslyVisitedLocations.contains(ptToCheck);

                if (!intersectsOldSpace && inBounds(ptToCheck) && get(ptToCheck).getOwner() != boundary) {
                    if (borderPoints.contains(ptToCheck)
                             || invalidFillPositions.contains(ptToCheck)) {
                            // if we can reach the boarder or another knownInvalidLocation, we have nothing else to do.
                        return null;
                    }
                    previouslyVisitedLocations.addAll(ret);
                    nodesToExamine.push(ptToCheck);
                }
                
                if (intersectsOldSpace)
                    return null;
            }
        }
        return ret;
    }
    

    /**
     * Get a list of all points next to the given set, excluding the set itself.
     * @param points
     * @return
     */
    private MutableList<Point2D> getAdjacent(Set<Point2D> points) {
        MutableList<Point2D> ret = Lists.mutable.empty();
        for (Point2D point : points) {
            Point2D p1 = new Point2D(point.getX()-1, point.getY());
            Point2D p2 = new Point2D(point.getX()+1, point.getY());
            Point2D p3 = new Point2D(point.getX(), point.getY()+1);
            Point2D p4 = new Point2D(point.getX(), point.getY()-1);
            
            if (!ret.contains(p1) && inBounds(p1) && !points.contains(p1)) ret.add(p1);
            if (!ret.contains(p2) && inBounds(p2) && !points.contains(p2)) ret.add(p2);
            if (!ret.contains(p3) && inBounds(p3) && !points.contains(p3)) ret.add(p3);
            if (!ret.contains(p4) && inBounds(p4) && !points.contains(p4)) ret.add(p4);
        }
        
        return ret;
    }


    /**
     * Returns a map of players : who killed them.
     * @param playerMoves Possible moves.
     * @return
     */
    protected MutableMap<SplixPlayer, SplixPlayer> getDeathsFromMoves(MutableMap<SplixPlayer, Direction> playerMoves) {
        MutableMap<SplixPlayer, Point2D> newPlayerPositions = Maps.mutable.empty();
        MutableMap<SplixPlayer, SplixPlayer> deadPlayers =
                Maps.mutable.empty();

        MutableList<SplixPlayer> players = playerPositions.keysView().toList();
        players.forEach(player -> newPlayerPositions.put(player,
                        Utils.addPoints(playerPositions.get(player), playerMoves.get(player).vector)));
        
        players.forEach(p -> {
            if (outOfBounds(newPlayerPositions.get(p)))
                deadPlayers.put(p, p);
        });
        players = players.select(p -> !deadPlayers.keySet().contains(p));// remove wall hitters
        MutableSet<SplixPlayer> playersThatCanDie = players
                         .select(p -> get(newPlayerPositions.get(p)).getOwner() != p).toSet();

        for (int i = 0; i < players.size(); i++) {

            SplixPlayer currentPlayer = players.get(i);
            
            // check trail intersection
            SplixPlayer newPositionClaimer = get(newPlayerPositions.get(currentPlayer)).getClaimer();
            if (newPositionClaimer != null && playersThatCanDie.contains(newPositionClaimer)) {
                deadPlayers.put(newPositionClaimer, currentPlayer);
            }
            

            // all combos of players
            // used to check if any of their positions are close enough to kill
            for (int j = i+1; j < players.size(); j++) {
                // need to check for 3 types of death: death by swapping head butt, death
                // by hitting same spot, and death by being hit just behind the head. Death
                // by being hit just behind the head has an interesting edge case when the
                // new position is inside a player's area but his old position is occupied
                // by another player. This is rolled into `playersThatCanDie`.
                
                SplixPlayer otherPlayer = players.get(j);
                // point old, point new
                Point2D currPlayerO = playerPositions.get(currentPlayer);
                Point2D otherPlayerO = playerPositions.get(otherPlayer);
                Point2D currPlayerN = newPlayerPositions.get(currentPlayer);
                Point2D otherPlayerN = newPlayerPositions.get(otherPlayer);

                if (Utils.realMovementDist(currPlayerO, otherPlayerO) < 3) {// they actually have a chance at colliding
                    if (currPlayerN.equals(otherPlayerN)) {// normal head butt, both should die
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
    public void applyMoves(Map<SplixPlayer, Direction> playerMoves) {
        MutableMap<SplixPlayer, Point2D> newPlayerPositions = Maps.mutable.empty();

        MutableList<SplixPlayer> players = playerPositions.keysView().toList();
        players.forEach(player -> newPlayerPositions.put(player,
                Utils.addPoints(playerPositions.get(player), playerMoves.get(player).vector)));

        newPlayerPositions.forEach((player, nPos) -> {
            Point2D oPos = playerPositions.get(player);
            if (get(oPos).getOwner() != player)
                get(oPos).setClaimer(player);

            playerPositions.put(player, nPos);
        });
    }

    public SquareRegion getBounds() {return selfBounds;}

    public boolean putSafe(Point2D point, SplixPoint item) {
        try {
            super.put(point, item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean inBounds(Point2D point) {
        return selfBounds.inBounds(point);
    }

    @Override
    public boolean outOfBounds(Point2D point) {
        return selfBounds.outOfBounds(point);
    }
}
