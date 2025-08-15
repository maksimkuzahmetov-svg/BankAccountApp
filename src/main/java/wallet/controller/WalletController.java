package wallet.controller;

import wallet.dto.request.WalletOperationDto;
import wallet.dto.response.WalletBalanceDto;
import wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/wallets/{id}/balance")
    public ResponseEntity<WalletBalanceDto> getBalance(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(walletService.getBalance(id));
    }

    @PostMapping("/wallet")
    @Transactional
    public ResponseEntity<WalletBalanceDto> carryOutOperation(@RequestBody WalletOperationDto walletOperationDto) {
        return ResponseEntity.ok(walletService.carryOutOperation(walletOperationDto));
    }

}
