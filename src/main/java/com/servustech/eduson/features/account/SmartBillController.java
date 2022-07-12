package com.servustech.eduson.features.account;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
public class SmartBillController {

    @GetMapping(value="/bill")
    private String getBill(){
        String uri = "https://ws.smartbill.ro/SBORO/api/";
        RestTemplate restTemplate=new RestTemplate();
        String result=restTemplate.getForObject(uri,String.class);
        return result;

    }

    @RequestMapping(value = "/bill",method = RequestMethod.POST)
    public @ResponseBody SmartBill postsmartBill(@RequestBody SmartBill postsmartBill){

        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://ws.smartbill.ro/SBORO/api/invoice";

        SmartBill result = restTemplate.postForObject(uri,postsmartBill,SmartBill.class);
        return result;
    }

    @RequestMapping(value = "/payment",method = RequestMethod.DELETE)
    public @ResponseBody SmartBill deletesmartBill(@RequestBody SmartBill deletesmartBill){

        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://ws.smartbill.ro/SBORO/api/payment/chitanta?cif={cif}&seriesname={seriesname}&number={number}";

        SmartBill result = restTemplate.postForObject(uri,deletesmartBill,SmartBill.class);
        return result;
    }

    @RequestMapping(value = "/invoice",method = RequestMethod.PUT)
    public @ResponseBody SmartBill putsmartBill(@RequestBody SmartBill putsmartBill){

        RestTemplate restTemplate = new RestTemplate();
        final String uri = "https://ws.smartbill.ro/SBORO/api/payment/chitanta?cif={cif}&seriesname={seriesname}&number={number}";

        SmartBill result = restTemplate.postForObject(uri,putsmartBill,SmartBill.class);
        return result;
    }

}
