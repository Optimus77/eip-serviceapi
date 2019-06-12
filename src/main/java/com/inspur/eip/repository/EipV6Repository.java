package com.inspur.eip.repository;

import com.inspur.eip.entity.ipv6.EipV6;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(collectionResourceRel = "eipv6", path = "eipv6")
public interface EipV6Repository extends JpaRepository<EipV6,String> {

    EipV6 findByEipv6Id(String id);

    EipV6 findByEipv6IdAndIsDelete(String eipv6Id,int isDelete);

    List<EipV6> findByUserIdAndIsDelete(String projectId, int isDelete);

    EipV6 findByIpv6AndUserIdAndIsDelete(String ipAddress, String userId, int isDelete);

    EipV6 findByIpv6AndIsDelete(String ipAddress, int isDelete);

    Page<EipV6> findByUserIdAndIsDelete(String projectId, int isDelete, Pageable pageable);

    EipV6 findByIpv4AndUserIdAndIsDelete(String eipAddress, String projcectid, int isDelete);





}
