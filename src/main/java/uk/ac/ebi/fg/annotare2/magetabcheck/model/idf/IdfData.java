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

package uk.ac.ebi.fg.annotare2.magetabcheck.model.idf;

import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface IdfData {

    Info getInfo();

    Collection<Person> getContacts();

    Collection<ExperimentalDesign> getExperimentDesigns();

    Collection<ExperimentalFactor> getExperimentalFactors();

    Collection<QualityControlType> getQualityControlTypes();

    Collection<ReplicateType> getReplicateTypes();

    Collection<NormalizationType> getNormalizationTypes();

    Collection<Publication> getPublications();

    Collection<Protocol> getProtocols();

    Collection<TermSource> getTermSources();

    Collection<Comment> getComments();

    Collection<Comment> getComments(String name);

    TermSource getTermSource(String ref);

    Protocol getProtocol(String protocolRef);
}
