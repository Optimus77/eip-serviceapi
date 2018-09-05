package com.inspur.icp.service.cost.invoicemng.impl;

import com.inspur.icp.entity.cost.invoicemng.InvoiceInfo;
import com.inspur.icp.repository.cost.invoicemng.InvoiceInfoRepository;
import com.inspur.icp.service.cost.invoicemng.InvoiceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceInfoServiceImpl implements InvoiceInfoService {
    private final InvoiceInfoRepository invoiceInfoRepository;

    @Autowired
    public InvoiceInfoServiceImpl(InvoiceInfoRepository invoiceInfoRepository) {
        this.invoiceInfoRepository = invoiceInfoRepository;
    }

    @Override
    public List<InvoiceInfo> getInvoiceInfos() {
        return invoiceInfoRepository.findAll();
    }

    @Override
    public void delInvoiceInfo(Long id) {
        invoiceInfoRepository.deleteById(id);
    }

    @Override
    public InvoiceInfo addInvoiceInfo(InvoiceInfo invoiceInfo) {
        return invoiceInfoRepository.save(invoiceInfo);
    }

    @Override
    public Optional<InvoiceInfo> getInvoiceInfo(Long id) {
        return invoiceInfoRepository.findById(id);
    }

    @Override
    public InvoiceInfo modifyInvoiceInfo(InvoiceInfo invoiceInfo) {
        return invoiceInfoRepository.save(invoiceInfo);
    }

}
