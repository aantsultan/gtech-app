package id.task.gtech.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {

    private String accountNo;
    private BigDecimal balance;

}
