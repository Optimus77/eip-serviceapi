package com.inspur.eip.repository;

import com.inspur.eip.entity.Eip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EipRepository extends JpaRepository<Eip,String> {


}
