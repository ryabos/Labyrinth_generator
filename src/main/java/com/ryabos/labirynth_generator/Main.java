package com.ryabos.labirynth_generator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;


public class Main extends Application {
    private static final int PADDING = 4;
    private final Spinner<Integer> widthField = createSpinner(3, 10000, 400);
    private final Spinner<Integer> heightField = createSpinner(3, 10000, 400);
    private final Spinner<Integer> stepField = createSpinner(2, 200, 8);
    private Canvas canvas = new Canvas();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button refreshButton = new Button("Нарисовать лабиринт");
        refreshButton.setOnAction(event -> drawLabyrinth());
        drawLabyrinth();
        final Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.jpg", "*.jpg"));
            final File file = fileChooser.showSaveDialog(primaryStage);
            saveImage(file);
        });
        VBox root = new VBox(PADDING,
                new HBox(PADDING,
                        refreshButton,
                        new Label("Ширина"), widthField,
                        new Label("Высота"), heightField,
                        new Label("Шаг"), stepField,
                        saveButton),
                createScrollPane(canvas));
        root.setPadding(new Insets(4));
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveImage(File file) {
        file.mkdirs();
        Image myJavaFXImage = canvas.snapshot(null, null);
        try {
            BufferedImage image = SwingFXUtils.fromFXImage(myJavaFXImage, null);
            BufferedImage imageRGB = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.OPAQUE);
            Graphics2D graphics = imageRGB.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            ImageIO.write(imageRGB, "jpg", file);
            graphics.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawLabyrinth() {
        drawLabyrinth(stepField.getValue(), widthField.getValue(), heightField.getValue());
    }

    private ScrollPane createScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setPannable(true);
        return scrollPane;
    }

    private Spinner<Integer> createSpinner(int min, int max, int initial) {
        Spinner<Integer> spinner = new Spinner<>(min, max, initial);
        spinner.setEditable(true);
        spinner.setOnScroll(event -> {
            if (event.getDeltaY() > 0) {
                spinner.increment();
            } else {
                spinner.decrement();
            }
        });
        return spinner;
    }

    private void drawLabyrinth(int step, int xAmount, int yAmount) {
        canvas.setWidth(stepField.getValue() * widthField.getValue() + PADDING * 2);
        canvas.setHeight(stepField.getValue() * heightField.getValue() + PADDING * 2);
        final Instant now = Instant.now();
        final Collection<SchemeGenerator.Line> generate = new FastSchemeGenerator(xAmount, yAmount).generate();
        System.out.println(xAmount + "/" + yAmount + " scheme was generated in " + Duration.between(now, Instant.now())
                                                                                           .toMillis() + " ms");
        Platform.runLater(() -> {
            canvas.getGraphicsContext2D()
                  .clearRect(0, 0, step * xAmount + PADDING * 2, step * yAmount + PADDING * 2);
            for (SchemeGenerator.Line line : generate) {
                canvas.getGraphicsContext2D()
                      .strokeLine(line.x1 * step + step,
                              line.y1 * step + step,
                              line.x2 * step + step,
                              line.y2 * step + step);
            }
        });
    }

}
