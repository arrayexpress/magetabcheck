package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.fg.annotare2.magetab.model.idf.Person;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermList;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedPerson extends LimpopoBasedIdfObject implements Person {

    private final int index;

    public LimpopoBasedPerson(LimpopoIdfHelper idf, int index) {
        super(idf);
        this.index = index;
    }

    @Override
    public String getFirstName() {
        return get(idf().personFirstName, index);
    }

    @Override
    public String getLastName() {
        return get(idf().personLastName, index);
    }

    @Override
    public String getMidInitials() {
        return get(idf().personMidInitials, index);
    }

    @Override
    public String getEmail() {
        return get(idf().personEmail, index);
    }

    @Override
    public String getPhone() {
        return get(idf().personPhone, index);
    }

    @Override
    public String getFax() {
        return get(idf().personFax, index);
    }

    @Override
    public String getAddress() {
        return get(idf().personAddress, index);
    }

    @Override
    public String getAffiliation() {
        return get(idf().personAffiliation, index);
    }

    @Override
    public TermList getRoles() {
        String roleNames = get(idf().personRoles, index);
        String roleAccessions = get(idf().personRolesTermAccession, index);
        final String roleTermSource = get(idf().personRolesTermSourceREF, index);

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
            public List<String> getNames() {
                return Collections.unmodifiableList(names);
            }

            @Override
            public List<String> getAccessions() {
                return Collections.unmodifiableList(accessions);
            }

            @Override
            public TermSource getSource() {
                return termSource(roleTermSource);
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
