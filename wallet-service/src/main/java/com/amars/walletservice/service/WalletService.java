package com.amars.walletservice.service;

import com.amars.walletservice.dto.PaymentRequestDTO;
import com.amars.walletservice.dto.WalletRequestDTO;
import com.amars.walletservice.dto.WalletResponseDTO;
import com.amars.walletservice.model.Wallet;
import com.amars.walletservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public void createWallet(WalletRequestDTO walletRequestDTO){
        Wallet wallet = Wallet.builder()
                .studentCode(walletRequestDTO.getStudentCode())
                .status(0)
                .build();
        walletRepository.save(wallet);
    }

    public WalletResponseDTO walletStatus(WalletRequestDTO walletRequestDTO){
        Wallet wallet = walletRepository.getById(walletRequestDTO.getStudentCode());
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .status(wallet.getStatus())
                .build();
        return walletResponseDTO;
    }

    public void payForExam(PaymentRequestDTO paymentRequestDTO){
        Wallet wallet = walletRepository.getById(paymentRequestDTO.getStudentCode());
        wallet.setStatus(wallet.getStatus()-paymentRequestDTO.getAmount());
        walletRepository.save(wallet);
    }

    public void addFunds(PaymentRequestDTO paymentRequestDTO){
        Wallet wallet = walletRepository.getById(paymentRequestDTO.getStudentCode());
        wallet.setStatus(wallet.getStatus()+paymentRequestDTO.getAmount());
        walletRepository.save(wallet);
    }

}
