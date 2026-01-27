package id.task.gtech.controller;

import id.task.gtech.dto.AccountDto;
import id.task.gtech.dto.ResponseDto;
import id.task.gtech.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account/{accountNo}")
    public ResponseEntity<ResponseDto<AccountDto>> get(@PathVariable String accountNo) {
        return ResponseEntity.ok(new ResponseDto<>(accountService.get(accountNo)));
    }

}
