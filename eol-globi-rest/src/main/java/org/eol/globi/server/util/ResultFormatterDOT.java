package org.eol.globi.server.util;

import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResultFormatterDOT implements ResultFormatter {
    protected static String getSafeLabel(String string) {
        return string.replaceAll("\\W", "_");
    }

    /*
        https://en.wikipedia.org/wiki/DOT_language

        digraph graphName {
            a -> b -> c;
            b -> d;
        }
         */

    @Override
    public String format(final String content) throws ResultFormattingException {
        StringBuilder builder = dotPrefix();
        JsonNode results;
        try {
            results = RequestHelper.parse(content);
        } catch (IOException e) {
            throw new ResultFormattingException("failed to parse", e);
        }
        JsonNode columns = results.get("columns");
        Map<String, Integer> nameIndex = new HashMap<String, Integer>();
        for (int i = 0; i < columns.size(); i++) {
            nameIndex.put(columns.get(i).getTextValue(), i);
        }
        JsonNode rows = results.get("data");
        for (JsonNode row : rows) {
            JsonNode sourceTaxon = row.get(nameIndex.get(ResultField.SOURCE_TAXON_NAME.getLabel()));
            JsonNode targetTaxon = row.get(nameIndex.get(ResultField.TARGET_TAXON_NAME.getLabel()));
            JsonNode type = row.get(nameIndex.get(ResultField.INTERACTION_TYPE.getLabel()));
            if (sourceTaxon != null && targetTaxon != null && type != null) {
                String sourceId = getSafeLabel(sourceTaxon.getTextValue());
                if (targetTaxon.isArray()) {
                    for (JsonNode targetTaxonItem : targetTaxon) {
                        appendEdge(builder, targetTaxonItem, type, sourceId);
                    }
                } else {
                    appendEdge(builder, targetTaxon, type, sourceId);
                }
            }
        }
        dotSuffix(builder);
        return builder.toString();
    }

    private void appendEdge(StringBuilder builder, JsonNode targetTaxon, JsonNode type, String sourceId) {
        String targetId = getSafeLabel(targetTaxon.getTextValue());
        appendEdge(builder, type, sourceId, targetId);
    }

    private void appendEdge(StringBuilder builder, JsonNode type, String sourceId, String targetId) {
        builder.append(sourceId);
        builder.append("->");
        builder.append(targetId);
        builder.append("[label=\"");
        builder.append(type.getTextValue());
        builder.append("\"];\n");
    }

    private void dotSuffix(StringBuilder builder) {
        builder.append(" }");
    }

    private StringBuilder dotPrefix() {
        StringBuilder builder = new StringBuilder();
        builder.append("// generated by GloBI (see http://globalbioticinteractions.org) on ");
        builder.append(DateTimeFormat.fullDateTime().print(new DateTime()));
        builder.append("\n");
        builder.append("digraph GloBI {\n");
        return builder;
    }
}
