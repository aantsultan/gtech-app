package id.task.gtech.repository;

import id.task.gtech.model.Transfer;
import id.task.gtech.model.embedded.TransferId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, TransferId> {
}
