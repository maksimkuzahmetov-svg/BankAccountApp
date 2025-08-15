package wallet.service;

import wallet.dto.request.WalletOperationDto;
import wallet.dto.response.WalletBalanceDto;

import java.util.UUID;

public interface WalletService {

    WalletBalanceDto getBalance(UUID id);

    WalletBalanceDto carryOutOperation(WalletOperationDto walletOperationDto);

}
