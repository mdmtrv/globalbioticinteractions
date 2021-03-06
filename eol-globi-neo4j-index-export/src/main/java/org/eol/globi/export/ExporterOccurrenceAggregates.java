package org.eol.globi.export;

import org.eol.globi.domain.Study;
import org.eol.globi.domain.StudyNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExporterOccurrenceAggregates extends ExporterOccurrencesBase {

    @Override
    public void doExportStudy(final StudyNode study, ExportUtil.Appender writer, boolean includeHeader) throws IOException {
        if (includeHeader) {
            exportDistinct(study, writer);
        }
    }

    public void exportDistinct(StudyNode study, ExportUtil.Appender writer) throws IOException {
        ExporterAggregateUtil.exportDistinctInteractionsByStudy(writer, study.getUnderlyingNode().getGraphDatabase(), new OccurrenceRowWriter());
    }

    class OccurrenceRowWriter implements ExporterAggregateUtil.RowWriter {
        @Override
        public void writeRow(ExportUtil.Appender writer, StudyNode study, String sourceTaxonId, String relationshipType, List<String> targetTaxonIds) throws IOException {
            HashMap<String, String> properties = new HashMap<String, String>();
            String sourceOccurrenceId = study.getNodeID() + "-" + sourceTaxonId + "-" + relationshipType;
            writeRow(writer, properties, "globi:occur:source:" + sourceOccurrenceId, sourceTaxonId);
            for (String targetTaxonId : targetTaxonIds) {
                String targetOccurrenceId = sourceOccurrenceId + "-" + targetTaxonId;
                writeRow(writer, properties, "globi:occur:target:" + targetOccurrenceId, targetTaxonId);
            }
        }

        private void writeRow(ExportUtil.Appender writer, Map<String, String> properties, String idPrefix, String taxonExternalId) throws IOException {
            properties.put(EOLDictionary.OCCURRENCE_ID, idPrefix);
            properties.put(EOLDictionary.TAXON_ID, taxonExternalId);
            writeProperties(writer, properties);
            properties.clear();
        }
    }

}
