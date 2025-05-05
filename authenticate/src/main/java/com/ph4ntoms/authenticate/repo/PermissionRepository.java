package com.ph4ntoms.authenticate.repo;

import com.ph4ntoms.authenticate.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);

    @Query(value = """
            SELECT p FROM Permission p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR LOWER(p.code) LIKE LOWER(CONCAT('%', :query, '%')) 
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Permission> searchEntities(String query, Pageable pageable);

}
