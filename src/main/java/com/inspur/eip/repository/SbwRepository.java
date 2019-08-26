package com.inspur.eip.repository;

import com.inspur.eip.entity.sbw.Sbw;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(collectionResourceRel = "sbw", path = "sbw")
public interface SbwRepository extends JpaRepository<Sbw, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Sbw> findById(String id);

    List<Sbw> findByProjectIdAndIsDelete(String projectId, int isDelete);

    Page<Sbw> findByProjectIdAndIsDelete(String projectId, int isDelete, Pageable pageable);

    Page<Sbw> findByIdAndProjectIdAndIsDelete(String id, String projectId, int isDelete, Pageable pageable);

    Page<Sbw> findByProjectIdAndIsDeleteAndSbwNameContaining(String projectId, int isDelete, String name, Pageable pageable);

    long countByProjectIdAndIsDelete(String projectId, int isDelete);

    long countByStatusAndProjectIdAndIsDelete(String status,String projectId, int isDelete );

    Sbw findByIdAndIsDelete(String id,int isDelete);

}
