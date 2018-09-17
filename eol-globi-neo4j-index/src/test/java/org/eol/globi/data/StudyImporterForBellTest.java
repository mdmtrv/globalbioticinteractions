package org.eol.globi.data;

import org.eol.globi.domain.Study;
import org.eol.globi.util.NodeUtil;
import org.joda.time.DateTime;
import org.junit.Test;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class StudyImporterForBellTest extends GraphDBTestCase {

    @Test
    public void importAll() throws StudyImporterException {
        StudyImporter importer = new StudyImporterForBell(new ParserFactoryLocal(), nodeFactory);
        importStudy(importer);
        List<Study> allStudies = NodeUtil.findAllStudies(getGraphDb());
        assertThat(allStudies.size(), is(6));
        for (Study study : allStudies) {
            assertThat(study.getSource(), is("Bell, K. C., Matek, D., Demboski, J. R., & Cook, J. A. (2015). Expanded Host Range of Sucking Lice and Pinworms of Western North American Chipmunks. Comparative Parasitology, 82(2), 312–321. doi:10.1654/4756.1 . Data provided by Kayce C. Bell."));
            assertThat(study.getDOI().toString(), is("10.1654/4756.1"));
            assertThat(study.getCitation(), startsWith("Bell, K. C., Matek, D., Demboski, J. R., & Cook, J. A. (2015). Expanded Host Range of Sucking Lice and Pinworms of Western North American Chipmunks. Comparative Parasitology, 82(2), 312–321. doi:10.1654/4756.1 . Data provided by Kayce C. Bell."));
        }
        ExecutionResult execute = new ExecutionEngine(getGraphDb()).execute("START taxon = node:taxons('*:*') RETURN taxon.name");
        String actual = execute.dumpToString();
        assertThat(actual, containsString("Tamias"));
        assertThat(taxonIndex.findTaxonByName("Tamias speciosus"), is(notNullValue()));
        assertThat(taxonIndex.findTaxonByName("Hoplopleura arboricola"), is(notNullValue()));
        assertThat(nodeFactory.findStudy("bell-"), is(notNullValue()));
    }

    @Test
    public void parseDateTime() {
        DateTime dateTime = StudyImporterForBell.parseDateTime("6/28/34");
        assertThat(dateTime.getYear(), is(1934));
    }

    @Test
    public void parseDateTimeNull() {
        DateTime dateTime = StudyImporterForBell.parseDateTime("mickey mouse");
        assertThat(dateTime, is(nullValue()));
    }

    @Test
    public void parseDateTime2() {
        DateTime dateTime = StudyImporterForBell.parseDateTime("6/23/98");
        assertThat(dateTime.getYear(), is(1998));
    }
}
