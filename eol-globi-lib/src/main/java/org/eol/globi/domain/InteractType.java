package org.eol.globi.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum InteractType implements RelType {
    PREYS_UPON("http://purl.obolibrary.org/obo/RO_0002439"),
    PARASITE_OF("http://purl.obolibrary.org/obo/RO_0002444"),
    HAS_HOST("http://purl.obolibrary.org/obo/RO_0002454"),
    INTERACTS_WITH("http://purl.obolibrary.org/obo/RO_0002437"),
    HOST_OF("http://purl.obolibrary.org/obo/RO_0002453"),
    POLLINATES("http://purl.obolibrary.org/obo/RO_0002455"),
    PERCHING_ON(PropertyAndValueDictionary.NO_MATCH),
    ATE("http://purl.obolibrary.org/obo/RO_0002470"),
    SYMBIONT_OF("http://purl.obolibrary.org/obo/RO_0002440"),
    PREYED_UPON_BY("http://purl.obolibrary.org/obo/RO_0002458"),
    POLLINATED_BY("http://purl.obolibrary.org/obo/RO_0002456"),
    EATEN_BY("http://purl.obolibrary.org/obo/RO_0002471"),
    HAS_PARASITE("http://purl.obolibrary.org/obo/RO_0002445"),
    PERCHED_ON_BY(PropertyAndValueDictionary.NO_MATCH),
    HAS_PATHOGEN("http://purl.obolibrary.org/obo/RO_0002557"),
    PATHOGEN_OF("http://purl.obolibrary.org/obo/RO_0002556"),

    HAS_VECTOR("http://purl.obolibrary.org/obo/RO_0002460"),
    VECTOR_OF("http://purl.obolibrary.org/obo/RO_0002459"),
    FLOWERS_VISITED_BY("http://purl.obolibrary.org/obo/RO_0002622"),
    VISITS_FLOWERS_OF("http://purl.obolibrary.org/obo/RO_0002623"),

    INHABITED_BY(PropertyAndValueDictionary.NO_MATCH),
    INHABITS(PropertyAndValueDictionary.NO_MATCH),

    LIVED_ON_BY(PropertyAndValueDictionary.NO_MATCH),
    LIVES_ON(PropertyAndValueDictionary.NO_MATCH),

    LIVED_INSIDE_OF_BY(PropertyAndValueDictionary.NO_MATCH),
    LIVES_INSIDE_OF(PropertyAndValueDictionary.NO_MATCH),

    LIVED_NEAR_BY(PropertyAndValueDictionary.NO_MATCH),
    LIVES_NEAR(PropertyAndValueDictionary.NO_MATCH),

    LIVED_UNDER_BY(PropertyAndValueDictionary.NO_MATCH),
    LIVES_UNDER(PropertyAndValueDictionary.NO_MATCH),

    LIVES_WITH(PropertyAndValueDictionary.NO_MATCH),

    ENDOPARASITE_OF("http://purl.obolibrary.org/obo/RO_0002634"),
    HAS_ENDOPARASITE("http://purl.obolibrary.org/obo/RO_0002635"),

    HYPERPARASITE_OF("http://purl.obolibrary.org/obo/RO_0002553"),
    HAS_HYPERPARASITE("http://purl.obolibrary.org/obo/RO_0002554"),

    HYPERPARASITOID_OF(PropertyAndValueDictionary.NO_MATCH),
    HAS_HYPERPARASITOID(PropertyAndValueDictionary.NO_MATCH),

    ECTOPARASITE_OF("http://purl.obolibrary.org/obo/RO_0002632"),
    HAS_ECTOPARASITE("http://purl.obolibrary.org/obo/RO_0002633"),

    KLEPTOPARASITE_OF(PropertyAndValueDictionary.NO_MATCH),
    HAS_KLEPTOPARASITE(PropertyAndValueDictionary.NO_MATCH),

    PARASITOID_OF(PropertyAndValueDictionary.NO_MATCH),
    HAS_PARASITOID(PropertyAndValueDictionary.NO_MATCH),

    ENDOPARASITOID_OF(PropertyAndValueDictionary.NO_MATCH),
    HAS_ENDOPARASITOID(PropertyAndValueDictionary.NO_MATCH),

    ECTOPARASITOID_OF(PropertyAndValueDictionary.NO_MATCH),
    HAS_ECTOPARASITOID(PropertyAndValueDictionary.NO_MATCH),

    // living in something that is not the body.
    GUEST_OF(PropertyAndValueDictionary.NO_MATCH),
    HAS_GUEST_OF(PropertyAndValueDictionary.NO_MATCH),

    FARMED_BY(PropertyAndValueDictionary.NO_MATCH),
    FARMS(PropertyAndValueDictionary.NO_MATCH),

    DAMAGED_BY(PropertyAndValueDictionary.NO_MATCH),
    DAMAGES(PropertyAndValueDictionary.NO_MATCH),

    DISPERSAL_VECTOR_OF("http://eol.org/schema/terms/DispersalVector"),
    HAS_DISPERAL_VECTOR("http://eol.org/schema/terms/HasDispersalVector"),

    KILLED_BY("http://purl.obolibrary.org/obo/RO_0002627"),
    KILLS("http://purl.obolibrary.org/obo/RO_0002626");

    String iri;

    private static final Map<String, InteractType> SYNONYMS = new HashMap<String, InteractType>() {{
        put("http://eol.org/schema/terms/FlowersVisitedBy", FLOWERS_VISITED_BY);
        put("http://eol.org/schema/terms/VisitsFlowersOf", VISITS_FLOWERS_OF);
        put("http://eol.org/schema/terms/kills", KILLS);
        put("http://eol.org/schema/terms/isKilledBy", KILLED_BY);
    }};

    InteractType(String iri) {
        this.iri = iri;
    }

    public static InteractType typeOf(String iri) {
        if (StringUtils.startsWith(iri, "RO:")) {
            iri = StringUtils.replace(iri, "RO:", PropertyAndValueDictionary.RO_NAMESPACE);
        }
        InteractType[] values = values();
        for (InteractType interactType : values) {
            if (StringUtils.equals(iri, interactType.getIRI())) {
                return interactType;
            }
        }
        return SYNONYMS.get(iri);
    }

    public String getIRI() {
        return iri;
    }

    public static Collection<InteractType> hasTypes(InteractType type) {
        final Map<InteractType, Collection<InteractType>> pathMap = new HashMap<InteractType, Collection<InteractType>>() {
            {
                put(INTERACTS_WITH, new ArrayList<InteractType>());
                put(PERCHING_ON, Arrays.asList(LIVES_ON, INTERACTS_WITH));
                put(ATE, Arrays.asList(INTERACTS_WITH));
                put(SYMBIONT_OF, Arrays.asList(INTERACTS_WITH));
                put(PREYS_UPON, Arrays.asList(ATE, KILLS, INTERACTS_WITH));
                put(PATHOGEN_OF, Arrays.asList(PARASITE_OF, HAS_HOST, SYMBIONT_OF, INTERACTS_WITH));
                put(VECTOR_OF, Arrays.asList(HOST_OF, SYMBIONT_OF, INTERACTS_WITH));
                put(DISPERSAL_VECTOR_OF, Arrays.asList(HOST_OF, SYMBIONT_OF, INTERACTS_WITH));
                put(PARASITOID_OF, Arrays.asList(PARASITE_OF, HAS_HOST, ATE, KILLS, LIVES_WITH, SYMBIONT_OF, INTERACTS_WITH));
                put(ENDOPARASITOID_OF, Arrays.asList(PARASITOID_OF, PARASITE_OF, HAS_HOST, ATE, KILLS, LIVES_WITH, SYMBIONT_OF, INTERACTS_WITH));
                put(ECTOPARASITOID_OF, Arrays.asList(PARASITOID_OF, PARASITE_OF, HAS_HOST, ATE, KILLS, LIVES_WITH, SYMBIONT_OF, INTERACTS_WITH));
                put(HYPERPARASITOID_OF, Arrays.asList(PARASITOID_OF, PARASITE_OF,HAS_HOST,  ATE, KILLS, LIVES_WITH, SYMBIONT_OF, INTERACTS_WITH));
                put(PARASITE_OF, Arrays.asList(ATE, DAMAGES, LIVES_WITH, HAS_HOST, SYMBIONT_OF, INTERACTS_WITH));
                put(HYPERPARASITE_OF, Arrays.asList(PARASITE_OF, ATE, DAMAGES, HAS_HOST, LIVES_WITH, SYMBIONT_OF, INTERACTS_WITH));
                put(ENDOPARASITE_OF, Arrays.asList(PARASITE_OF, LIVES_INSIDE_OF, HAS_HOST, ATE, DAMAGES, SYMBIONT_OF, INTERACTS_WITH));
                put(ECTOPARASITE_OF, Arrays.asList(PARASITE_OF, LIVES_ON, ATE, HAS_HOST, DAMAGES, SYMBIONT_OF, INTERACTS_WITH));
                put(POLLINATES, Arrays.asList(VISITS_FLOWERS_OF, ATE, HAS_HOST, SYMBIONT_OF, INTERACTS_WITH));
                put(VISITS_FLOWERS_OF, Arrays.asList(HAS_HOST,INTERACTS_WITH));
                put(HOST_OF, Arrays.asList(SYMBIONT_OF, INTERACTS_WITH));
                put(KLEPTOPARASITE_OF, Arrays.asList(INTERACTS_WITH));
                put(INHABITS, Arrays.asList(INTERACTS_WITH));
                put(LIVES_ON, Arrays.asList(INTERACTS_WITH));
                put(LIVES_INSIDE_OF, Arrays.asList(INTERACTS_WITH));
                put(LIVES_NEAR, Arrays.asList(INTERACTS_WITH));
                put(LIVES_UNDER, Arrays.asList(INTERACTS_WITH));
                put(LIVES_WITH, Arrays.asList(INTERACTS_WITH));
                put(GUEST_OF, Arrays.asList(INTERACTS_WITH));
                put(FARMS, Arrays.asList(ATE, SYMBIONT_OF, INTERACTS_WITH));
                put(DAMAGES, Arrays.asList(INTERACTS_WITH));
                put(DISPERSAL_VECTOR_OF, Arrays.asList(HOST_OF, INTERACTS_WITH, VECTOR_OF));
                put(KILLS, Arrays.asList(INTERACTS_WITH));
            }
        };

        Map<InteractType, Collection<InteractType>> invertedPathMap = new HashMap<InteractType, Collection<InteractType>>() {
            {
                for (Map.Entry<InteractType, Collection<InteractType>> entry : pathMap.entrySet())

                {
                    ArrayList<InteractType> invertedPath = new ArrayList<InteractType>();
                    InteractType keyInverse = inverseOf(entry.getKey());
                    if (keyInverse != entry.getKey()) {
                        for (InteractType interactType : entry.getValue()) {
                            InteractType inverse = inverseOf(interactType);
                            if (null != inverse) {
                                invertedPath.add(inverse);
                            }
                        }
                        put(keyInverse, invertedPath);
                    }
                }
            }
        };
        pathMap.putAll(invertedPathMap);
        return pathMap.get(type);
    }

    public static Collection<InteractType> typesOf(InteractType type) {
        Collection<InteractType> inversePath = new ArrayList<InteractType>();
        inversePath.add(type);
        for (InteractType interactType : values()) {
            if (hasTypes(interactType).contains(type)) {
                inversePath.add(interactType);
            }
        }
        return inversePath;
    }

    public static InteractType inverseOf(InteractType type) {
        Map<InteractType, InteractType> inverseMap = new HashMap<InteractType, InteractType>() {
            {
                put(POLLINATES, POLLINATED_BY);
                put(PATHOGEN_OF, HAS_PATHOGEN);
                put(VECTOR_OF, HAS_VECTOR);
                put(FLOWERS_VISITED_BY, VISITS_FLOWERS_OF);
                put(INHABITED_BY, INHABITS);
                put(FARMED_BY, FARMS);
                put(LIVED_ON_BY, LIVES_ON);
                put(LIVED_INSIDE_OF_BY, LIVES_INSIDE_OF);
                put(LIVED_NEAR_BY, LIVES_NEAR);
                put(LIVED_UNDER_BY, LIVES_UNDER);
                put(LIVES_WITH, LIVES_WITH);
                put(KLEPTOPARASITE_OF, HAS_KLEPTOPARASITE);
                put(GUEST_OF, HAS_GUEST_OF);
                put(PERCHING_ON, PERCHED_ON_BY);
                put(HOST_OF, HAS_HOST);
                put(PREYS_UPON, PREYED_UPON_BY);
                put(ATE, EATEN_BY);
                put(DAMAGED_BY, DAMAGES);
                put(KILLS, KILLED_BY);
                put(SYMBIONT_OF, SYMBIONT_OF);
                put(INTERACTS_WITH, INTERACTS_WITH);
                put(PARASITE_OF, HAS_PARASITE);
                put(HYPERPARASITE_OF, HAS_HYPERPARASITE);
                put(ENDOPARASITE_OF, HAS_ENDOPARASITE);
                put(ECTOPARASITE_OF, HAS_ECTOPARASITE);
                put(PARASITOID_OF, HAS_PARASITOID);
                put(HYPERPARASITOID_OF, HAS_HYPERPARASITOID);
                put(ENDOPARASITOID_OF, HAS_ENDOPARASITOID);
                put(ECTOPARASITOID_OF, HAS_ECTOPARASITOID);
                put(DISPERSAL_VECTOR_OF, HAS_DISPERAL_VECTOR);
            }
        };

        final Map<InteractType, InteractType> swappedMap = new HashMap<InteractType, InteractType>();
        for (Map.Entry<InteractType, InteractType> entry : inverseMap.entrySet()) {
            swappedMap.put(entry.getValue(), entry.getKey());
        }
        inverseMap.putAll(swappedMap);

        return inverseMap.get(type);
    }
}
