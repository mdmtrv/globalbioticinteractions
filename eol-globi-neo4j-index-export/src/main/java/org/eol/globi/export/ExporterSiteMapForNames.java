package org.eol.globi.export;

import org.eol.globi.data.StudyImporterException;
import org.eol.globi.data.export.SiteMapUtils;
import org.eol.globi.domain.RelTypes;
import org.eol.globi.domain.Study;
import org.eol.globi.domain.StudyNode;
import org.eol.globi.domain.TaxonNode;
import org.eol.globi.util.NodeTypeDirection;
import org.eol.globi.util.NodeUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ExporterSiteMapForNames implements GraphExporter {

    @Override
    public void export(GraphDatabaseService graphDatabase, String baseDir) throws StudyImporterException {
        Set<String> names = new HashSet<String>();
        names.add("Homo sapiens");
        // just do it once
        final List<StudyNode> allStudies = NodeUtil.findAllStudies(graphDatabase);
        for (StudyNode allStudy : allStudies) {
            NodeUtil.handleCollectedRelationships(new NodeTypeDirection(allStudy.getUnderlyingNode()), specimen -> {
                final Iterable<Relationship> relationships = specimen.getEndNode().getRelationships(Direction.OUTGOING, NodeUtil.asNeo4j(RelTypes.CLASSIFIED_AS));
                if (relationships.iterator().hasNext()) {
                    final Node endNode = relationships.iterator().next().getEndNode();
                    final TaxonNode taxonNode = new TaxonNode(endNode);
                    names.add(taxonNode.getName());
                }
            });
        }
        final String queryParamName = "interactionType=interactsWith&sourceTaxon=";
        final String siteMapLocation = "https://depot.globalbioticinteractions.org/snapshot/target/data/sitemap/names/";
        SiteMapUtils.generateSiteMap(names, baseDir, queryParamName, siteMapLocation);
    }

}
