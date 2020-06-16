package kavaliou.ivan.net.moneymanagermobile.forms;

import java.io.Serializable;

import kavaliou.ivan.net.moneymanagermobile.utils.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCategoryForm  implements Serializable {

    private Integer id;
    private String name;
    private TransactionType transactionType;
}
