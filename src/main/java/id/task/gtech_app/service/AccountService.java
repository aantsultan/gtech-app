package id.task.gtech_app.service;

import id.task.gtech_app.model.Account;

public interface AccountService {

    Account findById(String username);

    Account save(Account account);

}
