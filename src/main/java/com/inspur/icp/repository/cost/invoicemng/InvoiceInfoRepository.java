package com.inspur.icp.repository.cost.invoicemng;

import com.inspur.icp.entity.cost.invoicemng.InvoiceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceInfoRepository extends JpaRepository<InvoiceInfo,Long> {
}