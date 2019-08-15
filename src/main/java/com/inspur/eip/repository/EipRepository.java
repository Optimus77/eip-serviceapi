package com.inspur.eip.repository;

import com.inspur.eip.entity.eip.Eip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(collectionResourceRel = "eip", path = "eip")
public interface EipRepository extends JpaRepository<Eip,String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Eip> findById(String id);

    Eip findByIdAndIsDelete(String id, int isDelete);

    Eip findByInstanceIdAndIsDelete (String instanceId, int isDelete);

    List<Eip> findByProjectIdAndIsDelete(String projectId, int isDelete);

    Eip findByEipAddressAndProjectIdAndIsDelete(String ipAddress, String projectId, int isDelete);

    Eip findByEipAddressAndIsDelete(String ipAddress, int isDelete);

    Page<Eip> findByProjectIdAndIsDelete(String projectId, int isDelete, Pageable pageable);

    long countBySbwIdAndIsDelete(String sharedBandWidthId, int isDelete);

    long countByPipId(String pipeId);

    Page<Eip> findByProjectIdAndIsDeleteAndSbwId(String projectId, int isDelete, String sbwId, Pageable pageable);

    List<Eip> findByProjectIdAndIsDeleteAndSbwId(String projectId, int isDelete, String sbwId);

    @Query(value = "SELECT * from eip where project_id=?1 and is_delete=?2 and bill_type=?3 and(sbw_id is null or sbw_id=?4)",nativeQuery = true)
    List<Eip> getEipListNotBinding(String projectId, int isDelete,String billType, String sbwId);

    List<Eip> findByProjectIdAndIsDeleteAndBillType(String projectId, int isDelete,String billType);

    List<Eip> findByChargeModeAndIsDelete(String chargeMode, int isDelete);
}
