package com.servustech.eduson.features.account;

import com.servustech.eduson.features.account.role.RoleName;
import com.servustech.eduson.features.permissions.ChangeBeneficiaryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
public class SmartBillController {

    @GetMapping(value = "/bill")
    private String getBill() {
        String uri = "https://ws.smartbill.ro/SBORO/api/";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        return result;

    }

    @RequestMapping(value = "/bill", method = RequestMethod.POST)
    public @ResponseBody SmartBill postsmartBill(@RequestBody SmartBill postsmartBill,@RequestParam(required = true) String companyVatCode) {

        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://ws.smartbill.ro/SBORO/api/invoice";

        SmartBill result = restTemplate.postForObject(uri, postsmartBill, SmartBill.class);
        return result;
    }

    @RequestMapping(value = "/payment", method = RequestMethod.DELETE)
    public @ResponseBody SmartBill deletesmartBill(@RequestBody SmartBill deletesmartBill) {

        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://ws.smartbill.ro/SBORO/api/payment/chitanta?cif={cif}&seriesname={seriesname}&number={number}";

        SmartBill result = restTemplate.postForObject(uri, deletesmartBill, SmartBill.class);
        return result;
    }

    @RequestMapping(value = "/invoice", method = RequestMethod.PUT)
    public @ResponseBody SmartBill setInvoice(@RequestBody SmartBill setInvoice) {

        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://ws.smartbill.ro/SBORO/api/payment/chitanta?cif={cif}&seriesname={seriesname}&number={number}";

        SmartBill result = restTemplate.postForObject(uri, setInvoice, SmartBill.class);
        return result;
    }

    @RequestMapping(value = "/invoice/paymentstatus", method = RequestMethod.GET)
    public @ResponseBody SmartBill getsmartBill(@RequestParam(required = true) SmartBill getsmartBill,@RequestParam(required = true) String cif,@RequestParam(required = true) String seriesname,@RequestParam(required = true) String number ) {

        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://ws.smartbill.ro/SBORO/api/invoice/paymentstatus?cif={cif}&seriesname={seriesname}&number={number}";

        SmartBill result = restTemplate.postForObject(uri, getsmartBill, SmartBill.class);
        return result;
    }

}
