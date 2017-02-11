package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.util.Utils;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Scanner;
import java.util.function.Supplier;

import static org.testng.Assert.*;

/**
 * Created by Jarrett on 02/05/17.
 */
public class SplixBoardTest {

    Submission<SplixPlayer> player1 = new Submission<>("Null Player 1", null);
    Submission<SplixPlayer> player2 = new Submission<>("Null Player 2", null);

    MutableMap<String, MutableList<String>> maps = Maps.mutable.empty();

    SplixPoint player1OwnedPoint = new SplixPoint();
    SplixPoint player2OwnedPoint = new SplixPoint();


    MutableSet<Point2D> pointsFromList(int[] positions) {
        MutableSet<Point2D> ret = Sets.mutable.empty();
        for (int i = 0; i < positions.length; i+=2) {
            ret.add(new Point2D(positions[i], positions[i+1]));
        }
        return ret;
    }



    @BeforeMethod
    public void setUp() throws Exception {
        player1OwnedPoint.setTypeOfOwner(player1);
        player2OwnedPoint.setTypeOfOwner(player2);
        loadInData("\\BoardStateTests.txt");
    }




    @Test
    public void testFloodSearch() throws Exception {

    }

    @Test
    public void testGetSubset() throws Exception {

    }

    @Test
    public void testKillPlayers() throws Exception {
//        SplixBoard board = getBoardWithDimsFromData(new Point2D(6, 12), "killOnePlayerTest");
//        MutableMap<Submission<SplixPlayer>, Point2D> playerPositions =
//                Maps.mutable.of(player1, new Point2D(2, 7), player2, new Point2D(2, 2));
    }

    @Test
    public void testCheckPlayerTrailsConnected() throws Exception {
    }

    @Test
    public void testFillPlayerCapturedArea() throws Exception {
        // test a passing fill
        SplixBoard board = getBoardWithDimsFromData(new Point2D(7, 15), "floodSearchPassing");
        MutableMap<Submission<SplixPlayer>, Point2D> playerPositions =
                Maps.mutable.of(player1, new Point2D(2, 7), player2, new Point2D(2, 2));
        board.initPlayers(playerPositions);

        board.fillPlayerCapturedArea(player1);
        assertEquals(board.countPointsOwnedByPlayer(player1), 69);

        // test a failing fill
        board = getBoardWithDimsFromData(new Point2D(7, 15), "floodSearchFailing");
        board.initPlayers(playerPositions);

        board.fillPlayerCapturedArea(player1);
        assertEquals(board.countPointsOwnedByPlayer(player1), 57);
    }

    @Test
    public void testGetDeathsFromMoves() throws Exception {
//        System.err.println(maps );
        // death by line cross
        SplixBoard board = getBoardWithDimsFromData(new Point2D(6, 13), "killPlayerTestOddSpacesAway");

        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.North));
        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.West));
        showBoard(board);
        assert(board.getDeathsFromMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.West)).
                equals(Maps.mutable.of(player2, player1)));

        // death by line cross just behind other's head
        board = getBoardWithDimsFromData(new Point2D(6, 13), "killPlayerTestEvenSpacesAway");

        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.North));
        assert(board.getDeathsFromMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.West)).
                equals(Maps.mutable.of(player1, player2)));
        
        
        // death by odd head butt (both end up on the same position)
        board = getBoardWithDimsFromData(new Point2D(6, 13), "killPlayerTestOddSpacesAway");

        board.applyMoves(Maps.mutable.of(player1, Direction.East, player2, Direction.West));
        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.North));
        assert(board.getDeathsFromMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.North)).
                equals(Maps.mutable.of(player2, player1, player1, player2)));
        
        // death by even head butt (players flip positions)
        board = getBoardWithDimsFromData(new Point2D(6, 13), "killPlayerTestEvenSpacesAway");

        board.applyMoves(Maps.mutable.of(player1, Direction.East, player2, Direction.North));
        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.North));
        assert(board.getDeathsFromMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.North)).
                equals(Maps.mutable.of(player2, player1, player1, player2)));
        
        // death by wall
        board = getBoardWithDimsFromData(new Point2D(6, 13), "killPlayerTestEvenSpacesAway");

        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.East));
        assert(board.getDeathsFromMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.East)).
                equals(Maps.mutable.of(player2, player2)));
        
    }


    @Test
    public void testInitPlayers() throws Exception {
        SplixBoard board = new SplixBoard(new SquareBounds(new Point2D(0, 0), new Point2D(10, 10)));
        board.initPlayers(Maps.mutable.of(player1, new Point2D(3, 2), player2, new Point2D(7, 7)));
//        showBoard(board);
        assertEquals(board.countPointsOwnedByPlayer(player1), 25);
    }



    private void loadInData(String path) {
        Scanner scanner = new Scanner(getClass().getResourceAsStream(path));
        MutableList<String> buffer = Lists.mutable.empty();
        String currentName = null;

        while (scanner.hasNext()) {
            String str = scanner.nextLine();
            if (str.startsWith("#")) {// map name
                if (buffer.notEmpty()) {
                    maps.put(currentName, buffer.clone());
                    buffer.clear();
                }
                currentName = str.replace("#", "");
            } else
                buffer.add(str);
        }
    }

    private void initBoardFromMap(MutableList<String> map, SplixBoard base) {
        MutableMap<Character, Supplier<SplixPoint>> symbolMapping = Maps.mutable.empty();
        symbolMapping.put('x', () -> new SplixPoint(player1, null));
        symbolMapping.put('+', () -> new SplixPoint(player2, null));
        symbolMapping.put(' ', () -> new SplixPoint(null, null));
        symbolMapping.put('1', () -> new SplixPoint(player1, player2));
        symbolMapping.put('2', () -> new SplixPoint(player2, player1));
        symbolMapping.put('3', () -> new SplixPoint(null, player1));
        symbolMapping.put('4', () -> new SplixPoint(null, player2));
        // X is player 1's position
        // - is player 2's position
        for (int y = 0; y < map.size(); y++) {
            String line = map.get(y);

            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Point2D pos = new Point2D(x, y);

                try {
                    if (c == 'X') {
                        base.putPlayerInPosition(player1, pos);
                        base.put(pos, symbolMapping.get('x').get());
                    } else if (c == '-') {
                        base.putPlayerInPosition(player2, pos);
                        base.put(pos, symbolMapping.get('+').get());
                    } else
                        base.put(pos, symbolMapping.get(c).get());
                } catch (Exception e) {
                    System.err.println("" + y + " " + x);
                }
            }
        }
    }

    private void showBoard(SplixBoard board) {

        MutableMap<SplixPoint, Character> symbolMapping = Maps.mutable.empty();
        symbolMapping.put(new SplixPoint(player1, null), 'x');
        symbolMapping.put(new SplixPoint(player2, null), '+');
        symbolMapping.put(new SplixPoint(null, null), ' ');
        symbolMapping.put(new SplixPoint(player1, player2), '1');
        symbolMapping.put(new SplixPoint(player2, player1), '2');
        symbolMapping.put(new SplixPoint(null, player1), '3');
        symbolMapping.put(new SplixPoint(null, player2), '4');

        StringBuilder sb = new StringBuilder();
        sb.append("---\n");
        for (int y = 0; y < board.getBounds().getTop(); y++) {
            for (int x = 0; x < board.getBounds().getRight(); x++) {
                sb.append(symbolMapping.get(board.get(new Point2D(x, y))));
            }
            sb.append("|\n");
        }
        sb.append("---");
        System.err.println(sb);
    }


    private SplixBoard getBoardWithDimsFromData(Point2D dims, String whichData) {
        SplixBoard board = new SplixBoard(new SquareBounds(
                new Point2D(0, 0), dims));

        initBoardFromMap(maps.get(whichData), board);
        return board;
    }


}
