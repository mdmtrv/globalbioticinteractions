package org.trophic.graph.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.NamedIndexRepository;
import org.trophic.graph.domain.Location;
import org.trophic.graph.domain.Season;

public interface SeasonRepository extends GraphRepository<Season>,
		NamedIndexRepository<Season> {
}
