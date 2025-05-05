package com.ph4ntoms.authenticate.service.entity;

import com.ph4ntoms.authenticate.pageable.PageableObject;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AService<E, R, ID> {

    List<E> findAllEntities();

    Page<E> searchEntities(PageableObject pageableObject);

    E createEntity(R request);

    E updateEntityById(ID id, R request);

    E findEntityById(ID id);

    void removeEntityById(ID id);

    void removeEntitiesByIds(List<ID> ids);

    void mapRequestToEntity(E entity, R request);
}