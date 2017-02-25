package com.jatkin.splixkoth.ppcg.game;

import com.jatkin.splixkoth.ppcg.game.readonly.HiddenPlayer;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;
import com.nmerrill.kothcomm.game.players.AbstractPlayer;

/**
 * Created by Jarrett on 02/02/17.
 */
public abstract class SplixPlayer extends AbstractPlayer<SplixPlayer> {

    protected abstract Direction makeMove(ReadOnlyGame game, ReadOnlyBoard board);
    
    protected HiddenPlayer getThisHidden() {return new HiddenPlayer(this.getType());}
    
    
}
