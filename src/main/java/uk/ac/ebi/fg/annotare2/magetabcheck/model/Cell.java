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

package uk.ac.ebi.fg.annotare2.magetabcheck.model;

/**
 * @author Olga Melnichuk
 */
public class Cell<T> {

    private final T value;

    private final String sourceName;

    private final int line;

    private final int column;

    public Cell(T value) {
        this(value, null, -1, -1);
    }

    public Cell(T value, String sourceName, int line, int column) {
        this.value = value;
        this.sourceName = sourceName;
        this.line = line;
        this.column = column;
    }

    public T getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getSourceName() {
        return sourceName;
    }
}
