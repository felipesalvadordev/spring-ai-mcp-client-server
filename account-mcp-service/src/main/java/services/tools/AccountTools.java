package services.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import services.model.Account;
import services.model.Transaction;
import services.repository.AccountRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountTools {

    private static final Logger log = LoggerFactory.getLogger(AccountTools.class);
    private final AccountRepository accountRepository;
    private List<Transaction> accountTransactions = new ArrayList<>();
    private final ObjectMapper objectMapper;

    public AccountTools(AccountRepository accountRepository, ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.objectMapper = objectMapper;
    }

    @Tool(description = "Find all accounts by person ID")
    public List<Account> getAccountsByPersonId(
            @ToolParam(description = "Person ID") Long personId) {
        return accountRepository.findByPersonId(personId);
    }

    @Tool( description = "Get a single transaction by accountId")
    public Transaction getTransaction(String accountId) {
        return accountTransactions.stream()
                .filter(transaction -> transaction.accountId().equals(accountId))
                .findFirst()
                .orElse(null);
    }

    @PostConstruct
    public void init() {
        log.info("Loading Transactions from JSON file 'account-transactions.json'");
        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/account-transactions.json")) {
            var transactions = objectMapper.readValue(inputStream, Transaction[].class);
            this.accountTransactions = Arrays.stream(transactions).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }
}
