/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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
public class CheckContext {

    public static final ThreadLocal<CheckContext> threadLocal = new ThreadLocal<CheckContext>();

    private CheckPosition position;

    public CheckPosition getPosition() {
        return position;
    }

    public void setPosition(CheckPosition position) {
        this.position = position;
    }

    public static void clearContext() {
        threadLocal.set(new CheckContext());
    }

    public static void setCheckPosition(int line, int column) {
        threadLocal.get().setPosition(new CheckPosition(line, column));
    }

    public static CheckPosition getCheckPosition() {
        return threadLocal.get().getPosition();
    }
}