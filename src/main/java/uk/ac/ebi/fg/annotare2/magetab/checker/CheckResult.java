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


import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckModality.*;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResultType.CHECK_FAILURE;
import static uk.ac.ebi.fg.annotare2.magetab.checker.CheckResultType.CHECK_SUCCESS;

/**
 * @author Olga Melnichuk
 */
public class CheckResult {

    private CheckResultType type;

    private CheckModality modality;

    private String title;

    private Throwable exception;

    private CheckPosition position;

    private CheckResult() {
    }

    private CheckResult setType(CheckResultType type) {
        this.type = type;
        return this;
    }

    private CheckResult setModality(CheckModality modality) {
        this.modality = modality;
        return this;
    }

    private CheckResult setTitle(String title) {
        this.title = title;
        return this;
    }

    public CheckResult setException(Throwable exception) {
        this.exception = exception;
        return this;
    }

    private CheckResult setPosition(CheckPosition position) {
        this.position = position;
        return this;
    }

    public static CheckResult checkSucceeded(String checkTitle) {
        return new CheckResult()
                .setType(CHECK_SUCCESS)
                .setTitle(checkTitle);
    }

    public static CheckResult checkFailed(String checkTitle, CheckModality checkModality, CheckPosition pos) {
        return new CheckResult()
                .setType(CHECK_FAILURE)
                .setTitle(checkTitle)
                .setModality(checkModality)
                .setPosition(pos);
    }

    public static CheckResult checkBroken(String checkTitle, CheckModality checkModality, Throwable e) {
        return new CheckResult()
                .setType(CheckResultType.RUN_ERROR)
                .setTitle(checkTitle)
                .setModality(checkModality)
                .setException(e);
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append(position == null ? -1 : position.getLine())
                .append(", ")
                .append(position == null ? -1 : position.getColumn());

        sb.append(" : ");

        if (isSuccess()) {
            sb.append("SUCCESS");
        } else if (isError() || isWarning()) {
            sb.append(modality);
        } else if (isException()) {
            sb.append("EXCEPTION");
        } else {
            sb.append("OTHER");
        }

        sb.append(" : ")
                .append(title == null ? "Unknown check" : title);

        if (exception != null) {
            sb.append(" : ")
                    .append(exception.getMessage())
                    .append("; See logs for details");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "type=" + type +
                ", modality=" + modality +
                ", title='" + title + '\'' +
                ", exception=" + exception +
                ", position=" + position +
                '}';
    }

    public boolean isSuccess() {
        return type == CHECK_SUCCESS;
    }

    public boolean isWarning() {
        return type == CHECK_FAILURE && modality == WARNING;
    }

    public boolean isError() {
        return type == CHECK_FAILURE && modality == ERROR;
    }

    public boolean isException() {
        return exception != null;
    }
}
