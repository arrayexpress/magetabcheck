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
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.annotation.MageTabCheck;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Set;

import static com.google.common.io.CharStreams.newWriterSupplier;
import static com.google.common.io.CharStreams.write;
import static com.google.common.io.Files.newOutputStreamSupplier;

/**
 * @author Olga Melnichuk
 */
public class CheckListGenerator {

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

        Set<Class<?>> classBasedChecks = reflections.getTypesAnnotatedWith(MageTabCheck.class);
        Set<Method> methodBasedChecks = reflections.getMethodsAnnotatedWith(MageTabCheck.class);

        MarkdownChecks markdown = new MarkdownChecks();
        markdown.header1(title);
        markdown.checksStart();
        for (Method method : methodBasedChecks) {
            markdown.addCheck(method);
        }
        for (Class<?> clazz : classBasedChecks) {
            markdown.addCheck(clazz);
        }
        return markdown.toString();
    }

    private static class MarkdownChecks {
        private final StringBuilder sb = new StringBuilder();

        public void header1(String title) {
            sb.append("## ")
                    .append(title)
                    .append("\n");
        }

        public void checksStart() {
            for (Column c : Column.values()) {
                sb.append("|");
                sb.append(c.getTitle());

            }
            sb.append("|\n");

            for (Column c : Column.values()) {
                sb.append("|");
                sb.append(Strings.repeat("-", c.getTitle().length()));
            }
            sb.append("|\n");
        }

        public void addCheck(Method method) {
            for (Column c : Column.values()) {
                sb.append("|");
                sb.append(c.getValue(method));

            }
            sb.append("|\n");
        }

        public void addCheck(Class<?> clazz) {
            for (Column c : Column.values()) {
                sb.append("|");
                sb.append(c.getValue(clazz));

            }
            sb.append("|\n");
        }

        @Override
        public String toString() {
            return sb.toString();
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
                return annot.modality().toString();
            }
        },
        TYPE("Type") {
            @Override
            String getValue(MageTabCheck annot) {
                return annot.application().toString();
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
                return "TBA";
            }
        };

        private final String title;

        private Column(String title) {
            this.title = title;
        }

        private String getTitle() {
            return title;
        }

        public String getValue(Method method) {
            return getValue(method.getAnnotation(MageTabCheck.class));
        }

        public String getValue(Class<?> clazz) {
            return getValue(clazz.getAnnotation(MageTabCheck.class));
        }

        abstract String getValue(MageTabCheck annot);
    }
}
