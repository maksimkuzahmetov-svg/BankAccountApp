package wallet.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletBalanceDto(UUID id, BigDecimal balance) {}
