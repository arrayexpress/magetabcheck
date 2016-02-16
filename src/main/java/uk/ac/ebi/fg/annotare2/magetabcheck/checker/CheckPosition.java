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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import uk.ac.ebi.fg.annotare2.magetabcheck.model.Cell;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.primitives.Ints.compare;

/**
 * @author Olga Melnichuk
 */
public class CheckPosition implements Comparable<CheckPosition> {

    private final String fileName;

    private final int line;

    private final int column;

    private CheckPosition(String fileName, int line, int column) {
        this.fileName = fileName;
        this.line = line;
        this.column = column;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String asString() {
        return isNullOrEmpty(fileName) ? "UNLOCATED" : fileName + "@(" + printIndex(line) + ", " + printIndex(column) + ")";
    }

    private String printIndex(int index) {
        return index < 0 ? "unknown" : Integer.toString(index);
    }

    public boolean isUndefined() {
        return line < 0 || column < 0 || isNullOrEmpty(fileName);
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
        if (isUndefined() && o.isUndefined()) {
            return 0;
        } else if (isUndefined()) {
            return 1;
        } else if (o.isUndefined()) {
            return -1;
        }

        int compared;
        if ((compared = fileName.compareTo(o.fileName)) != 0) {
            return compared;
        }

        if ((compared = compare(line, o.line)) != 0) {
            return compared;
        }

        return compare(column, o.column);
    }

    public static CheckPosition createPosition(Cell<?> cell) {
        return createPosition(cell.getSourceName(), cell.getLine(), cell.getColumn());
    }

    public static CheckPosition createPosition(String fileName, int line, int column) {
        return new CheckPosition(fileName, line, column);
    }

    public static CheckPosition undefinedPosition() {
        return new CheckPosition(null, -1, -1);
    }
}
