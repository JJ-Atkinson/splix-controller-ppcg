package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.KotHCommMultiThread;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.nmerrill.kothcomm.game.KotHComm;

import java.util.Random;

/**
 * Created by Jarrett on 01/31/17.
 */
public class Main {
    public static void main(String[] args) {
        long now = System.currentTimeMillis();
        KotHCommMultiThread<SplixPlayer, SplixGame> runner = new KotHCommMultiThread<>(() -> new SplixGame(80));
        runner.setThreadCount(8);
//        runner.
        runner.addSubmission("TrapBot 1.0", TrapBot::new);
        runner.addSubmission("TrapBot 2.0", TrapBot::new);
        runner.setGameSize(2);
        runner.run(args);
        System.out.println(System.currentTimeMillis() - now);
    }
}
