package com.inspur.eip.repository;

import com.inspur.eip.entity.eip.ExtNet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RepositoryRestResource(collectionResourceRel = "extnet", path = "extnet")
public interface ExtNetRepository extends JpaRepository<ExtNet,String> {
    List<ExtNet> findByRegion(String region);
}
