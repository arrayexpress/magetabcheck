/*
 * Copyright 2012 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetab.checker;

import com.google.common.primitives.Ints;
import org.omg.CORBA.UNKNOWN;

/**
 * @author Olga Melnichuk
 */
public class CheckPosition implements Comparable<CheckPosition> {

    private static final int UNKNOWN_INDEX = -1;

    private static final CheckPosition UNKNOWN = new CheckPosition(UNKNOWN_INDEX, UNKNOWN_INDEX);

    private final int line;

    private final int column;

    private CheckPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String asString() {
        return "@(" + line + ", " + column + ")";
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    @Override
    public String toString() {
        return "CheckPosition{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }

    @Override
    public int compareTo(CheckPosition o) {
        if (isUnknown() && o.isUnknown()) {
            return 0;
        } else if (isUnknown()) {
            return 1;
        } else if (o.isUnknown()) {
            return -1;
        }

        int compareLines = Ints.compare(line, o.line);
        if (compareLines != 0) {
            return compareLines;
        }

        return Ints.compare(column, o.column);
    }

    public static CheckPosition newCheckPosition(int line, int column) {
        return line == UNKNOWN_INDEX || column == UNKNOWN_INDEX ? UNKNOWN : new CheckPosition(line, column);
    }

    public static CheckPosition unknownPosition() {
        return UNKNOWN;
    }
}
