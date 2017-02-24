package com.jatkin.splixkoth.ppcg;

/**
 * Created by Jarrett on 02/21/17.
 */
import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.jatkin.splixkoth.ppcg.players.TrapBot2;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.ui.gui.GameRunnerPane;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Random;

public class UIController {
    SplixGame game;
    
    MutableSet<Submission<SplixPlayer>> players = 
            Sets.mutable.of(
                    new Submission<>("TrapBot 1.0", TrapBot::new),
                    new Submission<>("TrapBot 1.1", TrapBot2::new)
            );
    

    
    MutableSet<Color> colors = Sets.mutable.of(
            Color.web("#a22929"),// red
            Color.web("#531880"),// purple
            Color.web("#27409c"),// blue
            Color.web("#2ACC38"),// green
            Color.web("#d2b732"),// yellow
            Color.web("#d06c18")// yellow
    );

    private GameViewer localView;

    private GameViewer globalGameViewerView;



    @FXML
    private Canvas localViewCanvas;

    @FXML
    private Canvas globalViewCanvas;

    @FXML
    private Text turnsLeft;

    @FXML
    private VBox gameStateContainer;


    @FXML
    void initialize() {
        game = new SplixGame(20);
        
        MutableMap<Submission<SplixPlayer>, Color> playerColors = Maps.mutable.empty();
        players.toList().zip(colors.toList()).forEach(p -> playerColors.put(p.getOne(), p.getTwo()));
        
        MutableSet<SplixPlayer> playerInstances = players.collect(Submission::create);
        game.addPlayers(playerInstances);
        game.setRandom(new Random(-1085302355));
        game.setup();
        
        globalGameViewerView = new GameViewer(globalViewCanvas, () -> game.getBoard(), SplixBoard::locations, playerColors);
        
        GRP gameRunnerControls = new GRP(game);
        gameStateContainer.getChildren().add(gameRunnerControls);
        gameRunnerControls.addGameNode(globalGameViewerView);
        
        
    }
}

