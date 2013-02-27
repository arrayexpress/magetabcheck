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

package uk.ac.ebi.fg.annotare2.magetabcheck;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.junit.Test;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.*;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedChecker;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckDefinitionsFactory.singleMethodCheck;
import static uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResultStatus.SUCCESS;

/**
 * @author Olga Melnichuk
 */
public class LimpopoModelTest {

    private static final String RNA_SEQ_TYPE = "RNA-seq of coding RNA";

    @Test
    public void magetabAssemblyTest() throws UknownExperimentTypeException {
        MAGETABInvestigation inv = new MAGETABInvestigation();
        inv.IDF.getComments().put("AEExperimentType", new HashSet<String>(asList(RNA_SEQ_TYPE)));
        inv.IDF.investigationTitle = "Test";

        Collection<CheckResult> results = (new LimpopoBasedChecker(new RnaSeqModule())).check(inv);
        assertEquals(1, results.size());

        CheckResult result = results.iterator().next();
        assertEquals(SUCCESS, result.getStatus());

        CheckPosition pos = result.getPosition();
        assertTrue(pos.isUndefined());
    }

    public static class RnaSeqModule extends AbstractModule {
        @Override
        protected void configure() {
            EfoService service = createMock(EfoService.class);
            expect(service.findHtsInvestigationType(RNA_SEQ_TYPE))
                    .andReturn("AN_ACCESSION");
            expect(service.findMaInvestigationType(RNA_SEQ_TYPE))
                    .andReturn(null);
            replay(service);

            bind(EfoService.class).toInstance(service);

            bind(new TypeLiteral<List<CheckDefinition>>() {
            }).toInstance(singleMethodCheck());

            install(new FactoryModuleBuilder().build(CheckerFactory.class));
        }
    }
}
