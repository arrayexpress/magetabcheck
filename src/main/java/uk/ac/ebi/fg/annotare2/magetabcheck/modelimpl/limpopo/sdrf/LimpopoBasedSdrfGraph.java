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

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.sdrf;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraph;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf.SdrfGraphNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedSdrfGraph implements SdrfGraph {

    private final Set<SdrfHelper> helpers;

    public LimpopoBasedSdrfGraph(Collection<SDRF> sdrfs, IdfData idf) {
        helpers = new HashSet<SdrfHelper>();
        for (SDRF sdrf : sdrfs) {
            helpers.add(new SdrfHelper(sdrf, idf));
        }
    }

    @Override
    public Collection<? extends SdrfGraphNode> getRootNodes() {
        List<SdrfGraphNode> rootNodes = newArrayList();
        for (SdrfHelper helper : helpers) {
            rootNodes.addAll(helper.getRootNodes());
        }
        return rootNodes;
    }
}
