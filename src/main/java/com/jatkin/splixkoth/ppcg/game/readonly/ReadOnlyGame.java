package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.players.Submission;

import java.util.Map;

/**
 * Created by Jarrett on 02/02/17.
 */
public class ReadOnlyGame {
    private SplixGame backing;

    public ReadOnlyGame(SplixGame backing) {
        this.backing = backing;
    }
    
    public int getRemainingIterations() {
        return backing.getRemainingIterations();
    }
    
}
