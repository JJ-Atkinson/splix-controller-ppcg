package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.loaders.ClojureLoader;
import com.jatkin.splixkoth.ppcg.loaders.ClojurePlayer;
import com.nmerrill.kothcomm.communication.languages.Language;
import com.nmerrill.kothcomm.communication.languages.java.JavaLoader;
import com.nmerrill.kothcomm.game.maps.Point2D;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

/**
 * Created by Jarrett on 02/14/17.
 */
public class SplixSettings {
    public static final int gameIterationsCount = 2000;
    public static final int pointsForKill = 300;
    public static final int boardDims = 100;
    
    // must be square for the ui to work correctly
    public static final Point2D viewingAreaSize = new Point2D(20, 20);
    public static final int playersPerGame = 3;
    
    
    public static final ImmutableList<Language<SplixPlayer>> languages =
            Lists.immutable.of(
                    new JavaLoader<>(SplixPlayer.class),
                    new ClojureLoader<>(ClojurePlayer::new)
            );
}
