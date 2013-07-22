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

package uk.ac.ebi.fg.annotare2.magetabcheck.efo;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

import static com.google.common.io.Closeables.closeQuietly;

/**
 * @author Olga Melnichuk
 */
public class EfoLoader {

    private static final Logger log = LoggerFactory.getLogger(EfoLoader.class);

    private File cacheDir;
    private final URL defaultEfoUrl;
    private final int entityExpansionLimit;

    public EfoLoader(EfoServiceProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("EfoServiceProperties == null");
        }

        defaultEfoUrl = properties.getEfoUrl();
        entityExpansionLimit = properties.getOwlEntityExpansionLimit();
        cacheDir = properties.getCacheDir();
        cacheDir = (cacheDir == null) ?
                Files.createTempDir() : cacheDir;
        log.debug("EFO cache dir used {}", cacheDir);
    }

    public EfoDag load() throws IOException, OWLOntologyCreationException {
        return load(defaultEfoUrl);
    }

    public EfoDag load(final URL url) throws IOException, OWLOntologyCreationException {
        if (url == null) {
            log.error("Given EFO url is null; please use the config file to specify the proper one");
            return null;
        }

        File cacheFile = getCacheFile(url);
        if (!cacheFile.exists()) {
            log.debug("The cache file doesn't exist; creating one [file={}]", cacheFile);
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                throw new IOException("Can't create EFO cache directory: " + cacheDir.getAbsolutePath());
            }

            log.debug("Downloading EFO file [url={}]", url);
            OutputSupplier<FileOutputStream> out = Files.newOutputStreamSupplier(cacheFile);
            ByteStreams.copy(new InputSupplier<InputStream>() {
                @Override
                public InputStream getInput() throws IOException {
                    return url.openStream();
                }
            }, out);
            log.debug("EFO file download successfully completed.");
        } else {
            log.debug("Loading EFO graph from cache [file={}]", cacheFile);
        }
        return load(cacheFile);
    }

    private File getCacheFile(URL url) {
        String fileName = url.toString().replaceAll("[^\\p{L}\\p{N}]", "");
        fileName = fileName.replaceAll("http|www", "");
        fileName = fileName.substring(0, Math.min(30, fileName.length()));
        return new File(cacheDir, fileName + ".cache");
    }

    public EfoDag load(File file) throws IOException, OWLOntologyCreationException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return load(in);
        } finally {
            closeQuietly(in);
        }
    }

    public EfoDag load(InputStream in) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLReasoner reasoner = null;

        try {
            // The default entityExpansionLimit=64000 defined in RDFXMLParser
            // is not enough to load EFO
            System.setProperty("entityExpansionLimit", Integer.toString(entityExpansionLimit));

            log.debug("Reading the ontology...");
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(in);
            reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            reasoner.isConsistent();
            return EfoDag.build(ontology, reasoner);
        } finally {
            if (reasoner != null) {
                reasoner.dispose();
            }
        }
    }
}
