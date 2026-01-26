package id.task.gtech_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Account {

    @Id
    private String accountNo;
    private BigDecimal balance;

}
