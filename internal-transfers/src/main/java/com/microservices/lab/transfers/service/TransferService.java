package com.microservices.lab.transfers.service;

import com.microservices.lab.transfers.model.request.TransferRequest;
import com.microservices.lab.transfers.model.response.TransferResponse;

public interface TransferService {
    TransferResponse transfer(TransferRequest request);


}
