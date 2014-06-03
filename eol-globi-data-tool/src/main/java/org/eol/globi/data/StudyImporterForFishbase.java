package org.eol.globi.data;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.swizzle.stream.FixedTokenReplacementInputStream;
import org.codehaus.swizzle.stream.StreamTokenHandler;
import org.codehaus.swizzle.stream.StringTokenHandler;
import org.eol.globi.domain.Specimen;
import org.eol.globi.domain.Study;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class StudyImporterForFishbase extends BaseStudyImporter {
    public StudyImporterForFishbase(ParserFactory parserFactory, NodeFactory nodeFactory) {
        super(parserFactory, nodeFactory);
    }

    @Override
    public Study importStudy() throws StudyImporterException {
        String studyResource = "fishbase/fooditems.tsv";
        try {
            importStudy(getClass().getResourceAsStream(studyResource));
        } catch (IOException e) {
            throw new StudyImporterException("failed to access resource [" + studyResource + "]");
        }

        return null;
    }

    protected void importStudy(InputStream inputStream) throws IOException, StudyImporterException {
        LabeledCSVParser parser = getLabeledCSVParser(inputStream);
        while (parser.getLine() != null) {
            int lastLineNumber = parser.getLastLineNumber();
            if (importFilter.shouldImportRecord((long) lastLineNumber)) {
                Study study = parseStudy(parser);
                Specimen consumer = parseInteraction(parser, study);
                if (consumer != null) {
                    associateLocation(parser, consumer);
                }
            }
        }
    }

    protected static LabeledCSVParser getLabeledCSVParser(InputStream inputStream) throws IOException {
        FixedTokenReplacementInputStream filteredInputStream = new FixedTokenReplacementInputStream(inputStream, "\r", new StringTokenHandler() {
            @Override
            public String handleToken(String token) throws IOException {
                return "";
            }
        });
        Reader reader = FileUtils.getUncompressedBufferedReader(filteredInputStream, CharsetConstant.UTF8);
        LabeledCSVParser parser = new LabeledCSVParser(new CSVParser(reader));
        parser.changeDelimiter('\t');
        return parser;
    }

    private void associateLocation(LabeledCSVParser parser, Specimen consumer) throws StudyImporterException {
        parser.getValueByLabel("locality");
        parser.getValueByLabel("countryCode");
        String latitude = StringUtils.replace(parser.getValueByLabel("latitude"), "NULL", "");
        String longitude = StringUtils.replace(parser.getValueByLabel("longitude"), "NULL", "");

        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
            try {
                consumer.caughtIn(nodeFactory.getOrCreateLocation(Double.parseDouble(latitude),
                        Double.parseDouble(longitude), null));
            } catch (NodeFactoryException e) {
                throw new StudyImporterException("failed to create location using [" + latitude + "] and [" + longitude + "] on line [" + parser.lastLineNumber() + 1 + "]", e);

            } catch (NumberFormatException e) {
                throw new StudyImporterException("failed to create location using [" + latitude + "] and [" + longitude + "] on line [" + parser.lastLineNumber() + 1 + "]", e);
            }
        }
    }

    private Specimen parseInteraction(LabeledCSVParser parser, Study study) throws StudyImporterException {
        Specimen consumer = null;
        try {
            String consumerName = StringUtils.join(new String[]{parser.getValueByLabel("consumer genus"),
                    parser.getValueByLabel("consumer species")}, " ");
            if (StringUtils.isBlank(consumerName)) {
                getLogger().warn(study, "found blank consumer name on line [" + parser.lastLineNumber() + 1 + "]");
            }
            String foodName = parseFoodName(parser);
            if (StringUtils.isBlank(foodName)) {
                getLogger().warn(study, "found blank food item name on line [" + parser.lastLineNumber() + 1 + "]");
            }
            if (StringUtils.isNotBlank(consumerName) && StringUtils.isNotBlank(foodName)) {
                consumer = nodeFactory.createSpecimen(consumerName);
                consumer.ate(nodeFactory.createSpecimen(foodName));
                study.collected(consumer);
            }
        } catch (NodeFactoryException e) {
            throw new StudyImporterException("failed to create specimens on line [" + parser.lastLineNumber() + 1 + "]", e);
        }
        return consumer;
    }

    private Study parseStudy(LabeledCSVParser parser) {
        String author = StringUtils.replace(parser.getValueByLabel("author"), "NULL", "");
        String year = StringUtils.replace(parser.getValueByLabel("year"), "NULL", "");
        String title = StringUtils.replace(parser.getValueByLabel("title"), "NULL", "");
        return nodeFactory.getOrCreateStudy(StringUtils.join("Fishbase-", author, year),
                author,
                "",
                "",
                title
                , year
                , "Database export shared by http://fishbase.org in December 2013. For use by Brian Hayden and Jorrit Poelen only.", null);
    }

    private String parseFoodName(LabeledCSVParser parser) {
        String foodName = StringUtils.join(new String[]{parser.getValueByLabel("food item genus"),
                parser.getValueByLabel("food item species")}, " ");
        if (StringUtils.isBlank(foodName) || StringUtils.contains(foodName, "NULL")) {
            foodName = parser.getValueByLabel("food III");
        }
        return foodName;
    }
}