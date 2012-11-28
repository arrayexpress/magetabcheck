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

package uk.ac.ebi.fg.annotare2.services.efo;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import uk.ac.ebi.fg.annotare2.services.efo.EfoLoader;

import java.io.File;
import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public class EfoTest {

    @Test
    public void test() {
        try {
            (new EfoLoader(new File("/Users/olkin/Projects/ebi/annotare"))).load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

}





