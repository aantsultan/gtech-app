package id.task.gtech.service;

import id.task.gtech.dto.TransferDto;
import id.task.gtech.exception.NotFoundException;
import id.task.gtech.exception.TransferException;
import id.task.gtech.helper.ExceptionHelper;
import id.task.gtech.model.Account;
import id.task.gtech.model.Transfer;
import id.task.gtech.repository.TransferRepository;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository repository;
    private final AccountService accountService;
    private final Map<String, Integer> duplicate = new HashMap<>();

    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public String post(TransferDto dto) {
        String debitAccount = dto.getDebitAccount();
        String creditAccount = dto.getCreditAccount();
        String transactionCode = dto.getTransactionCode();

        // 1. create transfer transaction
        BigDecimal amount = dto.getAmount();
        Transfer transfer = new Transfer();
        transfer.setDebitAccount(debitAccount);
        transfer.setCreditAccount(creditAccount);
        transfer.setTransactionCode(transactionCode);
        transfer.setAmount(amount);

        try {
            repository.save(transfer);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Unique index or primary key violation")) {
                throw new TransferException(ExceptionHelper.TRANSFER_DUPLICATE);
            }
        }

        // 2. update debit balance
        Account dbAccount = accountService.findById(debitAccount);
        if (dbAccount == null) throw new NotFoundException(ExceptionHelper.ACCOUNT_NOT_FOUND);
        BigDecimal dbBalance = dbAccount.getBalance();
        if (dbBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0)
            throw new TransferException(ExceptionHelper.TRANSFER_MINUS);
        dbAccount.setBalance(dbBalance.subtract(amount));
        accountService.save(dbAccount);

        // 3. update credit balance
        Account crAccount = accountService.findById(creditAccount);
        if (crAccount == null) throw new NotFoundException(ExceptionHelper.ACCOUNT_NOT_FOUND);
        BigDecimal crBalance = crAccount.getBalance();
        crAccount.setBalance(crBalance.add(amount));
        accountService.save(crAccount);

        return "OK";
    }
}
