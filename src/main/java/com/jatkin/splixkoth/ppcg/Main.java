package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.jatkin.splixkoth.ppcg.players.TrapBot2;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.game.scoring.MamAggregator;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import com.nmerrill.kothcomm.game.KotHComm;

import java.util.Random;

/**
 * Created by Jarrett on 01/31/17.
 */
public class Main {
    public static void main(String[] args) {
        KotHComm runner = new KotHComm<>(() -> new SplixGame(80));
        runner.addSubmission("TrapBot 1.0", TrapBot::new);
        runner.addSubmission("TrapBot 2.0", TrapBot2::new);
        runner.setGameSize(2);
        runner.run(args);
    }
}
