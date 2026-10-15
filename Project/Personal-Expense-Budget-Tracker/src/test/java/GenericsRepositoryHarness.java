import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

import com.hcltraining.model.Account;
import com.hcltraining.repository.CrudRepository;
import com.hcltraining.repository.InMemoryRepository;
import com.hcltraining.service.FinanceService;

public final class GenericsRepositoryHarness {
    public static void main(String[] args) throws Exception {
        CrudRepository<Account, Integer> repository = new InMemoryRepository<>(Account::getId);
        Account account = new Account(1, "Cash", "Wallet", new BigDecimal("20.00"));
        repository.save(account);
        check(repository.findById(1).orElseThrow() == account, "find by ID");
        check(repository.findAll().equals(List.of(account)), "find all snapshot");
        check(repository.deleteById(1), "delete existing");
        check(!repository.deleteById(1), "delete absent");

        FinanceService finance = new FinanceService();
        List<Account> loaded = finance.loadSeedAccounts(new StringReader(
                "id,name,type,openingBalance\n1,Checking,Bank,100.00\n2,Cash,Wallet,25.00\n"));
        check(loaded.size() == 2 && finance.getAccount(2).getBalance().compareTo(new BigDecimal("25.00")) == 0,
                "CSV seed data loads into service");
        FinanceService invalid = new FinanceService();
        try {
            invalid.loadSeedAccounts(new StringReader(
                    "id,name,type,openingBalance\n1,Valid,Bank,100.00\n2,Broken,Bank,not-money\n"));
            throw new AssertionError("Invalid CSV should fail");
        } catch (java.io.IOException expected) {
            check(invalid.getAccounts().isEmpty(), "invalid CSV leaves service unchanged");
        }
        System.out.println("PASS: generic CRUD operations");
        System.out.println("PASS: valid CSV seeds loaded");
        System.out.println("PASS: invalid CSV rejected without partial data");
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
