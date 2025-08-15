package wallet.service;

import static wallet.dto.request.WalletOperationDto.OperationType.DEPOSIT;
import static wallet.dto.request.WalletOperationDto.OperationType.WITHDRAW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import wallet.config.TestContainerConfig;
import wallet.dto.request.WalletOperationDto;
import wallet.dto.response.WalletBalanceDto;
import wallet.entity.Wallet;
import wallet.exception.InsufficientFundsException;
import wallet.exception.WalletNotFoundException;
import wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = {TestContainerConfig.class})
class WalletServiceImplTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    private UUID walletId;
    private BigDecimal initialBalance = BigDecimal.valueOf(500.00);

    @BeforeEach
    @Transactional
    void setUp() {
        // Создаем и сохраняем кошелёк
        Wallet wallet = new Wallet();
        wallet.setBalance(initialBalance);

        Wallet savedWallet = walletRepository.save(wallet);

        // Сохраняем сгенерированный ID
        this.walletId = savedWallet.getId();

        // Логирование
        System.out.println("Сохраненный ID: " + walletId);
        System.out.println("Баланс: " + savedWallet.getBalance());

        // Проверки
        if (savedWallet == null) {
            throw new RuntimeException("Кошелёк не был сохранён в БД");
        }

        if (!savedWallet.getBalance().equals(initialBalance)) {
            throw new RuntimeException("Некорректное сохранение баланса");
        }
    }

    @Test
    void testGetBalance() {
        WalletBalanceDto balance = walletService.getBalance(walletId);
        assertThat(balance.id()).isEqualTo(walletId);
        assertThat(balance.balance()).isEqualTo(initialBalance);
    }

    @Test
    void testDepositOperation() {
        WalletOperationDto depositDto = new WalletOperationDto(
                walletId, // Используем сгенерированный ID
                DEPOSIT,
                BigDecimal.valueOf(500)
        );

        WalletBalanceDto result = walletService.carryOutOperation(depositDto);
        assertThat(result.balance().compareTo(initialBalance.add(BigDecimal.valueOf(500))))
                .isEqualTo(0);
    }

    @Test
    void testWithdrawOperation() {
        WalletOperationDto withdrawDto = new WalletOperationDto(
                walletId, // Используем сгенерированный ID
                WITHDRAW,
                BigDecimal.valueOf(200)
        );
        WalletBalanceDto result = walletService.carryOutOperation(withdrawDto);
        assertThat(result.balance().compareTo(initialBalance.subtract(BigDecimal.valueOf(200))))
                .isEqualTo(0);
    }

    @Test
    void testInsufficientFunds() {
        WalletOperationDto withdrawDto = new WalletOperationDto(
                walletId, // Используем сгенерированный ID
                WITHDRAW,
                BigDecimal.valueOf(2000)
        );

        assertThatThrownBy(() -> walletService.carryOutOperation(withdrawDto))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Недостаточно средств");
    }

    @Test
    void testWalletNotFound() {
        UUID nonExistingId = UUID.randomUUID();
        assertThatThrownBy(() -> walletService.getBalance(nonExistingId))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining("Кошелёк не найден");
    }

    @Test
    void testInvalidInputData() {
        WalletOperationDto invalidDto = new WalletOperationDto(
                null,
                DEPOSIT,
                BigDecimal.ZERO
        );
        assertThatThrownBy(() -> walletService.carryOutOperation(invalidDto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
