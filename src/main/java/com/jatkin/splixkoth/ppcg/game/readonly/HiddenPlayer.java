package com.jatkin.splixkoth.ppcg.game.readonly;

import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.nmerrill.kothcomm.game.players.Submission;

/**
 * Created by Jarrett on 02/14/17.
 */
public class HiddenPlayer {
    private SplixPlayer backing;
    
    public HiddenPlayer(SplixPlayer backing) {
        this.backing = backing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HiddenPlayer that = (HiddenPlayer) o;
        if ((that.backing == null && backing != null) 
             || (that.backing != null && backing == null))
            return false;
        
                        // vvv both could be null
        return that.backing == backing || backing.equals(that.backing);
    }

    @Override
    public int hashCode() {
        return backing == null? 0 : backing.hashCode();
    }
}
