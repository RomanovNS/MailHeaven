package MailHeavenPackage.repos;

import MailHeavenPackage.models.MailBoxAccount;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailBoxAccountRepository extends JpaRepository<MailBoxAccount, Long> {
    List<MailBoxAccount> findByUserid(User userid);
}