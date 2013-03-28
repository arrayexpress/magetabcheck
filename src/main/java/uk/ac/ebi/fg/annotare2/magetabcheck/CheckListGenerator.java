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

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckModality;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;
import uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownProtocolHardware;
import uk.ac.ebi.fg.annotare2.magetabcheck.extension.KnownTermSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.CharStreams.newWriterSupplier;
import static com.google.common.io.CharStreams.write;
import static com.google.common.io.Files.newOutputStreamSupplier;
import static com.google.inject.internal.util.$Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
public class CheckListGenerator {

    private static final Pattern REF_PATTERN = Pattern.compile("([a-zA-Z]+)[0-9]*");

    private static final Map<String, String> prefixes = newHashMap();

    static {
        prefixes.put("G", "General");
        prefixes.put("TS", "Term Source");
        prefixes.put("PR", "Protocol");
        prefixes.put("PB", "Publication");
        prefixes.put("C", "Contact");
        prefixes.put("EF", "Experimental Factor");
        prefixes.put("ED", "Experimental Design");
        prefixes.put("QC", "Quality Control Type");
        prefixes.put("NT", "Normalization Type");
        prefixes.put("RT", "Replicate Type");

        prefixes.put("SR", "Source Node");
        prefixes.put("SM", "Sample Node");
        prefixes.put("EX", "Extract Node");
        prefixes.put("LE", "Labeled Extract Node");
        prefixes.put("MT", "Material Type Attribute");
        prefixes.put("CA", "Characteristic Attribute");
        prefixes.put("UA", "Unit Attribute");
        prefixes.put("L", "Label Attribute");
        prefixes.put("PN", "Protocol Node");
        prefixes.put("PV", "Parameter Value Attribute");
        prefixes.put("FV", "Factor Value Attribute");
        prefixes.put("AN", "Assay Node");
        prefixes.put("TT", "Technology type");
        prefixes.put("SC", "Scan Node");
        prefixes.put("NN", "Normalization Node");
        prefixes.put("AD", "Array Design Attribute");
        prefixes.put("ADN", "Array Data Node");
        prefixes.put("DADN", "Derived Array Data Node");
        prefixes.put("ADMN", "Array Data Matrix Node");
        prefixes.put("DADMN", "Derived Array Data Matrix Node");
    }

    private static final Map<String, RefList> refs = newHashMap();

    static {
        refs.put("#protocol-hardware-list", new ProtocolHardwareList("Supported protocol hardware list"));
        refs.put("#term-source-list", new TermSourceList("Term source list"));
    }

    private final String packageName;

    private final String title;

    public CheckListGenerator(String packageName, String title) {
        this.packageName = packageName;
        this.title = title;
    }

    public static void main(String... args) {
        toMarkdown("uk.ac.ebi.fg.annotare2.magetabcheck.checks.idf", "IDF checks", "./doc/idf-checks.md");
        toMarkdown("uk.ac.ebi.fg.annotare2.magetabcheck.checks.sdrf", "SDRF checks", "./doc/sdrf-checks.md");
    }

    private static void toMarkdown(String packageName, String title, String fileOut) {
        try {
            (new CheckListGenerator(packageName, title)).generateMarkdown(new File(fileOut));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateMarkdown(final File out) throws IOException {
        if (!out.exists()) {
            File parent = out.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Can't create directories " + parent);
            }
        }
        write(markdown(), newWriterSupplier(newOutputStreamSupplier(out), Charsets.UTF_8));
    }

    private String markdown() {
        Reflections reflections = new Reflections(packageName,
                new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

        List<MageTabCheck> checks = new ArrayList<MageTabCheck>();
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(MageTabCheck.class)) {
            checks.add(clazz.getAnnotation(MageTabCheck.class));
        }
        for (Method method : reflections.getMethodsAnnotatedWith(MageTabCheck.class)) {
            checks.add(method.getAnnotation(MageTabCheck.class));
        }

        checks = Ordering.from(new Comparator<MageTabCheck>() {
            @Override
            public int compare(MageTabCheck o1, MageTabCheck o2) {
                int prefixDiff = prefix(o1.ref()).compareTo(prefix(o2.ref()));
                if (prefixDiff != 0) {
                    return prefixDiff;
                }
                int modalityDiff = o1.modality().compareTo(o2.modality());
                return modalityDiff == 0 ? o1.ref().compareTo(o2.ref()) : modalityDiff;
            }

            private String prefix(String str) {
                Matcher m = REF_PATTERN.matcher(str);
                return (m.matches()) ? m.group(1) : str;
            }
        }).sortedCopy(checks);

        MarkdownChecks markdown = new MarkdownChecks(refs);
        markdown.header1(title + " (" + checks.size() + ")");
        String prev = null;
        for (MageTabCheck check : checks) {
            Matcher m = REF_PATTERN.matcher(check.ref());
            String prefix;
            if (m.matches()) {
                prefix = m.group(1);
            } else {
                throw new IllegalStateException("Wrong REF format: " + check.ref());
            }
            if (prev == null || !prev.equals(prefix)) {
                if (prefixes.containsKey(prefix)) {
                    markdown.header2(prefixes.get(prefix) + " Checks");
                }
                markdown.checksStart();
                prev = prefix;
            }
            markdown.addCheck(check);
        }
        return markdown.toString();
    }

    private static class MarkdownChecks {
        private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        private final StringBuilder title = new StringBuilder();
        private final StringBuilder text = new StringBuilder();
        private final StringBuilder toc = new StringBuilder();
        private final Set<RefList> refLists = newHashSet();

        private final Map<String, RefList> refs;

        private MarkdownChecks(Map<String, RefList> refs) {
            this.refs = refs;
        }

        public void header1(String header) {
            title.append("# ")
                    .append(header)
                    .append("\n(updated: ")
                    .append(dateFormat.format(new Date()))
                    .append(")\n\n");
        }

        public void header2(String header) {
            text.append("\n## ")
                    .append(header)
                    .append("\n");
            toc.append("+ [").append(header).append("](").append(ref(header)).append(")\n");
        }

        public void checksStart() {
            text.append("\n");
            for (Column c : Column.values()) {
                text.append("|");
                text.append(c.getTitle());
            }
            text.append("|\n");
            for (Column c : Column.values()) {
                text.append("|");
                text.append(Strings.repeat("-", c.getTitle().length()));
            }
            text.append("|\n");
        }

        public void addCheck(MageTabCheck check) {
            for (Column c : Column.values()) {
                text.append("|");
                text.append(filter(c.getValue(check)));

            }
            text.append("|\n");
        }

        private void addRefList(RefList refList) {
            header2(refList.getTitle());
            for (String item : refList.getItems()) {
                text.append("* ").append(item).append("\n");
            }
        }

        private String filter(String v) {
            for (String key : refs.keySet()) {
                if (v.contains(key)) {
                    RefList refList = refs.get(key);
                    v = v.replaceAll(key, ref(refList.getTitle()));
                    refLists.add(refList);
                }
            }
            return v;
        }

        private String ref(String title) {
            return "#" + title.toLowerCase().replaceAll("\\s+", " ").replaceAll("\\s", "-");
        }

        @Override
        public String toString() {
            for (RefList list : refLists) {
                addRefList(list);
            }
            return title.toString() + toc.toString() + text.toString();
        }
    }

    private static enum Column {
        REF("Ref") {
            @Override
            String getValue(MageTabCheck annot) {
                return annot.ref();
            }
        },
        MODALITY("Modality") {
            @Override
            String getValue(MageTabCheck annot) {
                CheckModality m = annot.modality();
                String str = m.toString().toLowerCase();
                return m.isError() ? "**" + str + "**" : str;
            }
        },
        TYPE("Type") {
            @Override
            String getValue(MageTabCheck annot) {
                switch (annot.application()) {
                    case MICRO_ARRAY_ONLY:
                        return "Micro-array";
                    case ANY:
                        return "Both";
                    case HTS_ONLY:
                        return "HTS";
                    default:
                        return annot.application().toString();
                }
            }
        },
        TITLE("Title") {
            @Override
            String getValue(MageTabCheck annot) {
                return annot.value();
            }
        },
        DETAILS("Details") {
            @Override
            String getValue(MageTabCheck annot) {
                return annot.details();
            }
        };

        private final String title;

        private Column(String title) {
            this.title = title;
        }

        private String getTitle() {
            return title;
        }

        abstract String getValue(MageTabCheck annot);
    }

    private static class RefList {
        private final String title;
        private final List<String> items = newArrayList();

        private RefList(String title) {
            this.title = title;
        }

        private List<String> getItems() {
            return copyOf(items);
        }

        private String getTitle() {
            return title;
        }

        public void addAll(Collection<String> items) {
            for (String item : items) {
                add(item);
            }
        }

        public void add(String item) {
            items.add(item);
        }
    }

    private static class ProtocolHardwareList extends RefList {
        private ProtocolHardwareList(String title) {
            super(title);
            addAll(KnownProtocolHardware.LIST);
        }
    }

    private static class TermSourceList extends RefList {
        private TermSourceList(String title) {
            super(title);
            for (KnownTermSource ts : KnownTermSource.values()) {
                add(ts.getName() + " " + ts.getUrl());
            }
        }
    }
}
