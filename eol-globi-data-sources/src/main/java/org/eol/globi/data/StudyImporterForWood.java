package org.eol.globi.data;

import com.Ostermiller.util.LabeledCSVParser;
import org.apache.commons.lang3.StringUtils;
import org.eol.globi.domain.Term;
import org.eol.globi.geo.LatLng;
import org.eol.globi.service.TaxonUtil;
import org.eol.globi.util.CSVTSVUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

public class StudyImporterForWood extends StudyImporterWithListener {

    public StudyImporterForWood(ParserFactory parserFactory, NodeFactory nodeFactory) {
        super(parserFactory, nodeFactory);
    }

    @Override
    public void importStudy() throws StudyImporterException {
        try (InputStream resource = getDataset().retrieve(URI.create("links"))) {
            importLinks(resource, getInteractionListener(), getFilter());
        } catch (IOException e) {
            throw new StudyImporterException(e);
        }
    }

    public void importLinks(InputStream inputStream, InteractionListener interactionListener, ImportFilter filter) throws IOException, StudyImporterException {
        LabeledCSVParser parser = CSVTSVUtil.createLabeledCSVParser(CSVTSVUtil.createExcelCSVParse(inputStream));

        while ((filter == null || filter.shouldImportRecord((long) parser.lastLineNumber())) && parser.getLine() != null) {
            Map<String, String> e = importLink(parser);
            if (e != null) {
                interactionListener.newLink(e);
            }
        }
    }


    private Map<String, String> importLink(LabeledCSVParser parser) {
        Map<String, String> link = new TreeMap<String, String>();
        addTSN(parser, link, "PredTSN", TaxonUtil.SOURCE_TAXON_ID);
        link.put(TaxonUtil.SOURCE_TAXON_NAME, parser.getValueByLabel("PredName"));
        addTSN(parser, link, "PreyTSN", TaxonUtil.TARGET_TAXON_ID);
        link.put(TaxonUtil.TARGET_TAXON_NAME, parser.getValueByLabel("PreyName"));
        link.put(StudyImporterForTSV.STUDY_SOURCE_CITATION, getSourceCitationLastAccessed());
        link.put(StudyImporterForTSV.REFERENCE_CITATION, getSourceCitation());
        link.put(StudyImporterForTSV.REFERENCE_ID, getSourceDOI().toPrintableDOI());
        link.put(StudyImporterForTSV.REFERENCE_DOI, getSourceDOI().toString());
        link.put(StudyImporterForTSV.REFERENCE_URL, getSourceDOI().toURI().toString());
        Term locality = StudyImporterNodesAndLinks.localityForDataset(getDataset());
        if (locality != null) {
            link.put(StudyImporterForTSV.LOCALITY_NAME, locality.getName());
            link.put(StudyImporterForTSV.LOCALITY_ID, locality.getId());
        }
        LatLng latLng = StudyImporterNodesAndLinks.locationForDataset(getDataset());
        if (latLng != null) {
            link.put(StudyImporterForTSV.DECIMAL_LATITUDE, Double.toString(latLng.getLat()));
            link.put(StudyImporterForTSV.DECIMAL_LONGITUDE, Double.toString(latLng.getLng()));
        }
        link.put(StudyImporterForTSV.INTERACTION_TYPE_NAME, "preysOn");
        link.put(StudyImporterForTSV.INTERACTION_TYPE_ID, "RO:0002439");
        return link;
    }

    private static void addTSN(LabeledCSVParser parser, Map<String, String> link, String tsn, String tsnLabel) {
        String tsnValue = parser.getValueByLabel(tsn);
        if (!StringUtils.startsWith(tsnValue, "san")) {
            link.put(tsnLabel, "ITIS:" + tsnValue);
        }
    }

}
