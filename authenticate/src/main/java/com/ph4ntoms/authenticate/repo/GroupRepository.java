package com.ph4ntoms.authenticate.repo;

import com.ph4ntoms.authenticate.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends BaseRepository<Group, UUID> {
    Optional<Group> findByName(String name);
    
    @Query(value = """
            SELECT g FROM Group g WHERE (LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR LOWER(g.code) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Group> searchEntities(String query, Pageable pageable);
}
