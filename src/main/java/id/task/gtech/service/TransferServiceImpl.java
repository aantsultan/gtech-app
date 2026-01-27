package id.task.gtech.service;

import id.task.gtech.dto.TransferDto;
import id.task.gtech.model.Account;
import id.task.gtech.model.Transfer;
import id.task.gtech.model.embedded.TransferId;
import id.task.gtech.repository.TransferRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
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
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public String post(TransferDto dto) {
        TransferId transferId = new TransferId();
        String debitAccount = dto.getDebitAccount();
        String creditAccount = dto.getCreditAccount();
        String transactionCode = dto.getTransactionCode();

        // 0. validation duplicate
        String key = debitAccount + creditAccount + transactionCode;
        if (duplicate.get(key) == null) {
            duplicate.put(key, 1);
        } else {
            throw new RuntimeException("Duplicate transfer !");
        }

        // 1. create transfer transaction
        BigDecimal amount = dto.getAmount();
        transferId.setDebitAccount(debitAccount);
        transferId.setCreditAccount(creditAccount);
        transferId.setTransactionCode(transactionCode);
        Transfer transfer = new Transfer();
        transfer.setTransferId(transferId);
        transfer.setAmount(amount);
        repository.save(transfer);

        // 2. update debit balance
        Account dbAccount = accountService.findById(debitAccount);
        if (dbAccount == null) throw new RuntimeException("Source Account is not found");
        BigDecimal dbBalance = dbAccount.getBalance();
        if (dbBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) throw new RuntimeException("Balance is minus !");
        dbAccount.setBalance(dbBalance.subtract(amount));
        accountService.save(dbAccount);

        // 3. update credit balance
        Account crAccount = accountService.findById(creditAccount);
        if (crAccount == null) throw new RuntimeException("Destination Account is not found");
        BigDecimal crBalance = crAccount.getBalance();
        crAccount.setBalance(crBalance.add(amount));
        accountService.save(crAccount);

        return "OK";
    }
}
