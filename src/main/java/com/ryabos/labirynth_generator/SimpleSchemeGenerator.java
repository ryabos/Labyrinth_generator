package com.ryabos.labirynth_generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public final class SimpleSchemeGenerator implements SchemeGenerator {
    private int[] ids;
    private final int xAmount;
    private final int yAmount;
    private final int xMax;
    private final int yMax;
    private ArrayList<Line> lines;
    private final int amount;
    private int groupCount;

    public SimpleSchemeGenerator(int xAmount, int yAmount) {
        this.xAmount = xAmount;
        this.yAmount = yAmount;
        this.xMax = xAmount - 1;
        this.yMax = yAmount - 1;
        amount = this.xAmount * this.yAmount;
        ids = new int[amount];
        groupCount = amount;
        for (int i = 0; i < amount; i++) {
            ids[i] = i;
        }
    }

    @Override
    public Collection<Line> generate() {
        lines = new ArrayList<>();
        generateFrame();
        generateRandomUnions();
        return lines;
    }

    private void generateFrame() {
        addLine(0, 0, xMax, 0);
        addLine(0, yMax, xMax, yMax);
        addLine(0, 0, 0, yAmount / 2);
        addLine(0, yAmount / 2 + 1, 0, yMax);
        addLine(xMax, 0, xMax, yAmount / 2);
        addLine(xMax, yAmount / 2 + 1, xMax, yMax);

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

    private void addLine(int x1, int y1, int x2, int y2) {
        lines.add(new Line(x1, y1, x2, y2));
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

    private boolean connectionIsAllowable(int p, int q) {
        return !(connected(0, p) && connected(q, amount - 1)) &&
                !(connected(0, q) && connected(p, amount - 1));
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

    private boolean connected(int p, int q) {
        return ids[p] == ids[q];
    }

    private void union(int p, int q) {
        if (connected(p, q)) { return; }
        final int x1 = (p % xAmount);
        final int y1 = (p / xAmount);
        final int x2 = (q % xAmount);
        final int y2 = (q / xAmount);
        addLine(x1, y1, x2, y2);

        int findP = ids[p];
        int findQ = ids[q];
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == findP) {
                ids[i] = findQ;
            }
        }
        groupCount--;
    }
}
