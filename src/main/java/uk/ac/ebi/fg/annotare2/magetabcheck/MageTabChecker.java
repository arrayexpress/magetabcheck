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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.MageTabCheckEfo;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.Experiment;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Comment;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.IdfData;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public class MageTabChecker {

    private static final Logger log = LoggerFactory.getLogger(MageTabChecker.class);

    private static final String AE_EXPERIMENT_TYPE_COMMENT = "AEExperimentType";

    private final MageTabCheckEfo efoService;

    private final CheckerFactory checkerFactory;

    @Inject
    public MageTabChecker(MageTabCheckEfo efoService,
                          CheckerFactory checkerFactory) {
        this.efoService = efoService;
        this.checkerFactory = checkerFactory;
    }

    public Collection<CheckResult> check(Experiment exp, ExperimentType type) {
        log.info("The experiment type is '{}'; running the checks..", type);
        return (checkerFactory.create(type).check(exp));
    }

    public Collection<CheckResult> check(Experiment exp) throws UknownExperimentTypeException {
        log.debug("The experiment type is not given explicitly");
        return check(exp, guessType(exp.getIdfData()));
    }

    private ExperimentType guessType(IdfData idf) throws UknownExperimentTypeException {
        log.info("Looking for an experiment type in 'Comment[{}]' IDF field...", AE_EXPERIMENT_TYPE_COMMENT);
        Collection<Comment> comments = idf.getComments(AE_EXPERIMENT_TYPE_COMMENT);
        if (comments.isEmpty()) {
            throw new UknownExperimentTypeException("IDF doesn't contain '" + AE_EXPERIMENT_TYPE_COMMENT +
                    "' comment; can't find the experiment type.");
        }
        return lookupTypeInEfo(comments.iterator().next().getValue().getValue());
    }

    private ExperimentType lookupTypeInEfo(String type) throws UknownExperimentTypeException {
        log.debug("Comment[{}]='{}' has been found. Checking if it's defined in EFO...", AE_EXPERIMENT_TYPE_COMMENT, type);

        if (isMicroArrayExperiment(type)) {
            return ExperimentType.MICRO_ARRAY;
        } else if (isHtsExperiment(type)) {
            return ExperimentType.HTS;
        }
        throw new UknownExperimentTypeException("Can't find '" + type + "' in EFO");
    }

    private boolean isHtsExperiment(String type) {
        return efoService.findHtsInvestigationType(type) != null;
    }

    private boolean isMicroArrayExperiment(String type) {
        return efoService.findMaInvestigationType(type) != null;
    }
}
