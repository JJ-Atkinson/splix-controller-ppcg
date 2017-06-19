package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.KotHCommMultiThread;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.nmerrill.kothcomm.communication.Arguments;
import com.nmerrill.kothcomm.communication.languages.java.JavaLoader;
import javafx.application.Application;

/**
 * Created by Jarrett on 01/31/17.
 */
public class Main {
    public static void main(String[] args) {
        SplixArguments arguments = new SplixArguments();
        arguments = Arguments.parse(args, arguments);

        if (arguments.useGui)
            Application.launch(UIDebuggerRunner.class, args);
        else
            runGames(arguments, args);
    }


    /**
     * @param args
     */
    static void runGames(SplixArguments args, String[] appargs) {
        long now = System.currentTimeMillis();
        KotHCommMultiThread<SplixPlayer, SplixGame> runner = new KotHCommMultiThread<>(() -> new SplixGame(SplixSettings.boardDims));

        SplixSettings.languages.each(runner::addLanguage);
        runner.setGameSize(SplixSettings.playersPerGame);
        runner.setArgumentParser(args);
        runner.run(appargs);
        System.out.println("Time to run: " + (System.currentTimeMillis() - now));
    }
}
