package com.jatkin.splixkoth.ppcg.loaders;

import clojure.lang.IPersistentVector;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;
import com.jatkin.splixkoth.ppcg.game.Direction;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyGame;

/**
 * Created by Jarrett on 06/18/17.
 */
public class ClojurePlayer extends SplixPlayer {

    Object state = null;
   
    
    @Override
    protected Direction makeMove(ReadOnlyGame game, ReadOnlyBoard board) {
        Var makeMoveFn = RT.var(getName(), "make-move");
        IPersistentVector ret = (IPersistentVector) makeMoveFn.invoke(game, board, state);
        Direction dir = (Direction)ret.nth(0);
        state = ret.nth(1);
        return dir;
    }
    
}
