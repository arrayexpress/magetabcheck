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

import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

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

    public static void main() {
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

    private void generateMarkdown(File out) throws IOException {
        CharStreams.write(markdown(), Files.newOutputStreamSupplier(out));
    }

    private String markdown() {
        StringBuilder sb = new StringBuilder();
        //TODO
        return sb.toString();
    }
}
