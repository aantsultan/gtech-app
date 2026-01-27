package id.task.gtech.service;

import id.task.gtech.model.Account;

public interface AccountService {

    Account findById(String username);

    Account save(Account account);

}
