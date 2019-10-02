import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {
    private static final int PADDING = 4;
    private static final int STEP = 8;
    private static final double LINE_WIDTH = 1;
    private int[] ids;
    private int groupCount;
    private int xAmount = 100;
    private int yAmount = 100;
    private final int amount = xAmount * yAmount;
    private int width = STEP * xAmount;
    private int height = STEP * yAmount;
    private Canvas canvas = new Canvas(width + PADDING * 2, height + PADDING * 2);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();
        drawBase();
        show(primaryStage);

        generateRandomUnions();
    }

    private boolean connected(int p, int q) {
        return ids[p] == ids[q];
    }

    private void show(Stage primaryStage) {
        final Scene scene = new Scene(new Group(canvas));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initialize() {
        gc.setFill(Color.BLANCHEDALMOND);
        gc.setLineWidth(LINE_WIDTH);
        ids = new int[amount];
        groupCount = amount;
        for (int i = 0; i < amount; i++) { ids[i] = i; }
    }

    private void drawBase() {
        drawDots();
        drawFrame();
    }

    private void drawFrame() {
        for (int i = 0; i < xAmount - 1; i++) { union(i, i + 1); }
        final int door = yAmount / 2;
        for (int i = 0; i < door; i++) { union(xAmount * i, xAmount * i + xAmount); }
        for (int i = door + 1; i < yAmount - 1; i++) { union(xAmount * i, xAmount * i + xAmount); }
        for (int i = 0; i < xAmount - 1; i++) {
            union((xAmount * (yAmount - 1)) + i, (xAmount * (yAmount - 1)) + i + 1);
        }
        for (int i = 0; i < door; i++) { union(xAmount * i - 1 + xAmount, xAmount * i + xAmount - 1 + xAmount); }
        for (int i = door + 1; i < yAmount - 1; i++) {
            union(xAmount * i - 1 + xAmount, xAmount * i + xAmount - 1 + xAmount);
        }
    }

    private void drawDots() {
        for (int i = 0; i < width; i += STEP) {
            for (int j = 0; j < height; j += STEP) {
                strokeLine(i, j, i, j);
            }
        }
    }

    private void strokeLine(int x1, int y1, int x2, int y2) {
        gc.strokeLine(x1 + PADDING, y1 + PADDING, x2 + PADDING, y2 + PADDING);
    }

    private void union(int p, int q) {
        if (connected(p, q)) { return; }
        final int x1 = (p % xAmount) * STEP;
        final int y1 = (p / xAmount) * STEP;
        final int x2 = (q % xAmount) * STEP;
        final int y2 = (q / xAmount) * STEP;
        gc.setLineWidth(LINE_WIDTH);
        strokeLine(x1, y1, x2, y2);

        int findP = ids[p];
        int findQ = ids[q];
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == findP) {
                ids[i] = findQ;
            }
        }
        groupCount--;
    }

    private void generateRandomUnions() {
        final Random random = new Random();
        while (groupCount > 2) {
            final int p = random.nextInt(amount);
            if (p % xAmount == 0 || p % xAmount == xAmount - 1) { continue; }
            if (connected(0, p) || connected(p, amount - 1)) {
                union(p, random.nextBoolean(), random.nextBoolean());
            }
        }
    }

    private void union(int p, boolean vertical, boolean more) {
        if (vertical) {
            if (more) {
                if (p / xAmount < yAmount - 1) {
                    final int q = p + xAmount;
                    if (connectionIsAllowable(p, q)) {
                        union(p, q);
                    }
                }
            } else if (p > xAmount) {
                final int q = p - xAmount;
                if (connectionIsAllowable(p, q)) {
                    union(p, q);
                }
            }
        } else {
            if (more) {
                if (p % xAmount < xAmount - 1) {
                    final int q = p + 1;
                    if (connectionIsAllowable(p, q)) {
                        union(p, q);
                    }
                }
            } else if (p % xAmount > 0) {
                final int q = p - 1;
                if (connectionIsAllowable(p, q)) {
                    union(p, q);
                }
            }
        }
    }

    private boolean connectionIsAllowable(int p, int q) {
        return !(connected(0, p) && connected(q, amount - 1)) &&
                !(connected(0, q) && connected(p, amount - 1));
    }
}
