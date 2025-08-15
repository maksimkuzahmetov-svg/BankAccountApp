package wallet.service.impl;

import wallet.dto.request.WalletOperationDto;
import wallet.dto.response.WalletBalanceDto;
import wallet.entity.Wallet;
import wallet.exception.InsufficientFundsException;
import wallet.exception.WalletLockException;
import wallet.exception.WalletNotFoundException;
import wallet.mapper.WalletMapper;
import wallet.repository.WalletRepository;
import wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

private final WalletRepository walletRepository;
private final WalletMapper walletMapper;

    @Override
    public WalletBalanceDto getBalance(UUID id) {
        return walletMapper.toDto(walletRepository.findById(id).orElseThrow(()->new WalletNotFoundException("Кошелёк не найден: " + id)));
    }

    @Override
    public WalletBalanceDto carryOutOperation(WalletOperationDto walletOperationDto) {
        validateInputData(walletOperationDto);
        UUID walletId = walletOperationDto.id();
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(()->new WalletNotFoundException("Кошелёк не найден: " + walletId));
        switch (walletOperationDto.operationType()) {
            case DEPOSIT -> wallet.setBalance(wallet.getBalance().add(walletOperationDto.amount()));
            case WITHDRAW -> {
                if (wallet.getBalance().compareTo(walletOperationDto.amount()) < 0) {
                    throw new InsufficientFundsException("Недостаточно средств");
                }
                wallet.setBalance(wallet.getBalance().subtract(walletOperationDto.amount()));
            }
        }
        try {
            return walletMapper.toDto(walletRepository.save(wallet));
        } catch (OptimisticLockingFailureException exception) {
            throw new WalletLockException("Кошелёк уже был изменен");
        }
    }

    private void validateInputData(WalletOperationDto walletOperationDto) {
        if (walletOperationDto == null) {
            throw new IllegalArgumentException("DTO не может быть null");
        }
        if (walletOperationDto.id() == null) {
            throw new IllegalArgumentException("ID кошелька не может быть null");
        }
        if (walletOperationDto.operationType() == null) {
            throw new IllegalArgumentException("Не указан тип операции: ");
        }
        if(walletOperationDto.amount() == null || walletOperationDto.amount().compareTo(BigDecimal.ZERO) <= 0 ) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
    }

}
