package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.players.HunterBot;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

/**
 * Created by Jarrett on 02/14/17.
 */
public class SplixSettings {
    public static final int gameIterationsCount = 2000;
    public static final int pointsForKill = 300;
    
    // must be square for the ui to work correctly
    public static final Point2D viewingAreaSize = new Point2D(20, 20);
    
    public static final MutableList<Submission> players = Lists.mutable.of(
            new Submission<>("TrapBot 1", TrapBot::new),
            new Submission<>("HunterBot 1", HunterBot::new)
    );
    public static final int playersPerGame = 2;
    
}
