package com.inspur.icp.controller.cost.invoicemng;

import com.inspur.icp.entity.cost.invoicemng.InvoiceInfo;
import com.inspur.icp.entity.ReturnMsg;
import com.inspur.icp.service.cost.invoicemng.InvoiceInfoService;
import com.inspur.icp.util.ReturnMsgUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/cost")
@RestController
public class InvoiceInfoController {

    private final InvoiceInfoService invoiceInfoService;

    @Autowired
    public InvoiceInfoController(InvoiceInfoService invoiceInfoService) {
        this.invoiceInfoService = invoiceInfoService;
    }

    @ApiOperation(value = "Get all invoiceInfos")
    @GetMapping("/invoiceInfos")
    public ReturnMsg getInvoiceInfos() {
        List<InvoiceInfo> invoiceInfos = invoiceInfoService.getInvoiceInfos();

        return ReturnMsgUtil.success(invoiceInfos);
    }

    @GetMapping("/invoiceInfo/{id}")
    public ReturnMsg getInvoiceInfo(@PathVariable("id") Long id) {
        Optional<InvoiceInfo> invoiceInfoOptional = invoiceInfoService.getInvoiceInfo(id);
        if (invoiceInfoOptional.isPresent()) {
            return ReturnMsgUtil.success(invoiceInfoOptional.get());
        }
        return ReturnMsgUtil.error(400, "No related record has been found");
    }


    @DeleteMapping("/invoiceInfo/{id}")
    public ReturnMsg delInvoiceInfo(@PathVariable("id") Long id) {
        invoiceInfoService.delInvoiceInfo(id);
        return ReturnMsgUtil.success();
    }

    @PutMapping("/invoiceInfo")
    public ReturnMsg modifyInvoiceInfo(@RequestBody InvoiceInfo invoiceInfo) {
        return ReturnMsgUtil.success(invoiceInfoService.modifyInvoiceInfo(invoiceInfo));
    }

    @PostMapping("/invoiceInfo")
    public ReturnMsg addInvoiceInfo(@RequestBody InvoiceInfo invoiceInfo) {
        return ReturnMsgUtil.success(invoiceInfoService.addInvoiceInfo(invoiceInfo));
    }
}
