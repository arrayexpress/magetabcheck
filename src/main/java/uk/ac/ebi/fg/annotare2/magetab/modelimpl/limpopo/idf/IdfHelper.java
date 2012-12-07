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

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.idf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;
import uk.ac.ebi.fg.annotare2.magetab.utils.Urls;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
class IdfHelper {

    private final IDF idf;

    private final Map<String, TermSource> termSources = new HashMap<String, TermSource>();

    private final String fileName;

    public IdfHelper(@Nonnull IDF idf) {
        this.idf = idf;
        this.fileName = Urls.getFileName(idf.getLocation());

        int size = this.idf.termSourceName.size();
        for (int i = 0; i < size; i++) {
            TermSource ts = new LimpopoBasedTermSource(this, i);
            termSources.put(ts.getName().getValue(), ts);
        }
    }

    IDF idf() {
        return idf;
    }

    TermSource getTermSource(String name) {
        return termSources.get(name);
    }

    List<TermSource> getTermSources() {
        List<TermSource> list = new ArrayList<TermSource>();
        list.addAll(termSources.values());
        return list;
    }

    public String getFileName() {
        return fileName;
    }
}
