package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.KotHCommMultiThread;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.players.HunterBot;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.nmerrill.kothcomm.communication.Arguments;
import com.nmerrill.kothcomm.game.KotHComm;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by Jarrett on 01/31/17.
 */ 
public class Main {
    public static void main(String[] args) {
        runGames(args, 
                new String[]{"TrapBot 1.0", "HunterBot 1.0"},
                new Supplier[] {TrapBot::new, HunterBot::new});
    }


    /**
     * 
     * @param args
     */
    static void runGames(String[] args, String[] botNames, Supplier<SplixPlayer>[] botConstructors) {
        Arguments arguments = new Arguments();
        arguments.iterations = 1000;
        long now = System.currentTimeMillis();
        KotHCommMultiThread<SplixPlayer, SplixGame> runner = new KotHCommMultiThread<>(() -> new SplixGame(200));
        runner.setThreadCount(8);
//        runner.

        for (int i = 0; i < botNames.length; i++) {
            runner.addSubmission(botNames[i], botConstructors[i]);
        }
        
        runner.setGameSize(2);
        runner.setArgumentParser(arguments);
        runner.run(args);
        System.out.println("Time to run: " + (System.currentTimeMillis() - now));
    }
}
