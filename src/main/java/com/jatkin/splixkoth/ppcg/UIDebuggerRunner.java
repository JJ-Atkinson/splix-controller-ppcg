package com.jatkin.splixkoth.ppcg;

/**
 * Created by Jarrett on 02/16/17.
 */

import com.jatkin.splixkoth.ppcg.game.KotHCommMultiThread;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.nmerrill.kothcomm.communication.Arguments;
import com.nmerrill.kothcomm.communication.languages.Language;
import com.nmerrill.kothcomm.communication.languages.java.JavaLoader;
import com.nmerrill.kothcomm.game.TournamentRunner;
import com.nmerrill.kothcomm.game.games.AbstractGame;
import com.nmerrill.kothcomm.game.players.AbstractPlayer;
import com.nmerrill.kothcomm.game.scoring.ItemAggregator;
import com.nmerrill.kothcomm.game.tournaments.Sampling;
import com.nmerrill.kothcomm.game.tournaments.Tournament;
import com.nmerrill.kothcomm.ui.gui.TournamentPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;


public class UIDebuggerRunner extends Application {

    public static SplixArguments arguments;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Object[] argsRaw = getParameters().getRaw().toArray();// really stupid hack, should just be able to cast...
        String[] args = Arrays.copyOf(argsRaw, argsRaw.length, String[].class);
        arguments = Arguments.parse(args, new SplixArguments());


        primaryStage.setTitle("Splix KotHComm Bot visualizer");

        TournamentPane<SplixPlayer, SplixGame> pane = new TournamentPane<>(getTournament(), this::getNewGame);


        Scene myScene = new Scene(pane);

        primaryStage.setScene(myScene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.show();

    }


    private TournamentRunner<SplixPlayer, SplixGame> getTournament() {
        Random random = arguments.getRandom();

        return new TournamentRunner<>(
                new Sampling<>(
                        KotHCommMultiThread.loadPlayers(
                                true,
                                arguments,
                                SplixSettings.languages.toList()),
                        random),
                new ItemAggregator<>(),
                SplixSettings.playersPerGame,
                () -> new SplixGame(SplixSettings.boardDims),
                random);
    }

    private Pane getNewGame(SplixGame game) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UILayout.fxml"));

        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UIController controller = loader.getController();
        controller.setGame(game);

        return pane;
    }
}
