package com.jatkin.splixkoth.ppcg.game;

import org.junit.Test;
import spock.lang.Specification;

import static org.junit.Assert.*;

/**
 * Created by Jarrett on 02/20/17.
 */
public class DirectionSpec extends Specification {
    
    def "a left turn should turn 90deg from a given direction"() {
        expect:
        Direction.East.leftTurn() == Direction.North
        Direction.North.leftTurn() == Direction.West
        Direction.West.leftTurn() == Direction.South
        Direction.South.leftTurn() == Direction.East
    }

    def "a right turn should turn 270deg from a given direction"() {
        expect:
        Direction.East.rightTurn() == Direction.South
        Direction.South.rightTurn() == Direction.West
        Direction.West.rightTurn() == Direction.North
        Direction.North.rightTurn() == Direction.East
    }

}