/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Term;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;

import static uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownTermSource.EFO;

/**
 * @author Olga Melnichuk
 */
public class MageTabCheckEfoImpl implements MageTabCheckEfo {

    private final EfoService efoService;

    @Inject
    public MageTabCheckEfoImpl(EfoService efoService) {
        this.efoService = efoService;
    }

    @Override
    public String findHtsInvestigationType(String name) {
        EfoTerm term = findTermByLabelInSubClasses(name, AE_EXPERIMENT_TYPES, HTS_ASSAY);
        return term == null ? null : term.getAccession();
    }

    @Override
    public String findArrayInvestigationType(String name) {
        EfoTerm term = findTermByLabelInSubClasses(name, AE_EXPERIMENT_TYPES, ARRAY_ASSAY);
        return term == null ? null : term.getAccession();
    }

    @Override
    public boolean isProtocolType(Term term, String protocolEfoId) {
        return isEfoTermSource(term.getSource().getValue())
                && efoService.findTermByLabelOrAccession(
                term.getName().getValue(), term.getAccession().getValue(), protocolEfoId) != null;
    }

    private boolean isEfoTermSource(TermSource ts) {
        return ts == null || EFO.matches(ts.getFile().getValue());
    }

    private EfoTerm findTermByLabelInSubClasses(String name, String... subclasses) {
        EfoTerm prev = null;
        for (String subclass : subclasses) {
            EfoTerm term = efoService.findTermByLabel(name, subclass);
            if (term == null) {
                return null;
            }
            if (prev != null && !prev.getAccession().equals(term.getAccession())) {
                return null;
            }
            prev = term;
        }
        return prev;
    }
}
