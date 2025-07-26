package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Account;
import com.eduai.schoolmanagement.entity.Account.AccountType;
import com.eduai.schoolmanagement.entity.Account.AccountCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    // Find by unique fields
    Optional<Account> findByAccountCode(String accountCode);
    Optional<Account> findByAccountCodeAndInstitutionId(String accountCode, String institutionId);

    // Find by institution
    List<Account> findByInstitutionId(String institutionId);
    List<Account> findByInstitutionIdAndIsActive(String institutionId, Boolean isActive);

    // Find by account type
    List<Account> findByInstitutionIdAndAccountType(String institutionId, AccountType accountType);
    List<Account> findByInstitutionIdAndAccountTypeAndIsActive(String institutionId, AccountType accountType, Boolean isActive);

    // Find by category
    List<Account> findByInstitutionIdAndCategory(String institutionId, AccountCategory category);
    List<Account> findByInstitutionIdAndCategoryAndIsActive(String institutionId, AccountCategory category, Boolean isActive);

    // Hierarchical queries
    List<Account> findByInstitutionIdAndParentAccountId(String institutionId, String parentAccountId);
    List<Account> findByInstitutionIdAndParentAccountIdAndIsActive(String institutionId, String parentAccountId, Boolean isActive);
    List<Account> findByInstitutionIdAndLevel(String institutionId, Integer level);

    // Find parent accounts (accounts that have children)
    @Query("{ 'institutionId': ?0, 'childAccountIds': { $exists: true, $not: { $size: 0 } } }")
    List<Account> findParentAccountsByInstitutionId(String institutionId);

    // Find leaf accounts (accounts with no children)
    @Query("{ 'institutionId': ?0, $or: [ { 'childAccountIds': { $exists: false } }, { 'childAccountIds': { $size: 0 } } ] }")
    List<Account> findLeafAccountsByInstitutionId(String institutionId);

    // Search by name or code
    @Query("{ 'institutionId': ?0, $or: [ { 'accountName': { $regex: ?1, $options: 'i' } }, { 'accountCode': { $regex: ?1, $options: 'i' } } ] }")
    List<Account> searchByNameOrCode(String institutionId, String searchTerm);

    // Find by bank details (for bank accounts)
    Optional<Account> findByInstitutionIdAndAccountNumber(String institutionId, String accountNumber);
    List<Account> findByInstitutionIdAndBankName(String institutionId, String bankName);

    // Find accounts with balance above/below threshold
    @Query("{ 'institutionId': ?0, 'balance': { $gte: ?1 } }")
    List<Account> findAccountsWithBalanceAbove(String institutionId, Double threshold);

    @Query("{ 'institutionId': ?0, 'balance': { $lte: ?1 } }")
    List<Account> findAccountsWithBalanceBelow(String institutionId, Double threshold);

    // Find accounts that need budget attention
    @Query("{ 'institutionId': ?0, 'budgetLimit': { $exists: true }, $expr: { $gte: [ '$balance', '$warningThreshold' ] } }")
    List<Account> findAccountsApproachingBudgetLimit(String institutionId);

    @Query("{ 'institutionId': ?0, 'budgetLimit': { $exists: true }, $expr: { $gte: [ '$balance', '$budgetLimit' ] } }")
    List<Account> findAccountsExceedingBudgetLimit(String institutionId);

    // Statistical queries
    @Query(value = "{ 'institutionId': ?0, 'accountType': ?1 }", count = true)
    long countByInstitutionIdAndAccountType(String institutionId, AccountType accountType);

    @Query(value = "{ 'institutionId': ?0, 'isActive': true }", count = true)
    long countActiveAccountsByInstitutionId(String institutionId);

    // Complex aggregation queries
    @Query("{ $group: { _id: '$accountType', totalBalance: { $sum: '$balance' }, count: { $sum: 1 } } }")
    List<Object> getAccountSummaryByType(String institutionId);

    // Find accounts by created date range
    @Query("{ 'institutionId': ?0, 'createdDate': { $gte: ?1, $lte: ?2 } }")
    List<Account> findAccountsCreatedBetween(String institutionId, String startDate, String endDate);

    // Find accounts by last modified date
    @Query("{ 'institutionId': ?0, 'lastModifiedDate': { $gte: ?1 } }")
    List<Account> findAccountsModifiedSince(String institutionId, String date);

    // Validation queries
    boolean existsByAccountCodeAndInstitutionId(String accountCode, String institutionId);
    boolean existsByAccountNumberAndInstitutionId(String accountNumber, String institutionId);

    // Custom delete operations
    void deleteByInstitutionIdAndAccountCode(String institutionId, String accountCode);

    // Find accounts for chart of accounts display
    @Query("{ 'institutionId': ?0, 'isActive': true }")
    List<Account> findActiveAccountsForChartOfAccounts(String institutionId);

    // Find cash and bank accounts
    @Query("{ 'institutionId': ?0, 'category': 'CASH_AND_CASH_EQUIVALENTS', 'isActive': true }")
    List<Account> findCashAndBankAccounts(String institutionId);

    // Find receivable accounts
    @Query("{ 'institutionId': ?0, 'accountName': { $regex: 'receivable', $options: 'i' }, 'isActive': true }")
    List<Account> findReceivableAccounts(String institutionId);

    // Find payable accounts
    @Query("{ 'institutionId': ?0, 'accountName': { $regex: 'payable', $options: 'i' }, 'isActive': true }")
    List<Account> findPayableAccounts(String institutionId);

    // Find income accounts
    @Query("{ 'institutionId': ?0, 'accountType': 'INCOME', 'isActive': true }")
    List<Account> findIncomeAccounts(String institutionId);

    // Find expense accounts
    @Query("{ 'institutionId': ?0, 'accountType': 'EXPENSE', 'isActive': true }")
    List<Account> findExpenseAccounts(String institutionId);
}
