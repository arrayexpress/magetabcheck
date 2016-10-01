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


import javax.annotation.Nonnull;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.Ordering.natural;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckPosition.undefinedPosition;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResultType.*;

/**
 * @author Olga Melnichuk
 */
public class CheckResult implements Comparable<CheckResult> {

    private final CheckResultType type;

    private final CheckModality modality;

    private String title;

    private String dynamicDetail;

    private String reference;

    private Throwable exception;

    private CheckPosition position = undefinedPosition();

    private CheckResult(@Nonnull CheckResultType type, @Nonnull CheckModality modality, String reference) {
        this.type = type;
        this.modality = modality;
        this.reference = reference;
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
        this.position = position == null ? undefinedPosition() : position;
        return this;
    }

    private CheckResult setDynamicDetail(String dynamicDetail) {
        this.dynamicDetail = dynamicDetail;
        return this;
    }

    public static CheckResult checkSucceeded(String checkTitle, CheckModality checkModality, CheckPosition pos, String checkReference) {
        return new CheckResult(CHECK_SUCCESS, checkModality, checkReference)
                .setTitle(checkTitle)
                .setPosition(pos);
    }

    public static CheckResult checkFailed(String checkTitle, CheckModality checkModality, CheckPosition pos, String dynamicDetail, String checkReference) {
        return new CheckResult(CHECK_FAILURE, checkModality, checkReference)
                .setTitle(checkTitle)
                .setPosition(pos)
                .setDynamicDetail(dynamicDetail);
    }

    public static CheckResult checkBroken(String checkTitle, CheckModality checkModality, Throwable e, String checkReference) {
        return new CheckResult(RUN_ERROR, checkModality, checkReference)
                .setTitle(checkTitle)
                .setException(e);
    }

    public final CheckResultStatus getStatus() {
        return type.status(modality);
    }

    public final CheckPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "type=" + type +
                ", modality=" + modality +
                ", title='" + title + '\'' +
                ", exception=" + exception +
                ", position=" + position +
                dynamicDetail != null ? ", dynamicDetail="+dynamicDetail : "" +
                '}';
    }

    @Override
    public int compareTo(CheckResult o) {
        int statusCompare = getStatus().compareTo(o.getStatus());
        if (statusCompare != 0) {
            return statusCompare;
        }

        int positionCompare = natural().nullsLast().compare(position, o.position);
        if (positionCompare != 0) {
            return positionCompare;
        }

        return natural().nullsLast().compare(title, o.title);
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[")
                .append(getStatus())
                .append("]");

        sb.append(" ")
                .append(position.asString());

        sb.append(" ")
                .append(title == null ? "Unknown check" : title);

        if (dynamicDetail != null) {
            sb.append(" (" + dynamicDetail + ") ");
        }

        if (exception != null) {
            sb.append("\n")
                    .append(getStackTraceAsString(exception));
        }
        return sb.toString();
    }

    public CheckResultType getType() {
        return type;
    }

    public CheckModality getModality() {
        return modality;
    }

    public String getTitle() {
        return title;
    }

    public String getDynamicDetail() {
        return dynamicDetail;
    }

    public String getReference() {
        return reference;
    }
}

