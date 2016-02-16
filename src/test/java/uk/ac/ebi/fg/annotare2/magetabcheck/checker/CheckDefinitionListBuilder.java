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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class CheckDefinitionListBuilder {

    private final ClassInstanceProvider instanceProvider;

    private final List<CheckDefinition> list = new ArrayList<CheckDefinition>();

    public CheckDefinitionListBuilder(ClassInstanceProvider instanceProvider) {
        this.instanceProvider = instanceProvider;
    }

    public CheckDefinitionListBuilder() {
        this(ClassInstanceProvider.DEFAULT_CLASS_INSTANCE_PROVIDER);
    }

    public List<CheckDefinition> build() {
      return list;
    }

    public void addMethodBasedCheck(Class<?> source, String methodName, Class<?> target) {
        try {
            list.add(new MethodBasedCheckDefinition(
                    source.getMethod(methodName, target),
                    instanceProvider
            ));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void addClassBasedCheck(Class<?> clazz) {
        list.add(new ClassBasedCheckDefinition(clazz, instanceProvider));
    }
}
