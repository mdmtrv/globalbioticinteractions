package org.eol.globi.data;

import org.eol.globi.domain.Study;
import org.eol.globi.domain.StudyNode;
import org.eol.globi.domain.Taxon;
import org.globalbioticinteractions.dataset.Dataset;
import org.globalbioticinteractions.dataset.DatasetImpl;
import org.eol.globi.util.NodeUtil;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class StudyImporterForHurlbertTest extends GraphDBTestCase {

    @Test
    public void importSome() throws StudyImporterException, IOException {
        doImport("some/namespace");

        List<StudyNode> allStudies = NodeUtil.findAllStudies(getGraphDb());
        assertThat(allStudies.size(), is(2));

        Study study = allStudies.get(0);
        assertThat(study.getSource(), startsWith("Allen Hurlbert. Avian Diet Database (https://github.com/hurlbertlab/dietdatabase/). Accessed at <AvianDietDatabase.txt>"));
        assertThat(study.getCitation(), is("Brown, B. T., W. C. Leibfried, T. R. Huels, and J. A. Olivera. 1991. Prey remains from Bald Eagle nests in Sonora, Mexico. Southwestern Naturalist 36:259-262."));

        study = allStudies.get(1);
        assertThat(study.getSource(), startsWith("Allen Hurlbert. Avian Diet Database (https://github.com/hurlbertlab/dietdatabase/). Accessed at <AvianDietDatabase.txt>"));
        assertThat(study.getCitation(), containsString("Cash, K. J., J. P. Austin-Smith"));

        assertThat(taxonIndex.findTaxonByName("Haliaeetus leucocephalus"), is(notNullValue()));
        Taxon preyTaxon = taxonIndex.findTaxonByName("Ictalurus");
        assertThat(preyTaxon, is(notNullValue()));
        assertThat(preyTaxon.getName(), is("Ictalurus"));
        assertThat(preyTaxon.getExternalId(), is("ITIS:163996"));

        preyTaxon = taxonIndex.findTaxonByName("Cyprinus carpio");
        assertThat(preyTaxon, is(notNullValue()));
        assertThat(preyTaxon.getName(), is("Cyprinus carpio"));
        assertThat(preyTaxon.getExternalId(), not(is("ITIS:unverified")));
    }

    public static InputStream getResource() {
        return StudyImporterForHurlbertTest.class.getResourceAsStream("hurlbert/avianDietFirst50.txt");
    }

    @Test
    public void importTwice() throws StudyImporterException {
        doImport("some/namespace");
        doImport("some/namespace2");
    }

    public StudyImporter doImport(final String namespace) throws StudyImporterException {
        StudyImporter importer = new StudyImporterForHurlbert(null, nodeFactory);
        Dataset dataset = new DatasetImpl(namespace, URI.create("some:uri"), inStream -> inStream) {
            @Override
            public InputStream retrieve(URI name){
                return StudyImporterForHurlbertTest.getResource();
            }

        };
        importer.setDataset(dataset);
        importStudy(importer);
        return importer;
    }

    @Test
    public void parseDateStringChopDecimal() {
        String dateString = StudyImporterForHurlbert.getDateString("2018", "1.75");
        assertThat(dateString, is("2018-1"));
    }

    @Test
    public void parseDateStringPropagateUnexpected() {
        String dateString = StudyImporterForHurlbert.getDateString("2018", "1.FOO");
        assertThat(dateString, is("2018-1.FOO"));
    }

    @Test
    public void parseDateString() {
        String dateString = StudyImporterForHurlbert.getDateString("2018", "1");
        assertThat(dateString, is("2018-1"));
    }

}
