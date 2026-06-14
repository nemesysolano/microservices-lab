package com.microservices.lab.transfers.controller;

import com.microservices.lab.transfers.model.request.TransferRequest;
import com.microservices.lab.transfers.model.response.TransferResponse;
import com.microservices.lab.transfers.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("TransferController")
@RequestMapping("/v1/transfers")
public class TransferController {
    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }
    @PostMapping("/")
    ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest request) {
        TransferResponse response = transferService.transfer(request);
        return response.successful() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}
