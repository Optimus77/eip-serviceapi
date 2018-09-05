package com.inspur.icp.service.cost.invoicemng;

import com.inspur.icp.entity.cost.invoicemng.InvoiceInfo;

import java.util.List;
import java.util.Optional;

public interface InvoiceInfoService {
    List<InvoiceInfo> getInvoiceInfos();

    void delInvoiceInfo(Long id);

    InvoiceInfo addInvoiceInfo(InvoiceInfo info);

    Optional<InvoiceInfo> getInvoiceInfo(Long id);

    InvoiceInfo modifyInvoiceInfo(InvoiceInfo invoiceInfo);
}
