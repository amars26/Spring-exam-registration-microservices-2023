package com.amars.walletservice.controller;

import com.amars.walletservice.dto.PaymentRequestDTO;
import com.amars.walletservice.dto.WalletRequestDTO;
import com.amars.walletservice.dto.WalletResponseDTO;
import com.amars.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/createWallet")
    @ResponseStatus(HttpStatus.CREATED)
    public void createWallet(@RequestBody WalletRequestDTO walletRequestDTO){
        walletService.createWallet(walletRequestDTO);
    }

    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.OK)
    public void payForExam(@RequestBody PaymentRequestDTO paymentRequestDTO){
        walletService.payForExam(paymentRequestDTO);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void addFunds(@RequestBody PaymentRequestDTO paymentRequestDTO){
        walletService.addFunds(paymentRequestDTO);
    }

    @PostMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public WalletResponseDTO studentStatus(@RequestBody WalletRequestDTO walletRequestDTO){
        return walletService.walletStatus(walletRequestDTO);
    }

}
