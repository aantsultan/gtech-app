package id.task.gtech.service;

import id.task.gtech.model.Account;
import id.task.gtech.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public Account findById(String username) {
        return repository.findById(username).orElse(null);
    }

    @Override
    public Account save(Account account) {
        return repository.save(account);
    }
}
