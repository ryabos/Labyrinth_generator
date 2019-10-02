package com.ryabos.labirynth_generator;

import java.util.*;
import java.util.stream.Collectors;

public final class FastSchemeGenerator implements SchemeGenerator {
    private static final Random RANDOM                  = new Random();
    private final        int    xAmount;
    private final        int    yAmount;
    private final        int    xMax;
    private final        int    yMax;
    private final        int    amount;
    private final        int[]  roots;
    private              Line[] lines;
    private              int    groupCount;
    private              int    linesCount              = 0;
    private              int    connectedWithFrameCount = 0;
    private              int[]  connectedWithFrame;

    public FastSchemeGenerator(int xAmount, int yAmount) {
        this.xAmount = xAmount;
        this.yAmount = yAmount;
        this.xMax = xAmount - 1;
        this.yMax = yAmount - 1;
        amount = this.xAmount * this.yAmount;
        groupCount = amount;
        roots = new int[amount];
        connectedWithFrame = new int[amount];
        for (int i = 0; i < amount; i++) { roots[i] = i; }
    }

    @Override
    public Collection<Line> generate() {
        lines = new Line[amount * 2];
        generateFrame();
        generateRandomUnions();
        return Arrays.stream(lines).filter(Objects::nonNull).collect(Collectors.toList());
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
        lines[linesCount++] = new Line(x1, y1, x2, y2);
    }

    private void generateRandomUnions() {
        while (groupCount > 2) {
            final int p = connectedWithFrame[RANDOM.nextInt(connectedWithFrameCount)];
            union(p, RANDOM.nextBoolean(), RANDOM.nextBoolean());
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
        return find(p) == find(q);
    }

    private void union(int p, int q) {
        if (connected(p, q)) { return; }
        if (!(q % xAmount == 0 || q % xAmount == xAmount - 1)) {
            connectedWithFrame[connectedWithFrameCount++] = q;
        }
        final int x1 = (p % xAmount);
        final int y1 = (p / xAmount);
        final int x2 = (q % xAmount);
        final int y2 = (q / xAmount);
        addLine(x1, y1, x2, y2);

        roots[find(p)] = find(q);
        groupCount--;
    }

    private int find(int p) {
        while (roots[p] != p) {
            roots[p] = roots[roots[p]];
            p = roots[p];
        }
        return p;
    }
}
