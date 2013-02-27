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

package uk.ac.ebi.fg.annotare2.magetabcheck.checker;

import uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf.IdfSimpleChecks;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class CheckDefinitionsFactory {

    public static List<CheckDefinition> singleMethodCheck() {
        try {
            List<CheckDefinition> list = new ArrayList<CheckDefinition>();
            list.add(new MethodBasedCheckDefinition(
                    IdfSimpleChecks.class.getMethod("investigationTitleRequired", Info.class),
                    ClassInstanceProvider.DEFAULT_CLASS_INSTANCE_PROVIDER
            ));
            return list;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
