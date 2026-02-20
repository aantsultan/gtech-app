package id.task.gtech.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Transfer {

    @Id
    @Column(name = "transfer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String debitAccount;
    private String creditAccount;

    @Column(name = "transaction_code", unique = true)
    private String transactionCode;

    private BigDecimal amount;

}
