package com.jatkin.splixkoth.ppcg;

/**
 * Created by Jarrett on 02/21/17.
 */
import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.ui.gui.GameRunnerPane;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Set;

public class UIController {
    SplixGame game;
    
    MutableSet<Color> colors = Sets.mutable.of(
            Color.web("#a22929"),// red
            Color.web("#4760bc"),// blue
            Color.web("#2ACC38"),// green
            Color.web("#d2b732"),// yellow
            Color.web("#d06c18"),// yellow
            Color.web("#632890")// purple
    );
    
    private GameViewer localGameViewerView;
    private GameViewer globalGameViewerView;
    private SplixPlayer playerFollowed;

    @FXML
    private Canvas localViewCanvas;
    
    @FXML
    private Canvas globalViewCanvas;

    @FXML
    private GridPane root;
    
    @FXML
    private Text turnsLeft;

    @FXML
    private VBox gameStateContainer;

    @FXML
    private ComboBox<SplixPlayer> playerChoiceComboBox;
    private GameRunnerPane gameRunnerControls;

    public void setGame(SplixGame game) {
        this.game = game;
        
        setup();
    }
    
    void setup() {
        MutableSet<SplixPlayer> players = game.getBoard().getPlayerPositions().keysView().toSet();
        
        
        MutableMap<SplixPlayer, Color> playerColors = Maps.mutable.empty();
        players.toList().zip(colors.toList()).forEach(p -> playerColors.put(p.getOne(), p.getTwo())); 


        globalGameViewerView = new GameViewer(globalViewCanvas, () -> game.getBoard(), SplixBoard::getBounds, playerColors);
        localGameViewerView = new GameViewer(localViewCanvas, () -> game.getBoard(), (board -> {
            boolean playersDead = ensurePlayerFollowedNotDead();
            Point2D playerPos = board.getPlayerPositions().get(playerFollowed);
            return playersDead ? board.getBounds() : game.getReadOnlyBoardForPosition(playerPos).viewingArea;
        }), playerColors);


        gameRunnerControls = new GameRunnerPane(game);
        gameStateContainer.getChildren().add(gameRunnerControls);
        gameRunnerControls.addGameNode(globalGameViewerView);
        gameRunnerControls.addGameNode(localGameViewerView);


        BorderPane lvcParent = (BorderPane) localViewCanvas.getParent();
        NumberBinding graphicsSize = Bindings.min(lvcParent.widthProperty(), lvcParent.heightProperty());
        graphicsSize.addListener(observable -> {
            globalGameViewerView.draw();
            localGameViewerView.draw();
        });

        globalViewCanvas.widthProperty().bind(graphicsSize);
        globalViewCanvas.heightProperty().bind(graphicsSize);
        localViewCanvas.widthProperty().bind(graphicsSize);
        localViewCanvas.heightProperty().bind(graphicsSize);
        
        root.parentProperty().addListener(obs -> {
            root.prefWidthProperty().bind(((Pane)root.getParent()).widthProperty());
            root.prefHeightProperty().bind(((Pane)root.getParent()).heightProperty());
        });
        
        playerChoiceComboBox.getItems().addAll(players);
        playerChoiceComboBox.valueProperty().addListener((b, o, newValue) ->
            {playerFollowed = newValue; localGameViewerView.draw();});
        playerChoiceComboBox.valueProperty().set(players.toList().get(1));

        SimpleIntegerProperty turnsLeftIntProp = new SimpleIntegerProperty(game.getRemainingIterations());
        turnsLeft.textProperty().bind(Bindings.convert(turnsLeftIntProp));
        gameRunnerControls.addGameNode(() -> Platform.runLater(() -> turnsLeftIntProp.set(game.getRemainingIterations())));
    }
    
    private boolean hasAlertedUser = false;
    private boolean ensurePlayerFollowedNotDead() {
        Set<SplixPlayer> alivePlayers = game.getBoard().getPlayerPositions().keySet();
        if (alivePlayers.isEmpty()) {
            
//            if (game.finished() == false)
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("All players dead. Game now finished.");
            if (!hasAlertedUser)
                alert.show();
            hasAlertedUser = true;
            
            gameRunnerControls.removeGameNode(globalGameViewerView);
            gameRunnerControls.removeGameNode(localGameViewerView);
            return true;
        }
        
        if (!alivePlayers.contains(playerFollowed)) {
            playerFollowed = alivePlayers.iterator().next();
        }
        return false;
    }

}

