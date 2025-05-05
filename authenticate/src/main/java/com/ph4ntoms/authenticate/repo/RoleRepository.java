package com.ph4ntoms.authenticate.repo;

import com.ph4ntoms.authenticate.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends BaseRepository<Role, UUID> {
    Optional<Role> findByName(String name);
    
    @Query(value = """
            SELECT r FROM Role r WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR LOWER(r.code) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Role> searchEntities(String query, Pageable pageable);
}
