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

package uk.ac.ebi.fg.annotare2.magetabcheck.extension;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
public class KnownProtocolHardware {

    //TODO put it into property file
    public static final List<String> LIST = unmodifiableList(transform(newArrayList(

            "Illumina Genome Analyzer",
            "Illumina Genome Analyzer II",
            "Illumina Genome Analyzer IIx",
            "Illumina HiSeq 2500",
            "Illumina HiSeq 2000",
            "Illumina HiSeq 1000",
            "Illumina MiSeq",
            "Illumina HiScanSQ",
            "Helicos HeliScope",
            "AB SOLiD System",
            "AB SOLiD System 2.0",
            "AB SOLiD System 3.0",
            "AB SOLiD 3 Plus System",
            "AB SOLiD 4 System",
            "AB SOLiD 4hq System",
            "AB SOLiD PI System",
            "AB 5500 Genetic Analyzer",
            "AB 5500xl Genetic Analyzer",
            "Complete Genomics",
            "PacBio RS",
            "Ion Torrent PGM",
            "Ion Torrent Proton",
            "AB 3730xL Genetic Analyzer",
            "AB 3730 Genetic Analyzer",
            "AB 3500xL Genetic Analyzer",
            "AB 3500 Genetic Analyzer",
            "AB 3130xL Genetic Analyzer",
            "AB 3130 Genetic Analyzer",
            "AB 310 Genetic Analyzer",
            "MinION",
            "GridION",
            "HiSeq X Ten",
            "NextSeq 500",
            "Illumina HiSeq 1500",
            "unspecified" ), new Function<String, String>() {
        @Override
        public String apply(@Nullable String input) {
            return input.toLowerCase();
        }
    }));

    public static boolean isValidProtocolHardware(String... hardware) {
        for (String h : hardware) {
            if (!LIST.contains(h.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
