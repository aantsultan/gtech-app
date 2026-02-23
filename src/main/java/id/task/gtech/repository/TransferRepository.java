package id.task.gtech.repository;

import id.task.gtech.model.Transfer;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<@NonNull Transfer, @NonNull Long> {
}
