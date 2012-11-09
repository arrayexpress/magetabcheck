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

import uk.ac.ebi.fg.annotare2.magetab.model.Cell;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetab.model.idf.TermSource;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.magetab.modelimpl.limpopo.IdfTags.*;

/**
 * @author Olga Melnichuk
 */
public class LimpopoBasedProtocol extends LimpopoBasedIdfObject implements Protocol {

    public LimpopoBasedProtocol(@Nonnull IdfHelper helper, int index) {
        super(helper, index);
    }

    @Override
    public Cell<String> getName() {
        return new Cell<String>(
                get(idf().protocolName),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_NAME),
                getColumn());
    }

    @Override
    public Cell<String> getDescription() {
        return new Cell<String>(
                get(idf().protocolDescription),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_DESCRIPTION),
                getColumn());
    }

    @Override
    public Cell<List<String>> getParameters() {
        String paramString = get(idf().protocolParameters);
        paramString = paramString == null ? "" : paramString;

        return new Cell<List<String>>(
                asList(paramString.split(",")),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_PARAMETERS),
                getColumn());
    }

    @Override
    public Cell<String> getHardware() {
        return new Cell<String>(
                get(idf().protocolHardware),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_HARDWARE),
                getColumn());
    }

    @Override
    public Cell<String> getSoftware() {
        return new Cell<String>(
                get(idf().protocolSoftware),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_SOFTWARE),
                getColumn());
    }

    @Override
    public Cell<String> getContact() {
        return new Cell<String>(
                get(idf().protocolContact),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_CONTACT),
                getColumn());
    }

    @Override
    public ProtocolType getType() {
        return new ProtocolType() {
            @Override
            public Cell<String> getName() {
                return new Cell<String>(
                        get(idf().protocolType),
                        idf().getLayout().getLineNumberForHeader(PROTOCOL_TYPE),
                        getColumn());
            }

            @Override
            public Cell<String> getAccession() {
                return new Cell<String>(
                        get(idf().protocolTermAccession),
                        idf().getLayout().getLineNumberForHeader(PROTOCOL_TERM_ACCESSION_NUMBER),
                        getColumn());
            }

            @Override
            public Cell<TermSource> getSource() {
                return new Cell<TermSource>(
                        termSource(get(idf().protocolTermSourceREF)),
                        idf().getLayout().getLineNumberForHeader(PROTOCOL_TERM_SOURCE_REF),
                        getColumn());
            }
        };
    }
}
