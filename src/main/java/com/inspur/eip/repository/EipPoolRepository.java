package com.inspur.eip.repository;

import com.inspur.eip.entity.EipPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EipPoolRepository extends JpaRepository<EipPool,String> {


}
