package id.task.gtech.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDto {

    private String debitAccount;
    private String creditAccount;
    private String transactionCode;
    private BigDecimal amount;

}
