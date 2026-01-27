package id.task.gtech.model;

import id.task.gtech.model.embedded.TransferId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Transfer {

    @EmbeddedId
    private TransferId transferId;
    private BigDecimal amount;

}
