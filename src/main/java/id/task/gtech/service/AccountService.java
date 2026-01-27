package id.task.gtech.service;

import id.task.gtech.dto.AccountDto;
import id.task.gtech.model.Account;

public interface AccountService {

    Account findById(String accountNo);

    void save(Account account);

    AccountDto get(String accountNo);
}
