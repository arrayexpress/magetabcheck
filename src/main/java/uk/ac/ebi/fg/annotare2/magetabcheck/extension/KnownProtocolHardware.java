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
            "454 GS",
            "454 GS 20",
            "454 GS FLX",
            "454 GS FLX Titanium",
            "454 GS FLX+",
            "454 GS Junior",
            "AB 310 Genetic Analyzer",
            "AB 3130 Genetic Analyzer",
            "AB 3130xL Genetic Analyzer",
            "AB 3500 Genetic Analyzer",
            "AB 3500xL Genetic Analyzer",
            "AB 3730 Genetic Analyzer",
            "AB 3730xL Genetic Analyzer",
            "AB 5500 Genetic Analyzer",
            "AB 5500xl Genetic Analyzer",
            "AB 5500xl-W Genetic Analysis System",
            "AB SOLiD 3 Plus System",
            "AB SOLiD 4 System",
            "AB SOLiD 4hq System",
            "AB SOLiD PI System",
            "AB SOLiD System 2.0",
            "AB SOLiD System 3.0",
            "BGISEQ-500",
            "Complete Genomics",
            "DNBSEQ",
            "DNBSEQ-T7",
            "DNBSEQ-G400",
            "DNBSEQ-G50",
            "DNBSEQ-G400 FAST",
            "GridION",
            "Helicos HeliScope",
            "HiSeq X Five",
            "HiSeq X Ten",
            "Illumina Genome Analyzer",
            "Illumina Genome Analyzer II",
            "Illumina Genome Analyzer IIx",
            "Illumina HiScanSQ",
            "Illumina HiSeq 1000",
            "Illumina HiSeq 1500",
            "Illumina HiSeq 2000",
            "Illumina HiSeq 2500",
            "Illumina HiSeq 3000",
            "Illumina HiSeq 4000",
            "Illumina MiniSeq",
            "Illumina MiSeq",
            "Illumina NovaSeq 6000",
            "Illumina iSeq 100",
            "Ion Torrent PGM",
            "Ion Torrent Proton",
            "Ion Torrent S5",
            "Ion Torrent S5 XL",
            "MinION",
            "NextSeq 500",
            "NextSeq 550",
            "PacBio RS",
            "PacBio RS II",
            "PromethION",
            "Sequel",
            "Sequel II",
            "unspecified"), new Function<String, String>() {
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
