package com.jatkin.splixkoth.ppcg;

/**
 * Created by Jarrett on 02/21/17.
 */
import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.ui.gui.GameRunnerPane;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
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
                    new Submission<>("TrapBot 1.1", TrapBot::new)
            );
    

    
    MutableSet<Color> colors = Sets.mutable.of(
            Color.web("#a22929"),// red
            Color.web("#4760bc"),// blue
            Color.web("#2ACC38"),// green
            Color.web("#d2b732"),// yellow
            Color.web("#d06c18"),// yellow
            Color.web("#531880")// purple
    );
    
    private GameViewer localGameViewerView;
    private GameViewer globalGameViewerView;
    private Submission<SplixPlayer> playerFollowed;

    @FXML
    private Canvas localViewCanvas;

    @FXML
    private Canvas globalViewCanvas;

    @FXML
    private Text turnsLeft;

    @FXML
    private VBox gameStateContainer;

    @FXML
    private ComboBox<Submission<SplixPlayer>> playerChoiceComboBox;
    
    public void setGame(SplixGame game) {
        this.game = game;
        
        setup();
    }
    
    void setup() {
        MutableSet<Submission<SplixPlayer>> players = game.getBoard().getPlayerPositions().keysView().toSet();
        
        
        MutableMap<Submission<SplixPlayer>, Color> playerColors = Maps.mutable.empty();
        players.toList().zip(colors.toList()).forEach(p -> playerColors.put(p.getOne(), p.getTwo())); 


        globalGameViewerView = new GameViewer(globalViewCanvas, () -> game.getBoard(), SplixBoard::getBounds, playerColors);
        localGameViewerView = new GameViewer(localViewCanvas, () -> game.getBoard(), (board -> {
            Point2D playerPos = board.getPlayerPositions().get(playerFollowed);
            return game.getReadOnlyBoardForPosition(playerPos).viewingArea;
        }), playerColors);


        GameRunnerPane gameRunnerControls = new GameRunnerPane(game);
        gameStateContainer.getChildren().add(gameRunnerControls);
        gameRunnerControls.addGameNode(globalGameViewerView);
        gameRunnerControls.addGameNode(localGameViewerView);


        BorderPane gvcParent = (BorderPane) globalViewCanvas.getParent();
        NumberBinding graphicsSize = Bindings.min(gvcParent.widthProperty(), gvcParent.heightProperty());
        graphicsSize.addListener(observable -> {
            globalGameViewerView.draw();
            localGameViewerView.draw();
        });

        globalViewCanvas.widthProperty().bind(graphicsSize);
        globalViewCanvas.heightProperty().bind(graphicsSize);
        localViewCanvas.widthProperty().bind(graphicsSize);
        localViewCanvas.heightProperty().bind(graphicsSize);


        playerChoiceComboBox.getItems().addAll(players);
        playerChoiceComboBox.valueProperty().addListener((b, o, newValue) ->
        {playerFollowed = newValue; localGameViewerView.draw();});
        playerChoiceComboBox.valueProperty().set(players.toList().get(1));
    }

}

