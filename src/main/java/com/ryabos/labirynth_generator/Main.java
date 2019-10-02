package com.ryabos.labirynth_generator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class Main extends Application {
    private static final int PADDING = 4;
    private static final int STEP = 8;
    private static final double LINE_WIDTH = 1;
    private int xAmount = 20;
    private int yAmount = 10;
    private int width = STEP * xAmount;
    private int height = STEP * yAmount;
    private Canvas canvas = new Canvas(width + PADDING * 2, height + PADDING * 2);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        gc.setFill(Color.BLANCHEDALMOND);
        gc.setLineWidth(LINE_WIDTH);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::drawLabirynth, 0, 1, MILLISECONDS);
        show(primaryStage);
    }

    private void drawLabirynth() {
        final Instant now = Instant.now();
        final Collection<SchemeGenerator.Line> generate = new FastSchemeGenerator(xAmount, yAmount).generate();
        System.out.println(xAmount + "/" + yAmount + " scheme was generated in " + Duration.between(now, Instant.now()).toMillis() + " ms");
        Platform.runLater(() -> {
            gc.clearRect(0, 0, width + PADDING * 2, height + PADDING * 2);
            for (SchemeGenerator.Line line : generate) {
                gc.strokeLine(line.x1 * STEP + STEP,
                        line.y1 * STEP + STEP,
                        line.x2 * STEP + STEP,
                        line.y2 * STEP + STEP);
            }
        });
    }

    private void show(Stage primaryStage) {
        final Scene scene = new Scene(new Group(canvas));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
