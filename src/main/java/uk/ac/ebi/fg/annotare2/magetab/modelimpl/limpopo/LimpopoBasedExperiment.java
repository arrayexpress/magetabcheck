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

package uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.fg.annotare2.magetab.model.Experiment;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.IdfData;
import uk.ac.ebi.fg.annotare2.magetab.model.sdrf.SdrfGraph;
import uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.idf.LimpopoIdfDataProxy;
import uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.sdrf.LimpopoBasedSdrfGraph;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedExperiment implements Experiment {

    private final IdfData idfData;

    private final SdrfGraph sdrfGraph;

    public LimpopoBasedExperiment(MAGETABInvestigation inv) {
        this.idfData = new LimpopoIdfDataProxy(inv.IDF);
        this.sdrfGraph = new LimpopoBasedSdrfGraph(inv.SDRF, idfData);
    }

    @Override
    public IdfData getIdfData() {
        return idfData;
    }

    @Override
    public SdrfGraph getSdfGraph() {
        return sdrfGraph;
    }
}
