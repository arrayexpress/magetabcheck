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

package uk.ac.ebi.fg.annotare2.magetabcheck.model.sdrf;


import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public interface SdrfProtocolNode extends SdrfGraphNode, HasTermSource, HasComments {

    String getDate();

    String getProtocolRef();

    Protocol getProtocol ();

    SdrfPerformerAttribute getPerformer();

    Collection<SdrfParameterValueAttribute> getParameters();
}
