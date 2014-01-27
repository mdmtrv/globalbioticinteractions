package org.eol.globi.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.lang3.StringUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EcoRegionFinderImpl implements EcoRegionFinder {

    private final EcoRegionFinderConfig config;

    public EcoRegionFinderImpl(EcoRegionFinderConfig config) {
        this.config = config;

    }

    public Map<String, String> findEcoRegion(Point point) throws EcoRegionFinderException {
        Map<String, String> map = null;
        FileDataStore store;
        SimpleFeatureCollection featureCollection;
        try {
            store = FileDataStoreFinder.getDataStore(config.getShapeFileURL());
            SimpleFeatureSource featureSource = store.getFeatureSource();
            featureCollection = featureSource.getFeatures();
        } catch (IOException e) {
            throw new EcoRegionFinderException("failed to load data store from url [" + config.getShapeFileURL().toExternalForm() + "]", e);
        }

        SimpleFeatureIterator features = featureCollection.features();
        while (features.hasNext()) {
            SimpleFeature feature = features.next();
            Object defaultGeometry = feature.getDefaultGeometry();
            if (defaultGeometry instanceof MultiPolygon) {
                MultiPolygon polygon = (MultiPolygon) defaultGeometry;
                if (polygon.contains(point)) {
                    map = new TreeMap<String, String>();
                    SimpleFeatureType featureType = feature.getFeatureType();
                    List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
                    for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
                        String localName = attributeDescriptor.getLocalName();
                        Object value = feature.getAttribute(localName);
                        if (value != null) {
                            if (value instanceof Number) {
                                value = Integer.toString(((Number) value).intValue());
                            } else {
                                value = value.toString();
                            }
                            map.put(attributeDescriptor.getLocalName(), value.toString());
                        }
                    }
                    break;
                }
            }
        }
        features.close();
        return map;
    }

    @Override
    public Collection<EcoRegion> findEcoRegion(double lat, double lng) throws EcoRegionFinderException {
        final Map<String, String> props = findEcoRegion(new GeometryFactory().createPoint(new Coordinate(lng, lat)));
        return props == null || !props.containsKey(config.getIdLabel()) ? null : new ArrayList<EcoRegion>() {{
            add(createEcoRegion(props));
        }};
    }

    private EcoRegion createEcoRegion(Map<String, String> props) {
        EcoRegion ecoRegion;
        ecoRegion = new EcoRegion();
        ecoRegion.setId(config.getNamespace() + ":" + props.get(config.getIdLabel()));
        ecoRegion.setName(props.get(config.getNameLabel()));
        ecoRegion.setGeometry(props.get(config.getGeometryLabel()));

        StringBuilder path = new StringBuilder();
        for (String label : config.getPathLabels()) {
            if (path.length() > 0) {
                path.append(" | ");
            }
            if (props.containsKey(label)) {
                String value = props.get(label);
                if (StringUtils.isNotBlank(value)) {
                    path.append(value);
                }

            }
        }
        ecoRegion.setPath(path.toString());
        return ecoRegion;
    }

}
