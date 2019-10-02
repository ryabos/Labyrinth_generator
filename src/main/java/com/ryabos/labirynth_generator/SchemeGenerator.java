package com.ryabos.labirynth_generator;

import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;

public interface SchemeGenerator {
    Collection<Line> generate();

    final class Line {
        public final int x1, y1, x2, y2;

        Line(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public final int hashCode() {

            return Objects.hash(x1, y1, x2, y2);
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Line line = (Line) o;
            return x1 == line.x1 &&
                    y1 == line.y1 &&
                    x2 == line.x2 &&
                    y2 == line.y2;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Line.class.getSimpleName() + "[", "]")
                    .add("x1=" + x1)
                    .add("y1=" + y1)
                    .add("x2=" + x2)
                    .add("y2=" + y2)
                    .toString();
        }
    }
}
