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

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.*;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.primitives.Ints.max;

/**
 * @author Olga Melnichuk
 */
public class LimpopoIdfDataProxy implements IdfData {

    private final IdfHelper idfHelper;

    public LimpopoIdfDataProxy(@Nonnull IDF idf) {
        this.idfHelper = new IdfHelper(idf);
    }

    @Override
    public Info getInfo() {
        return new LimpopoBasedInfo(idfHelper);
    }

    @Override
    public Collection<Person> getContacts() {
        int size = max(
                size(idf().personFirstName),
                size(idf().personLastName),
                size(idf().personEmail),
                size(idf().personAddress),
                size(idf().personAffiliation),
                size(idf().personPhone),
                size(idf().personMidInitials),
                size(idf().personFax),
                size(idf().personRoles),
                // Note that to check the existence of at least one contact the code requires
                // that at least one entry is present in that list. If none of the above applies, 1 below
                // will ensure that an empty LimpopoBasedPerson object will be inserted into contacts
                // This in turn will result in the 'non empty list of contacts' test being applied downstream.
                1);
        List<Person> contacts = newArrayList();
        for (int i = 0; i < size; i++) {
            contacts.add(new LimpopoBasedPerson(idfHelper, i));
        }
        return contacts;
    }

    @Override
    public Collection<ExperimentalDesign> getExperimentDesigns() {
        int size = max(
                size(idf().experimentalDesign),
                size(idf().experimentalDesignTermAccession),
                size(idf().experimentalDesignTermSourceREF),
                // Note that to check the existence of at least one design the code requires
                // that at least one entry is present in that list. If none of the above applies, 1 below
                // will ensure that an empty LimpopoBasedExperimentalDesign object will be inserted into designs
                // This in turn will result in the 'non empty list of experimental designs' test being applied downstream.
                1);
        List<ExperimentalDesign> designs = newArrayList();
        for (int i = 0; i < size; i++) {
            designs.add(new LimpopoBasedExperimentalDesign(idfHelper, i));
        }
        return designs;
    }

    @Override
    public Collection<ExperimentalFactor> getExperimentalFactors() {
        int size = max(
                size(idf().experimentalFactorName),
                size(idf().experimentalFactorType),
                size(idf().experimentalFactorTermAccession),
                size(idf().experimentalFactorTermSourceREF),
                // Note that to check the existence of at least one experimental factor the code requires
                // that at least one entry is present in that list. If none of the above applies, 1 below
                // will ensure that an empty LimpopoBasedExperimentalFactor object will be inserted into factors
                // This in turn will result in the 'non empty list of experimental factors' test being applied downstream.
                1);
        List<ExperimentalFactor> factors = newArrayList();
        for (int i = 0; i < size; i++) {
            factors.add(new LimpopoBasedExperimentalFactor(idfHelper, i));
        }
        return factors;
    }

    @Override
    public Collection<QualityControlType> getQualityControlTypes() {
        int size = max(
                size(idf().qualityControlType),
                size(idf().qualityControlTermAccession),
                size(idf().qualityControlTermSourceREF));
        List<QualityControlType> qualityControlTypes = newArrayList();
        for (int i = 0; i < size; i++) {
            qualityControlTypes.add(new LimpopoBasedQualityControlType(idfHelper, i));
        }
        return qualityControlTypes;
    }

    @Override
    public Collection<ReplicateType> getReplicateTypes() {
        int size = max(
                size(idf().replicateType),
                size(idf().replicateTermAccession),
                size(idf().replicateTermSourceREF));
        List<ReplicateType> replicateTypes = newArrayList();
        for (int i = 0; i < size; i++) {
            replicateTypes.add(new LimpopoBasedReplicateType(idfHelper, i));
        }
        return replicateTypes;
    }

    @Override
    public Collection<NormalizationType> getNormalizationTypes() {
        int size = max(
                size(idf().normalizationType),
                size(idf().normalizationTermAccession),
                size(idf().normalizationTermSourceREF));
        List<NormalizationType> normalizationTypes = newArrayList();
        for (int i = 0; i < size; i++) {
            normalizationTypes.add(new LimpopoBasedNormalizationType(idfHelper, i));
        }
        return normalizationTypes;
    }

    @Override
    public Collection<Publication> getPublications() {
        int size = max(
                size(idf().publicationTitle),
                size(idf().publicationAuthorList),
                size(idf().pubMedId),
                size(idf().publicationDOI),
                size(idf().publicationStatus),
                size(idf().publicationStatusTermAccession),
                size(idf().publicationStatusTermSourceREF));
        List<Publication> publications = newArrayList();
        for (int i = 0; i < size; i++) {
            publications.add(new LimpopoBasedPublication(idfHelper, i));
        }
        return publications;
    }

    @Override
    public Collection<Protocol> getProtocols() {
        int size = max(
                size(idf().protocolName),
                size(idf().protocolDescription),
                size(idf().protocolContact),
                size(idf().protocolParameters),
                size(idf().protocolHardware),
                size(idf().protocolSoftware),
                size(idf().protocolType),
                size(idf().protocolTermAccession),
                size(idf().protocolTermSourceREF),
                // Note that to check the existence of at least one protocol the code requires
                // that at least one entry is present in that list. If none of the above applies, 1 below
                // will ensure that an empty LimpopoBasedProtocol object will be inserted into protocols
                // This in turn will result in the 'non empty list of protocols' test being applied downstream.
                1);
        List<Protocol> protocols = newArrayList();
        for (int i = 0; i < size; i++) {
            protocols.add(new LimpopoBasedProtocol(idfHelper, i));
        }
        return protocols;
    }

    @Override
    public Collection<TermSource> getTermSources() {
        return idfHelper.getTermSources();
    }

    @Override
    public Collection<Comment> getComments(String type) {
        Map<String, Set<String>> allComments = idf().getComments();
        Set<String> comments = allComments.containsKey(type) ?
                allComments.get(type) : Collections.<String>emptySet();
        return createComments(comments, type);
    }

    @Override
    public Collection<Comment> getComments() {
        Map<String, Set<String>> allComments = idf().getComments();
        List<Comment> comments = newArrayList();
        for (String type : allComments.keySet()) {
            comments.addAll(createComments(allComments.get(type), type));
        }
        return comments;
    }

    private Collection<Comment> createComments(Set<String> values, String type) {
        List<Comment> comments = newArrayList();
        int i = 0;
        for (String value : values) {
            comments.add(new LimpopoBasedComment(idfHelper, i++, type, value));
        }
        return comments;
    }

    @Override
    public TermSource getTermSource(String ref) {
        return idfHelper.getTermSource(ref);
    }

    @Override
    public Protocol getProtocol(String ref) {
        for (Protocol protocol : getProtocols()) {
            if (ref.equals(protocol.getName().getValue())) {
                return protocol;
            }
        }
        return null;
    }


    private IDF idf() {
        return idfHelper.idf();
    }

    private int size(List<String> list) {
        if (list == null) {
            return 0;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!isNullOrEmpty(list.get(i))) {
                return i + 1;
            }
        }
        return 0;
    }
}
