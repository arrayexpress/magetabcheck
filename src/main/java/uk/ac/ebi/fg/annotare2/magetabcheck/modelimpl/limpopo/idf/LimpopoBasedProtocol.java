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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf;

import uk.ac.ebi.fg.annotare2.magetabcheck.model.Cell;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.Protocol;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.ProtocolType;
import uk.ac.ebi.fg.annotare2.magetabcheck.model.idf.TermSource;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.idf.IdfTags.*;

/**
 * @author Olga Melnichuk
 */
class LimpopoBasedProtocol extends LimpopoBasedIdfObject implements Protocol {

    public LimpopoBasedProtocol(@Nonnull IdfHelper helper, int index) {
        super(helper, index);
    }

    @Override
    public Cell<String> getName() {
        return createCell(
                get(idf().protocolName),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_NAME));
    }

    @Override
    public Cell<String> getDescription() {
        return createCell(
                get(idf().protocolDescription),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_DESCRIPTION));
    }

    @Override
    public Cell<List<String>> getParameters() {
        String paramString = get(idf().protocolParameters);
        paramString = paramString == null ? "" : paramString;

        return createCell(
                asList(paramString.split(",")),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_PARAMETERS));
    }

    @Override
    public Cell<String> getHardware() {
        return createCell(
                get(idf().protocolHardware),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_HARDWARE));
    }

    @Override
    public Cell<String> getSoftware() {
        return createCell(
                get(idf().protocolSoftware),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_SOFTWARE));
    }

    @Override
    public Cell<String> getContact() {
        return createCell(
                get(idf().protocolContact),
                idf().getLayout().getLineNumberForHeader(PROTOCOL_CONTACT));
    }

    @Override
    public ProtocolType getType() {
        return new ProtocolType() {
            @Override
            public Cell<String> getName() {
                return createCell(
                        get(idf().protocolType),
                        idf().getLayout().getLineNumberForHeader(PROTOCOL_TYPE));
            }

            @Override
            public Cell<String> getAccession() {
                return createCell(
                        get(idf().protocolTermAccession),
                        idf().getLayout().getLineNumberForHeader(PROTOCOL_TERM_ACCESSION_NUMBER));
            }

            @Override
            public Cell<TermSource> getSource() {
                return createCell(
                        termSource(get(idf().protocolTermSourceREF)),
                        idf().getLayout().getLineNumberForHeader(PROTOCOL_TERM_SOURCE_REF));
            }
        };
    }
}
