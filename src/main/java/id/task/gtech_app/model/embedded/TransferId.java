package id.task.gtech_app.model.embedded;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class TransferId implements Serializable {

    private String debitAccount;
    private String creditAccount;
    private String transactionCode;

}
