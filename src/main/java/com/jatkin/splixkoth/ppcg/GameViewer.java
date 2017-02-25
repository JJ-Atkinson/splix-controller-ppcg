package com.jatkin.splixkoth.ppcg;

import com.jatkin.splixkoth.ppcg.game.SplixBoard;
import com.jatkin.splixkoth.ppcg.game.SplixPlayer;
import com.jatkin.splixkoth.ppcg.game.SplixPoint;
import com.jatkin.splixkoth.ppcg.game.readonly.ReadOnlyBoard;
import com.nmerrill.kothcomm.game.maps.Point2D;
import com.nmerrill.kothcomm.game.maps.graphmaps.bounds.point2D.SquareRegion;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.ui.gui.GameNode;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Jarrett on 02/23/17.
 */
public class GameViewer implements GameNode {

    private Canvas canvas;
    Supplier<SplixBoard> board;
    Function<SplixBoard, SquareRegion> getView;
    MutableMap<Submission<SplixPlayer>, Color> playerColors;
    GraphicsContext g;
    
    Timeline queuedDraw = new Timeline(new KeyFrame(Duration.seconds(0.1), ae -> doDraw()));

    Color background = Color.web("#3a342f");
    Color gray = Color.web("#4e463f");
    
    public GameViewer(Canvas canvas, Supplier<SplixBoard> board, Function<SplixBoard, SquareRegion> getView, MutableMap<Submission<SplixPlayer>, Color> playerColors) {
        this.canvas = canvas;
        this.board = board;
        this.getView = getView;
        this.playerColors = playerColors;
        queuedDraw.setCycleCount(1);
    }
    
    @Override
    public void draw() {
        if (queuedDraw.statusProperty().get() == Animation.Status.STOPPED) 
            Platform.runLater(queuedDraw::playFromStart);
    }
    
    private void doDraw() {
        if (g == null)
            g = canvas.getGraphicsContext2D();

        double percentFillSquareForDeadPoint = 0.7;
        double percentFillSquareForLivePoint = 0.85;
        double percentFillSquareForTrailPoint = 1.1;

        double graphicsSize = canvas.getHeight();

        SplixBoard board = this.board.get();
        SquareRegion region = getView.apply(board);
//        SquareRegion gameSize = board.getBounds();
        int offsetX = region.getLeft();
        int offsetY = region.getBottom();
        int regionSize = region.getWidth();

        double pixlesPerSplixPoint = graphicsSize / (regionSize+1);

        g.setFill(background);
        g.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        for (int y = offsetY; y <= region.getTop(); y++) {
            for (int x = offsetX; x <= region.getRight(); x++) {
                Point2D loc = new Point2D(x, y);
                if (board.getBounds().inBounds(loc)) {
                    SplixPoint point = board.get(loc);
                    
                    double pointXLoc = (loc.getX()-offsetX) * pixlesPerSplixPoint;
                    double pointYLoc = (regionSize - (loc.getY()-offsetY)) * pixlesPerSplixPoint;
    
    
                    if (point.getTypeOfClaimer() != null) {
                        g.setFill(playerColors.get(point.getTypeOfClaimer()));
                        g.fillRect(pointXLoc + ((1 - percentFillSquareForTrailPoint) / 2) * pixlesPerSplixPoint,
                                pointYLoc + ((1 - percentFillSquareForTrailPoint) / 2) * pixlesPerSplixPoint,
                                percentFillSquareForTrailPoint * pixlesPerSplixPoint,
                                percentFillSquareForTrailPoint * pixlesPerSplixPoint);
                    } else if (point.getTypeOfOwner() != null) {
                        g.setFill(playerColors.get(point.getTypeOfOwner()));
                        g.fillRect(pointXLoc + ((1 - percentFillSquareForLivePoint) / 2) * pixlesPerSplixPoint,
                                pointYLoc + ((1 - percentFillSquareForLivePoint) / 2) * pixlesPerSplixPoint,
                                percentFillSquareForLivePoint * pixlesPerSplixPoint,
                                percentFillSquareForLivePoint * pixlesPerSplixPoint);
                    } else {
                        g.setFill(gray);
                        g.fillRect(pointXLoc + ((1 - percentFillSquareForDeadPoint) / 2) * pixlesPerSplixPoint,
                                pointYLoc + ((1 - percentFillSquareForDeadPoint) / 2) * pixlesPerSplixPoint,
                                percentFillSquareForDeadPoint * pixlesPerSplixPoint,
                                percentFillSquareForDeadPoint * pixlesPerSplixPoint);
                    }
                }
            }
        }

        MutableMap<Submission<SplixPlayer>, Point2D> playerPositions = board.getPlayerPositions();
        playerPositions.forEach((player, pos) -> {
            double pointXLoc = (pos.getX()-offsetX) * pixlesPerSplixPoint;
            double pointYLoc = (regionSize - (pos.getY()-offsetY)) * pixlesPerSplixPoint;
            g.setFill(playerColors.get(player));
            g.setEffect(new DropShadow(10, 2, 2, Color.BLACK));
            g.fillOval(pointXLoc, pointYLoc, pixlesPerSplixPoint * 1.2, pixlesPerSplixPoint * 1.2);
            g.setEffect(null);
        });
    }
}
