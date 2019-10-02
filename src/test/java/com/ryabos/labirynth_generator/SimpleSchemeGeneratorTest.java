package com.ryabos.labirynth_generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleSchemeGeneratorTest {
    private final int xAmount = 20;
    private final int yAmount = 10;
    private final int xMax = xAmount - 1;
    private final int yMax = yAmount - 1;
    private Collection<SchemeGenerator.Line> lines;

    @BeforeEach
    void setUp() {
        SchemeGenerator generator = new SimpleSchemeGenerator(xAmount, yAmount);
        lines = generator.generate();
    }

    @Test
    void schemeOfDefinedSizeIsReturned() {
        Assertions.assertEquals(0, lines.stream().mapToInt(line -> line.x1).min().orElse(-1));
        Assertions.assertEquals(0, lines.stream().mapToInt(line -> line.y1).min().orElse(-1));
        Assertions.assertEquals(xAmount - 1, lines.stream().mapToInt(line -> line.x2).max().orElse(-1));
        Assertions.assertEquals(yAmount - 1, lines.stream().mapToInt(line -> line.y2).max().orElse(-1));
    }

    @Test
    void frameIsGeneratedAroundScheme() {
        assertTrue(lines.stream().anyMatch(line -> line.x1 == 0 && line.y1 == 0 && line.x2 == xMax && line.y2 == 0));
        assertTrue(lines.stream().anyMatch(line -> line.x1 == 0 && line.y1 == 0 && line.x2 == 0 && line.y2 == 5));
        assertTrue(lines.stream().anyMatch(line -> line.x1 == 0 && line.y1 == 6 && line.x2 == 0 && line.y2 == yMax));
        assertTrue(lines.stream().anyMatch(line -> line.x1 == xMax && line.y1 == 0 && line.x2 == xMax && line.y2 == 5));
        assertTrue(lines.stream().anyMatch(line -> line.x1 == xMax && line.y1 == 6 && line.x2 == xMax && line.y2 == yMax));
        assertTrue(lines.stream().anyMatch(line -> line.x1 == 0 && line.y1 == yMax && line.x2 == xMax && line.y2 == yMax));
    }

    @Test
    void thereNoSingleDotsInScheme() {
        assertTrue(lines.stream().noneMatch(line -> line.x1 == line.x2 && line.y1 == line.y2));
    }

    @Test
    void thereNoObliqueLinesInScheme() {
        assertTrue(lines.stream().noneMatch(line -> Math.abs(line.x1 - line.x2) + Math.abs(line.y1 - line.y2) == 2));
    }

    @Test
    void everyDotInSchemeIsCrossedByLine() {
        for (int x = 0; x < xAmount; x++) {
            for (int y = 0; y < yAmount; y++) {
                int finalX = x;
                int finalY = y;
                assertTrue(lines.stream().anyMatch(line ->
                        (line.x1 == finalX || line.x2 == finalX) &&
                                (line.y1 == finalY || line.y2 == finalY)));

            }
        }
    }
}