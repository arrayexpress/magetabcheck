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

/**
 * @author Olga Melnichuk
 */
public class CheckPosition {

    private final int line;

    private final int column;

    public CheckPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public CheckPosition(int line) {
        this(line, -1);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "CheckPosition{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}
