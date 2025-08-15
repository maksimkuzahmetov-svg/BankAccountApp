package wallet.mapper;

import wallet.dto.response.WalletBalanceDto;
import wallet.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletBalanceDto toDto (Wallet wallet);

}
