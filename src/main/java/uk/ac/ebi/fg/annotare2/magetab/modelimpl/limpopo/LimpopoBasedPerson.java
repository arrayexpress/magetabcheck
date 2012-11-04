/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.fg.annotare2.magetab.model.Cell;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.IdfTags.*;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedPerson extends LimpopoBasedIdfObject implements Person {

    public LimpopoBasedPerson(LimpopoIdfHelper idf, int index) {
        super(idf, index);
    }

    @Override
    public Cell<String> getFirstName() {
        return new Cell<String>(
                get(idf().personFirstName),
                idf().getLayout().getLineNumberForHeader(PERSON_FIRST_NAME),
                getColumn());
    }

    @Override
    public Cell<String> getLastName() {
        return new Cell<String>(
                get(idf().personLastName),
                idf().getLayout().getLineNumberForHeader(PERSON_LAST_NAME),
                getColumn());
    }

    @Override
    public Cell<String> getMidInitials() {
        return new Cell<String>(
                get(idf().personMidInitials),
                idf().getLayout().getLineNumberForHeader(PERSON_MID_INITIALS),
                getColumn());
    }

    @Override
    public Cell<String> getEmail() {
        return new Cell<String>(
                get(idf().personEmail),
                idf().getLayout().getLineNumberForHeader(PERSON_EMAIL),
                getColumn());
    }

    @Override
    public Cell<String> getPhone() {
        return new Cell<String>(
                get(idf().personPhone),
                idf().getLayout().getLineNumberForHeader(PERSON_PHONE),
                getColumn());
    }

    @Override
    public Cell<String> getFax() {
        return new Cell<String>(
                get(idf().personFax),
                idf().getLayout().getLineNumberForHeader(PERSON_FAX),
                getColumn());
    }

    @Override
    public Cell<String> getAddress() {
        return new Cell<String>(
                get(idf().personAddress),
                idf().getLayout().getLineNumberForHeader(PERSON_ADDRESS),
                getColumn());
    }

    @Override
    public Cell<String> getAffiliation() {
        return new Cell<String>(
                get(idf().personAffiliation),
                idf().getLayout().getLineNumberForHeader(PERSON_AFFILIATION),
                getColumn());
    }

    @Override
    public TermList getRoles() {
        String roleNames = get(idf().personRoles);
        String roleAccessions = get(idf().personRolesTermAccession);
        final String roleTermSource = get(idf().personRolesTermSourceREF);

        final List<String> names = newArrayList();
        if (!isNullOrEmpty(roleNames)) {
            names.addAll(asList(roleNames.split(",")));
        }

        final List<String> accessions = newArrayList();
        if (!isNullOrEmpty(roleAccessions)) {
            accessions.addAll(asList(roleAccessions.split(",")));
        }

        final int size = toTheSameSize(names, accessions);

        return new TermList() {
            @Override
            public Cell<List<String>> getNames() {
                return new Cell<List<String>>(
                        Collections.unmodifiableList(names),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PERSON_ROLES),
                        getColumn());
            }

            @Override
            public Cell<List<String>> getAccessions() {
                return new Cell<List<String>>(
                        Collections.unmodifiableList(accessions),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PERSON_ROLES_TERM_ACCESSION_NUMBER),
                        getColumn());
            }

            @Override
            public Cell<TermSource> getSource() {
                return new Cell<TermSource>(
                        termSource(roleTermSource),
                        idf().getLayout().getLineNumberForHeader(IdfTags.PERSON_ROLES_TERM_SOURCE_REF),
                        getColumn());
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isEmpty() {
                return size == 0;
            }
        };
    }

    @SuppressWarnings(value = "unchecked")
    protected int toTheSameSize(List<String> list1, List<String> list2) {
        int size = Math.max(list1.size(), list2.size());

        List<List<String>> lists = asList(list1, list2);

        for (int i = 0; i < size; i++) {
            for (List<String> list : lists) {
                if (i <= list.size()) {
                    list.add("");
                }
            }
        }
        return size;
    }
}
