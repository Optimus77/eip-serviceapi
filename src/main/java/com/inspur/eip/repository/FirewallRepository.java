package com.inspur.eip.repository;

import com.inspur.eip.entity.Firewall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FirewallRepository extends JpaRepository<Firewall,String> {

}
