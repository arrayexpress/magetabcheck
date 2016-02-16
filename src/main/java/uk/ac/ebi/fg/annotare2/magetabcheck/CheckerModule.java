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

package uk.ac.ebi.fg.annotare2.magetabcheck;

import com.google.inject.*;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.*;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class CheckerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EfoServiceProperties.class).to(MageTabCheckProperties.class);
        bind(EfoService.class).toProvider(EfoServiceProvider.class).in(Scopes.SINGLETON);
        bind(MageTabCheckEfo.class).to(MageTabCheckEfoImpl.class).in(Scopes.SINGLETON);

        bind(AllChecks.class).to(AllChecksImpl.class).in(Scopes.SINGLETON);

        bind(new TypeLiteral<List<CheckDefinition>>() {
        }).toProvider(CheckListProvider.class);

        install(new FactoryModuleBuilder().build(CheckerFactory.class));
    }

    @Provides
    @Singleton
    public MageTabCheckProperties getProperties() {
        return MageTabCheckProperties.load();
    }
}

