package com.jatkin.splixkoth.ppcg;/**
 * Created by Jarrett on 02/16/17.
 */

import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
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
import java.util.Random;


public class UIDebuggerRunner extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        primaryStage.setTitle("FXML TableView Example");

        TournamentPane<SplixPlayer, SplixGame> pane = new TournamentPane<SplixPlayer, SplixGame>(getTournament(), this::getNewGame);
        

        Scene myScene = new Scene(pane);
        
        primaryStage.setScene(myScene);
        primaryStage.show();

    }


    private TournamentRunner<SplixPlayer, SplixGame> getTournament() {
        int seed = new Random().nextInt();

        return new TournamentRunner<SplixPlayer, SplixGame>(
                new Sampling(SplixSettings.players, new Random(seed)),
                new ItemAggregator<>(),
                SplixSettings.playersPerGame,
                () -> new SplixGame(80), 
                new Random(seed));
    }

    private Pane getNewGame(SplixGame game) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UILayout.fxml"));
        
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {e.printStackTrace();}
        
        UIController controller = loader.getController();
        controller.setGame(game);
        
        return pane;
    }
}
