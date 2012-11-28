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

import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckPosition.createPosition;

/**
 * @author Olga Melnichuk
 */
public class CheckPositionKeeper {

    public static final ThreadLocal<CheckPositionKeeper> threadLocal = new ThreadLocal<CheckPositionKeeper>();

    private CheckPosition position;

    public CheckPosition getPosition() {
        return position;
    }

    public void setPosition(CheckPosition position) {
        this.position = position;
    }

    public static void clearCheckPosition() {
        threadLocal.set(new CheckPositionKeeper());
    }

    public static void setCheckPosition(String fileName, int line, int column) {
        threadLocal.get().setPosition(createPosition(fileName, line, column));
    }

    public static CheckPosition getCheckPosition() {
        return threadLocal.get().getPosition();
    }
}
