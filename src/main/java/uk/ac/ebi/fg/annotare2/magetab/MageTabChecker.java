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

package uk.ac.ebi.fg.annotare2.magetab;

import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetab.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetab.checker.Checker;
import uk.ac.ebi.fg.annotare2.magetab.checker.InvestigationType;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Comment;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraph;
import uk.ac.ebi.fg.annotare2.services.efo.EfoNode;
import uk.ac.ebi.fg.annotare2.services.efo.EfoService;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public class MageTabChecker {

    private static final Logger log = LoggerFactory.getLogger(MageTabChecker.class);

    private static final String AE_EXPERIMENT_TYPE_COMMENT = "AEExperimentType";

    private final Injector injector;

    public MageTabChecker(Injector injector) {
        this.injector = injector;
    }

    public Collection<CheckResult> check(IdfData idf, SdrfGraph sdrf, InvestigationType type) {
        log.debug("The experiment type is '{}'; running the checks..", type);
        Checker checker = new Checker(injector, type);
        checker.check(idf);
        checker.check(sdrf);
        return checker.getResults();
    }

    public Collection<CheckResult> check(IdfData idf, SdrfGraph sdrf) throws UndefinedInvestigationTypeException {
        log.debug("The experiment type is not given explicit");
        return check(idf, sdrf, guessType(idf));
    }

    private InvestigationType guessType(IdfData idf) throws UndefinedInvestigationTypeException {
        log.debug("Looking for an experiment type in 'Comment[{}]' IDF field...", AE_EXPERIMENT_TYPE_COMMENT);
        Collection<Comment> comments = idf.getComments(AE_EXPERIMENT_TYPE_COMMENT);
        if (comments.isEmpty()) {
            throw new UndefinedInvestigationTypeException("IDF doesn't contain '" + AE_EXPERIMENT_TYPE_COMMENT +
                    "' comment; can't recognize an experiment type.");
        }
        return findType(comments.iterator().next().getComment().getValue());
    }

    private InvestigationType findType(String type) throws UndefinedInvestigationTypeException {
        log.debug("Comment[{}]='{}' has been found. Checking if it's defined in EFO...", AE_EXPERIMENT_TYPE_COMMENT, type);
        EfoService service = injector.getInstance(EfoService.class);
        EfoNode node = service.findMaInvestigationType(type);
        if (node != null) {
            return InvestigationType.MICRO_ARRAY;
        }

        node = service.findHtsInvestigationType(type);
        if (node != null) {
            return InvestigationType.HTS;
        }
        throw new UndefinedInvestigationTypeException("Unknown Comment[" +
                AE_EXPERIMENT_TYPE_COMMENT + "] value: '" + type + "'");
    }
}
