package org.eol.globi.service;

import org.codehaus.jackson.JsonNode;
import org.eol.globi.data.BaseStudyImporter;
import org.eol.globi.data.StudyImporter;
import org.eol.globi.data.StudyImporterException;
import org.eol.globi.data.StudyImporterForRSS;
import org.eol.globi.data.StudyImporterForCoetzer;
import org.eol.globi.data.StudyImporterForGoMexSI2;
import org.eol.globi.data.StudyImporterForHafner;
import org.eol.globi.data.StudyImporterForHurlbert;
import org.eol.globi.data.StudyImporterForJSONLD;
import org.eol.globi.data.StudyImporterForMetaTable;
import org.eol.globi.data.StudyImporterForPlanque;
import org.eol.globi.data.StudyImporterForSzoboszlai;
import org.eol.globi.data.StudyImporterForTSV;
import org.eol.globi.data.StudyImporterForWood;
import org.globalbioticinteractions.cache.CacheUtil;
import org.globalbioticinteractions.dataset.Dataset;
import org.globalbioticinteractions.dataset.DatasetConstant;
import org.globalbioticinteractions.dataset.DatasetFactory;
import org.globalbioticinteractions.dataset.DatasetRegistryException;
import org.globalbioticinteractions.dataset.DatasetRegistry;
import org.globalbioticinteractions.dataset.DatasetRegistryGitHubArchive;
import org.globalbioticinteractions.dataset.DatasetRegistryGitHubRemote;
import org.globalbioticinteractions.dataset.DatasetRegistryWithCache;
import org.globalbioticinteractions.dataset.DatasetRegistryZenodo;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class StudyImporterFactoryImplIT {

    @Test
    public void createGoMexSI() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "gomexsi/interaction-data");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForGoMexSI2.class)));
        StudyImporterForGoMexSI2 gomexsiImporter = (StudyImporterForGoMexSI2) importer;
        assertThat(gomexsiImporter.getSourceCitation(), is("http://gomexsi.tamucc.edu"));
    }

    @Test
    public void createHafner() throws StudyImporterException, DatasetRegistryException, IOException {
        final DatasetRegistry datasetRegistryGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        Dataset dataset = new DatasetFactory(datasetRegistryGitHubRemote).datasetFor("globalbioticinteractions/hafner");
        StudyImporter importer = new StudyImporterFactoryImpl(null).createImporter(dataset);
        assertThat(importer, is(notNullValue()));
        StudyImporterForHafner haftnerImporter = (StudyImporterForHafner) importer;
        assertThat(haftnerImporter.getDataset().retrieve(URI.create("hafner/gopher_lice_int.csv")), is(notNullValue()));
    }

    @Test
    public void createSzoboszlai() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "globalbioticinteractions/szoboszlai2015");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForSzoboszlai.class)));
        StudyImporterForSzoboszlai importerz = (StudyImporterForSzoboszlai) importer;
        assertThat(importerz.getSourceCitation(), containsString("Szoboszlai"));
    }

    @Test
    public void createWood() throws StudyImporterException, DatasetRegistryException, IOException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "globalbioticinteractions/wood2015");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForWood.class)));
        StudyImporterForWood importerz = (StudyImporterForWood) importer;
        assertThat(importerz.getSourceCitation(), containsString("Wood"));
        assertThat(importerz.getDataset().retrieve(URI.create("links")).toString(), is(notNullValue()));
    }

    @Test
    public void createPlanque() throws StudyImporterException, DatasetRegistryException, IOException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "globalbioticinteractions/planque2014");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForPlanque.class)));
        StudyImporterForPlanque importerz = (StudyImporterForPlanque) importer;
        assertThat(importerz.getSourceCitation(), containsString("Planque"));
    }

    @Test
    public void createArthopodEasyCapture() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "globalbioticinteractions/arthropodEasyCaptureAMNH");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForRSS.class)));
        assertThat(((StudyImporterForRSS) importer).getRssFeedUrlString(), is(notNullValue()));
    }

    @Test
    public void createMetaTable() throws DatasetRegistryException, StudyImporterException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "globalbioticinteractions/AfricaTreeDatabase");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForMetaTable.class)));
        assertThat(((StudyImporterForMetaTable) importer).getConfig(), is(notNullValue()));
        assertThat(((StudyImporterForMetaTable) importer).getBaseUrl(), startsWith("https://raw.githubusercontent.com/globalbioticinteractions/AfricaTreeDatabase/"));
    }

    @Test
    public void createAfrotropicalBees() throws StudyImporterException, DatasetRegistryException, IOException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        String repo = "globalbioticinteractions/Catalogue-of-Afrotropical-Bees";
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, repo);
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForCoetzer.class)));
        assertThat(((StudyImporterForCoetzer) importer).getDataset(), is(notNullValue()));
        assertThat(((StudyImporterForCoetzer) importer).getDataset().retrieve(URI.create("archive")), is(notNullValue()));

    }

    public StudyImporter importerFor(DatasetRegistryGitHubRemote datasetFinderGitHubRemote, String repo) throws StudyImporterException, DatasetRegistryException {
        Dataset dataset = new DatasetFactory(datasetFinderGitHubRemote).datasetFor(repo);
        return new StudyImporterFactoryImpl(null).createImporter(dataset);
    }

    @Test
    public void defaultTSVImporterCached() throws StudyImporterException, DatasetRegistryException, IOException {
        final DatasetRegistry datasetRegistry = new DatasetRegistryWithCache(new DatasetRegistryGitHubArchive(inStream -> inStream), dataset -> CacheUtil.cacheFor(dataset.getNamespace(), "target/datasets", inStream -> inStream));
        StudyImporter importer = getTemplateImporter(datasetRegistry, "globalbioticinteractions/template-dataset");
        StudyImporterForTSV importerTSV = (StudyImporterForTSV) importer;
        assertThat(importerTSV.getBaseUrl(), startsWith("https://github.com/globalbioticinteractions/template-dataset/"));
        assertThat(importerTSV.getDataset().retrieve(URI.create("globi.json")), is(notNullValue()));

    }

    @Test
    public void jsonldImporterCached() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistry datasetRegistry = new DatasetRegistryWithCache(new DatasetRegistryGitHubArchive(inStream -> inStream), dataset -> CacheUtil.cacheFor(dataset.getNamespace(), "target/datasets", inStream -> inStream));
        Dataset dataset = new DatasetFactory(datasetRegistry).datasetFor("globalbioticinteractions/jsonld-template-dataset");
        StudyImporter importer = new StudyImporterFactoryImpl(null).createImporter(dataset);
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForJSONLD.class)));
    }

    @Test
    public void defaultTSVImporterCachedZenodo() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistry datasetRegistry = new DatasetRegistryWithCache(new DatasetRegistryZenodo(inStream -> inStream), dataset -> CacheUtil.cacheFor(dataset.getNamespace(), "target/datasets", inStream -> inStream));
        StudyImporter importer = getTemplateImporter(datasetRegistry, "globalbioticinteractions/template-dataset");
        StudyImporterForTSV importerTSV = (StudyImporterForTSV) importer;
        assertThat(importerTSV.getSourceCitation(), containsString("doi.org"));
    }

    @Test
    public void defaultTSVImporterNotCached() throws StudyImporterException, DatasetRegistryException, IOException {
        final DatasetRegistry datasetRegistry = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = getTemplateImporter(datasetRegistry, "globalbioticinteractions/template-dataset");
        assertThat(((StudyImporterForTSV) importer).getBaseUrl(), startsWith("https://raw.githubusercontent.com/globalbioticinteractions/template-dataset/"));
        InputStream actual = ((StudyImporterForTSV) importer).getDataset().retrieve(URI.create("globi.json"));
        assertThat(actual, is(notNullValue()));
    }

    StudyImporter getTemplateImporter(DatasetRegistry datasetRegistry, String repo) throws DatasetRegistryException, StudyImporterException {
        Dataset dataset = new DatasetFactory(datasetRegistry).datasetFor(repo);
        StudyImporter importer = new StudyImporterFactoryImpl(null).createImporter(dataset);
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForTSV.class)));
        return importer;
    }

    @Test
    public void createMetaTableREEM() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "globalbioticinteractions/noaa-reem");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForMetaTable.class)));
        final JsonNode config = ((StudyImporterForMetaTable) importer).getConfig();
        assertThat(config, is(notNullValue()));
    }

    @Test
    public void createHurlbert() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "hurlbertlab/dietdatabase");
        assertThat(importer, is(notNullValue()));
        assertThat(importer, is(instanceOf(StudyImporterForHurlbert.class)));
        final JsonNode config = ((BaseStudyImporter) importer).getDataset().getConfig();
        assertThat(config, is(notNullValue()));
    }

    @Test
    public void createSIAD() throws StudyImporterException, DatasetRegistryException {
        final DatasetRegistryGitHubRemote datasetFinderGitHubRemote = new DatasetRegistryGitHubRemote(inStream -> inStream);
        StudyImporter importer = importerFor(datasetFinderGitHubRemote, "globalbioticinteractions/siad");
        assertThat(importer, is(notNullValue()));
        Dataset dataset = ((BaseStudyImporter) importer).getDataset();
        final JsonNode config = dataset.getConfig();
        assertThat(config, is(notNullValue()));
        assertThat(dataset.getOrDefault(DatasetConstant.SHOULD_RESOLVE_REFERENCES, "donald"), is("false"));
    }

}