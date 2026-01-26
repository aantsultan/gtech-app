package id.task.gtech_app.repository;

import id.task.gtech_app.model.Transfer;
import id.task.gtech_app.model.embedded.TransferId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, TransferId> {
}
