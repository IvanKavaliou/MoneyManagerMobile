package kavaliou.ivan.net.moneymanagermobile.forms;

import java.io.Serializable;
import java.math.BigDecimal;

import kavaliou.ivan.net.moneymanagermobile.utils.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionForm  implements Serializable {

    private Integer id;
    private CurrencyType currencyType;
    private BigDecimal value;
    private Integer idTransactionCategory;
    private TransactionCategoryForm transactionCategory;
    private String name;
    private String date;
}