package id.task.gtech.service;

import id.task.gtech.dto.AccountDto;
import id.task.gtech.exception.NotFoundException;
import id.task.gtech.helper.ExceptionHelper;
import id.task.gtech.model.Account;
import id.task.gtech.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public Account findById(String accountNo) {
        return repository.findById(accountNo).orElse(null);
    }

    @Override
    public void save(Account account) {
        repository.save(account);
    }

    @Override
    public AccountDto get(String accountNo) {
        Optional<Account> accountOptional = repository.findById(accountNo);
        if (accountOptional.isEmpty()) throw new NotFoundException(ExceptionHelper.ACCOUNT_NOT_FOUND);
        Account account = accountOptional.get();
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNo(account.getAccountNo());
        accountDto.setBalance(account.getBalance());
        return accountDto;
    }
}
