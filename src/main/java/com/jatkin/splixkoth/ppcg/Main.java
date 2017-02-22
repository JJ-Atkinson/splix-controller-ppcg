package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.jatkin.splixkoth.ppcg.players.TrapBot2;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Random;

/**
 * Created by Jarrett on 01/31/17.
 */
public class Main {
    public static void main(String[] args) {
        Random seedCreater = new Random();
        
        while (true) {
            int seed = seedCreater.nextInt();
            
            if (doAttemptGame(new Random(seed)))
                System.err.println("seed " + seed + " passed");
        }
//            System.err.println("fail");
        
    }

    private static boolean doAttemptGame(Random random) {
        SplixGame game = new SplixGame(20);
        MutableSet<Submission<SplixPlayer>> players = Sets.mutable.of(
                new Submission<>("tb1", TrapBot::new),
                new Submission<>("tb2", TrapBot2::new)
        );
        game.addPlayers(players.collect(Submission::create));
        game.setRandom(random);
        game.setup();
        
        for (int i = 0; i < 80; i++) {
            game.step();
            if (game.getBoard().getPlayerPositions().size() < 2) {
                System.out.println(game.getBoard().getPlayerPositions());
                return true;
            }
            
        }

        return false;
    }
}
