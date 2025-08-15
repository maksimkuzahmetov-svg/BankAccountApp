package wallet.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletOperationDto(UUID id, OperationType operationType, BigDecimal amount) {

    public enum OperationType{
        DEPOSIT,
        WITHDRAW
    }

}
