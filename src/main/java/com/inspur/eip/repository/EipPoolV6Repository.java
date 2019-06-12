package com.inspur.eip.repository;

import com.inspur.eip.entity.ipv6.EipPoolV6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(collectionResourceRel = "eippoolv6", path = "eippoolv6")
public interface EipPoolV6Repository extends JpaRepository<EipPoolV6,Integer> {

    EipPoolV6 findByIp(String ip);

//    @Query(value="SELECT * FROM eip_poolv6 AS t1 JOIN (SELECT ROUND(RAND() * (SELECT MAX(id) FROM eip_poolv6)) AS id) AS t2 WHERE t1.id >= t2.id ORDER BY t1.id ASC LIMIT 1", nativeQuery = true)
    @Query(value="SELECT * FROM eip_poolv6 WHERE id >= ((SELECT MAX(id) FROM eip_poolv6) - (SELECT MIN(id) FROM eip_poolv6)) * RAND() + (SELECT MIN(id) FROM eip_poolv6) LIMIT 1", nativeQuery = true)
    EipPoolV6 getEipV6ByRandom();
}
