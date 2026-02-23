package id.task.gtech.repository;

import id.task.gtech.model.Account;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<@NonNull Account, @NonNull String> {

}
