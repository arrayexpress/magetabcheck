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


/**
 * This class stores dynamic error message - that provides further context to the canned error message provided
 * via @MageTabCheck.value
 *
 * @author Robert Petryszak
 */
public class CheckDynamicDetailSetter {

    public static final ThreadLocal<CheckDynamicDetailSetter> threadLocal = new ThreadLocal<CheckDynamicDetailSetter>();
    static {
        clearCheckDynamicDetail();
    }

    private String dynamicDetail;

    public String getDynamicDetail() {
        return dynamicDetail;
    }

    public void setDynamicDetail(String dynamicDetail) {
        this.dynamicDetail = dynamicDetail;
    }

    public static void clearCheckDynamicDetail() {
        threadLocal.set(new CheckDynamicDetailSetter());
    }

    public static void setCheckDynamicDetail(String dynamicDetail) {
        threadLocal.get().setDynamicDetail(dynamicDetail);
    }

    public static String getCheckDynamicDetail() {
        return threadLocal.get().getDynamicDetail();
    }
}
