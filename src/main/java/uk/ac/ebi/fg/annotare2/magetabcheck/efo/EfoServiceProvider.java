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

package uk.ac.ebi.fg.annotare2.magetabcheck.efo;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public class EfoServiceProvider implements Provider<EfoService> {

    private static final Logger log = LoggerFactory.getLogger(EfoServiceProvider.class);

    private final EfoService service;

    @Inject
    public EfoServiceProvider(EfoServiceProperties properties) {
        service = load(properties);
    }

    private EfoService load(EfoServiceProperties properties) {
        if (properties != null) {
            try {
                EfoDag graph = new EfoLoader(properties).load();
                if (graph != null) {
                    return new EfoServiceImpl(graph);
                }
            } catch (IOException e) {
                log.error("EFO could not be loaded", e);
            } catch (OWLOntologyCreationException e) {
                log.error("EFO could not be loaded", e);
            }
        }
        return EfoService.UNAVAILABLE;
    }

    @Override
    public EfoService get() {
        return service;
    }
}