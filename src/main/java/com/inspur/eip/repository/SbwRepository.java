package com.inspur.eip.repository;

import com.inspur.eip.entity.sbw.Sbw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import javax.persistence.LockModeType;
import java.util.List;

@Repository
@RepositoryRestResource(collectionResourceRel = "sbw", path = "sbw")
public interface SbwRepository extends JpaRepository<Sbw, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Sbw findBySbwId(String id);

    List<Sbw> findByProjectIdAndIsDelete(String projectId, int isDelete);

    Page<Sbw> findByProjectIdAndIsDelete(String projectId, int isDelete, Pageable pageable);

    Page<Sbw> findBySbwIdAndProjectIdAndIsDelete(String id, String projectId, int isDelete, Pageable pageable);

    Page<Sbw> findByProjectIdAndIsDeleteAndSbwNameContaining(String projectId, int isDelete, String name, Pageable pageable);

    long countByProjectIdAndIsDelete(String projectId, int isDelete);
}
