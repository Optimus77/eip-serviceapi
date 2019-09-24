package com.inspur.eip.repository;

import com.inspur.eip.entity.eip.EipPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;


@Repository
@RepositoryRestResource(collectionResourceRel = "eippool", path = "eippool")
public interface EipPoolRepository extends JpaRepository<EipPool,Integer> {

//    @Query(value="SELECT * FROM eip_pool AS t1 JOIN (SELECT ROUND(RAND() * (SELECT MAX(id) FROM eip_pool)) AS id) AS t2 WHERE t1.id >= t2.id ORDER BY t1.id ASC LIMIT 1", nativeQuery = true)
//    @Query(value="SELECT * FROM eip_pool WHERE id >= ((SELECT MAX(id) FROM eip_pool) - (SELECT MIN(id) FROM eip_pool)) * RAND() + (SELECT MIN(id) FROM eip_pool) LIMIT 1", nativeQuery = true)
    @Query(value="SELECT * FROM eip_pool WHERE type=?1 LIMIT 1", nativeQuery = true)
    EipPool getEipByRandom(String type);

    EipPool findByIp(String ip);

}
