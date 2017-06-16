package com.jatkin.splixkoth.ppcg.game

import com.jatkin.splixkoth.ppcg.bots.java.TrapBot
import com.nmerrill.kothcomm.game.maps.Point2D
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareRegion
import org.eclipse.collections.api.map.MutableMap
import org.eclipse.collections.impl.factory.Maps
import org.eclipse.collections.impl.factory.Sets
import spock.lang.*

class SplixBoardTestSpock extends Specification {
    
    @Shared def player1 = new TrapBot()// does nothing, just used for reference
    @Shared def player2 = new TrapBot()
    def playersEvenSpacesAwayData = 
             """++++++
               |++++++
               |++++++
               |++++++
               |++++++
               |    - 
               |      
               |   #  
               |xxxxxx
               |xxxxxx
               |xxxxxx
               |xxxxxx
               |xxxxxx""".stripMargin()
    
    def playersOddSpacesAwayData = 
            """++++++
              |++++++
              |++++++
              |++++++
              |++++++
              |    - 
              |      
              |  #   
              |xxxxxx
              |xxxxxx
              |xxxxxx
              |xxxxxx
              |xxxxxx""".stripMargin()
    
    def failingFloodSearchData = 
            """xxxxxxx
              |   x  x
              |   x- 3
              |   x  #
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |+++++++
              |+++++++
              |+++++++
              |+++++++
              |+++++++""".stripMargin()

    def trailConnectedData =
            """333333333
              |3       3
              |3       #
              |xxxxxxxxx
              |+-       """.stripMargin()
    
    def passingFloodSearchData = 
            """xxxxxxx
              |   x  x
              |   x  3
              |   x  #
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |+++++++
              |+++++++
              |+++++++
              |+++++++
              |+++++++""".stripMargin()
    
    def thinLineFloodSearchData = 
            """xxxxxxx
              |     xx
              |     x3
              |     x#
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |+++++++
              |+++++++
              |+++++++
              |+++++++
              |+++++++""".stripMargin()
    
    def allSpaceSurroundedFloodSearchData = 
            """xxxxxxx
              |x     x
              |x     3
              |x     #
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |xxxxxxx
              |+++++++
              |+++++++
              |+++++++
              |+++++++
              |+++++++""".stripMargin()
    
    
    def "player should die when contacts wall"() {
        given: "a board where player is next to the wall"
        def board = getBoardWithDimsFromData(new Point2D(5, 12), playersEvenSpacesAwayData)
        board.applyMoves(Maps.mutable.of(player1, Direction.North, player2, Direction.East))
        
        expect: "the player to die when he hits the wall and that he killed himself"
        board.getDeathsFromMoves(
                Maps.mutable.of(player1, Direction.North, player2, Direction.East)) ==
                Maps.mutable.of(player2, player2)
    }
    
    
    def "players should die when they head butt"() {
        given: "a board where the players are an even or odd number of spaces away"
        def evenBoard = getBoardWithDimsFromData(new Point2D(5, 12), playersEvenSpacesAwayData)
        evenBoard.applyMoves(Maps.mutable.of(player1, Direction.East, player2, Direction.South))
        
        def oddBoard = getBoardWithDimsFromData(new Point2D(5, 12), playersOddSpacesAwayData)
        oddBoard.applyMoves(Maps.mutable.of(player1, Direction.North, player2, Direction.South))
        
        expect: "that both player should die both times"
        (evenBoard.getDeathsFromMoves(
                Maps.mutable.of(player1, Direction.North, player2, Direction.South)) == 
                Maps.mutable.of(player2, player1, player1, player2))
        (oddBoard.getDeathsFromMoves(
                Maps.mutable.of(player1, Direction.East, player2, Direction.West)) == 
                Maps.mutable.of(player2, player1, player1, player2))
    }
    
    def "players should die when their line is crossed"() {
        given: "a board where a player can cross another person's line"
        def farAwayLineCross = getBoardWithDimsFromData(new Point2D(5, 12), playersOddSpacesAwayData)
        farAwayLineCross.applyMoves(Maps.mutable.of(player1, Direction.North, player2, Direction.South))
        farAwayLineCross.applyMoves(Maps.mutable.of(player1, Direction.North, player2, Direction.West))

        // death by line cross just behind other's head
        def closeLineCross = getBoardWithDimsFromData(new Point2D(5, 12), playersEvenSpacesAwayData)
        closeLineCross.applyMoves(Maps.mutable.of(player1, Direction.North, player2, Direction.South))
        
        expect: "the player whos line was crossed to be killed by the other player"
        farAwayLineCross.getDeathsFromMoves(
                Maps.mutable.of(player1, Direction.North, player2, Direction.West)) ==
                Maps.mutable.of(player1, player2)
        closeLineCross.getDeathsFromMoves(
                Maps.mutable.of(player1, Direction.North, player2, Direction.West)) ==
                Maps.mutable.of(player1, player2)
    }

    
    
    /* ***************************************************************************************
       ****************************                                 **************************
       ****************************   Test fillPlayerCapturedArea   **************************
       ****************************                                 **************************
       *************************************************************************************** */
    
    

    def "players should fill in when they surround area that is not owned by another player"() {
        given: "a board where a player surrounds area"
        def board = getBoardWithDimsFromData(new Point2D(6, 14), passingFloodSearchData, false)
        board.applyMoves(Maps.mutable.of(player1, Direction.South))
        board.checkPlayerTrailsConnected()
        showBoard(board, player1, player2)
        
        expect: "the area to fill in"
        board.countPointsOwnedByPlayer(player1) == 61
    }
    
    def "players should not change area when the line is 2x thick"() {
        given: "a board where a player surrounds area"
        def board = getBoardWithDimsFromData(new Point2D(6, 14), thinLineFloodSearchData, false)
        board.applyMoves(Maps.mutable.of(player1, Direction.South))
        board.checkPlayerTrailsConnected()
        showBoard(board, player1, player2)
        
        expect: "the area to fill in"
        board.countPointsOwnedByPlayer(player1) == 55
    }
    
    def "players should fill in when the whole board is filled in"() {
        given: "a board where a player surrounds area"
        def board = getBoardWithDimsFromData(new Point2D(6, 14), allSpaceSurroundedFloodSearchData, false)
        board.applyMoves(Maps.mutable.of(player1, Direction.South))
        board.checkPlayerTrailsConnected()
        showBoard(board, player1, player2)
        
        expect: "the area to fill in"
        board.countPointsOwnedByPlayer(player1) == 70
    }
    
    def "players should not fill in when another player is inside"() {
        given: "a board where a player surrounds some area but another player is inside it"
        def board = getBoardWithDimsFromData(new Point2D(6, 14), failingFloodSearchData, false)
        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.South))
        board.checkPlayerTrailsConnected()
        showBoard(board, player1, player2)
        
        expect:
        board.countPointsOwnedByPlayer(player1) == 55
    }
    
    def "initPlayer should place a player at the given position and surround it with a 5x5 bit of land"() {
        given: 
        def board = new SplixBoard(new SquareRegion(new Point2D(0,0), new Point2D(7,7)))
        board.initPlayers(Maps.mutable.of(player1, new Point2D(3,3)))
        
        expect:
        board.countPointsOwnedByPlayer(player1) == 25
        board.getPlayerPositions().get(player1) == new Point2D(3, 3)
    }
    
    def "killPlayer erases all traces of a player"() {
        given: "a board with a player, locations owned by that player, and a trail of that player"
        def board = getBoardWithDimsFromData(new Point2D(6, 14), failingFloodSearchData)
        board.killPlayers(Sets.mutable.of(player1))
        def areaOwned = board.countPointsOwnedByPlayer(player1)
        def areaClaimed = board.locations().collect({l -> board.get(l)}).count {p -> p.getClaimer() == player1}
        def noPlayerPosition = board.getPlayerPositions().keySet().contains(player1) == false
        
        expect: "no trace left of that player"
        areaOwned == 0
        areaClaimed == 0
        noPlayerPosition
    }
    
    def "checkPlayerTrailConnected should convert a trail to normal line if it is connected and run a fill"() {
        given: "a board where the player has connected"
        def board = getBoardWithDimsFromData(new Point2D(8, 4), trailConnectedData, false)
        board.applyMoves(Maps.mutable.of(player1, Direction.South, player2, Direction.East))
        board.checkPlayerTrailsConnected()
        
        expect: "the board to be filled in and the trail to be converted"
        board.countPointsOwnedByPlayer(player1) == 36
    }
    
    /* ******************************************************************
    ****************************   Utils   ******************************
    ********************************************************************/
    
    
    private def initBoardFromString(String s, SplixBoard board, boolean setPlayerPositionOwned) {
        def symbolMapping = [('x' as char): {new SplixPoint(player1, null)}, 
                             ('+' as char): {new SplixPoint(player2, null)},
                             (' ' as char): {new SplixPoint(null, null)},
                             ('1' as char): {new SplixPoint(player1, player2)},
                             ('2' as char): {new SplixPoint(player2, player1)},
                             ('3' as char): {new SplixPoint(null, player1)},
                             ('4' as char): {new SplixPoint(null, player2)}]
        // X is player 1's position
        // - is player 2's position
        def map = s.split("\n")
        for (int y = 0; y < map.size(); y++) {
            String line = map[y]

            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x)
                Point2D pos = new Point2D(x, map.size()-1 - y)

                if (c == '#') {
                    board.putPlayerInPosition(player1, pos)
                    if (setPlayerPositionOwned)
                        board.put(pos, symbolMapping.get('x' as char)())
                } else if (c == '-') {
                    board.putPlayerInPosition(player2, pos)
                    if (setPlayerPositionOwned)
                        board.put(pos, symbolMapping.get('+' as char)())
                } else
                    board.put(pos, symbolMapping.get(c as char)())
            }
        }
        
    } 
    
    private def showBoard(SplixBoard board, SplixPlayer player1, SplixPlayer player2) {
        def symbolMapping = [(new SplixPoint(player1, null)): 'x', 
                             (new SplixPoint(player2, null)): '+',
                             (new SplixPoint(null, null)): ' ',
                             (new SplixPoint(player1, player2)): '1',
                             (new SplixPoint(player2, player1)): '2',
                             (new SplixPoint(null, player1)): '3',
                             (new SplixPoint(null, player2)): '4']
        // X is player 1's position
        // - is player 2's position
        MutableMap<SplixPlayer, Point2D> playerPositions = board.getPlayerPositions()

        def sb = new StringBuilder()
        sb.append("---\n")
        for (int y = board.getBounds().getTop(); y >= 0 ; y--) {
            for (int x = 0; x <= board.getBounds().getRight(); x++) {
                def point = new Point2D(x, y)
                def playerPosSame = playerPositions.findAll({SplixPlayer player, Point2D pos -> pos == point})
                if (!playerPosSame.isEmpty()) {
                    if (playerPosSame.keySet().contains(player1))
                        sb.append('#')
                    else
                        sb.append('-')
                } else
                    sb.append(symbolMapping.get(board.get(point)))

            }
            sb.append("|\n")
        }
        sb.append("---")
        System.err.println(sb)
    }

    private SplixBoard getBoardWithDimsFromData(Point2D dims, String whichData, boolean setPlayerPositionOwned = true) {
        def board = new SplixBoard(new SquareRegion(
                new Point2D(0, 0), dims))
        initBoardFromString(whichData, board, setPlayerPositionOwned)
        return board
    }
    
}