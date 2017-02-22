package com.jatkin.splixkoth.ppcg;

/**
 * Created by Jarrett on 02/21/17.
 */
import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixGame;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.jatkin.splixkoth.ppcg.players.TrapBot;
import com.jatkin.splixkoth.ppcg.players.TrapBot2;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareBounds;
import com.nmerrill.kothcomm.game.players.Submission;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
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
    
    Color background = Color.web("#3a342f");
    Color gray = Color.web("#4e463f");
    
    MutableSet<Color> colors = Sets.mutable.of(
            Color.web("#a22929"),// red
            Color.web("#531880"),// purple
            Color.web("#27409c"),// blue
            Color.web("#2ACC38"),// green
            Color.web("#d2b732"),// yellow
            Color.web("#d06c18")// yellow
    );
    
    MutableMap<Submission<SplixPlayer>, Color> playerColors = Maps.mutable.empty();

    @FXML
    private Canvas localViewCanvas;
    private GraphicsContext localViewGraphics;

    @FXML
    private Canvas globalViewCanvas;
    private GraphicsContext globalViewGraphics;

    @FXML
    private Text turnsLeft;


    @FXML
    void initialize() {
        game = new SplixGame(20);
        
        players.toList().zip(colors.toList()).forEach(p -> playerColors.put(p.getOne(), p.getTwo()));
        
        MutableSet<SplixPlayer> playerInstances = players.collect(Submission::create);
        game.addPlayers(playerInstances);
        game.setRandom(new Random(-1085302355));
        game.setup();
        
        localViewGraphics = localViewCanvas.getGraphicsContext2D();
        globalViewGraphics = globalViewCanvas.getGraphicsContext2D();
        
        globalViewCanvas.widthProperty().bind(globalViewCanvas.heightProperty());
        localViewCanvas.widthProperty().bind(localViewCanvas.heightProperty());
        
    }
    
    @FXML
    void doGameStep(ActionEvent event) {
        game.step();
        
        updateGlobalCanvas();
    }

    private void updateGlobalCanvas() {
        double percentFillSquareForDeadPoint = 0.7;
        double percentFillSquareForLivePoint = 0.85;
        double percentFillSquareForTrailPoint = 1;

        double graphicsSize = globalViewCanvas.getHeight();

        SplixBoard board = game.getBoard();
        MutableSet<Point2D> locations = board.locations();
        SquareBounds gameSize = board.getBounds();
        int boardSize = gameSize.getTop();

        double pixlesPerSplixPoint = graphicsSize / (boardSize+1);

        // shorten calls
        GraphicsContext g = this.globalViewGraphics;
        
        g.setFill(background);
        g.fillRect(0,0, graphicsSize, graphicsSize);

        for (Point2D location : locations) {
            SplixPoint point = board.get(location);
            double pointXLoc = location.getX() * pixlesPerSplixPoint;
            double pointYLoc = (boardSize - location.getY()) * pixlesPerSplixPoint;

            
            if (point.getTypeOfClaimer() != null) {
                g.setFill(playerColors.get(point.getTypeOfClaimer()));
                g.fillRect(pointXLoc + ((1-percentFillSquareForTrailPoint)/2)*pixlesPerSplixPoint,
                           pointYLoc + ((1-percentFillSquareForTrailPoint)/2)*pixlesPerSplixPoint,
                           percentFillSquareForTrailPoint*pixlesPerSplixPoint,
                           percentFillSquareForTrailPoint*pixlesPerSplixPoint);
            } else if (point.getTypeOfOwner() != null) {
                g.setFill(playerColors.get(point.getTypeOfOwner()));
                g.fillRect(pointXLoc + ((1-percentFillSquareForLivePoint)/2)*pixlesPerSplixPoint,
                           pointYLoc + ((1-percentFillSquareForLivePoint)/2)*pixlesPerSplixPoint,
                           percentFillSquareForLivePoint*pixlesPerSplixPoint,
                           percentFillSquareForLivePoint*pixlesPerSplixPoint);
            } else {
                g.setFill(gray);
                g.fillRect(pointXLoc + ((1-percentFillSquareForDeadPoint)/2)*pixlesPerSplixPoint,
                           pointYLoc + ((1-percentFillSquareForDeadPoint)/2)*pixlesPerSplixPoint,
                        percentFillSquareForDeadPoint*pixlesPerSplixPoint,
                        percentFillSquareForDeadPoint*pixlesPerSplixPoint);
            }
        }

        MutableMap<Submission<SplixPlayer>, Point2D> playerPositions = board.getPlayerPositions();
        playerPositions.forEach((player, pos) -> {
            double pointXLoc = (pos.getX() - 0.1) * pixlesPerSplixPoint;
            double pointYLoc = (boardSize - pos.getY() - 0.1 ) * pixlesPerSplixPoint;
            g.setFill(playerColors.get(player));
            g.setEffect(new DropShadow(10, 2, 2, Color.BLACK ));
            g.fillOval(pointXLoc, pointYLoc, pixlesPerSplixPoint*1.2, pixlesPerSplixPoint*1.2);
            g.setEffect(null);
        });
    }
}

