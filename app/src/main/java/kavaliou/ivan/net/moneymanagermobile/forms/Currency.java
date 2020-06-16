package kavaliou.ivan.net.moneymanagermobile.forms;

import java.io.Serializable;

import kavaliou.ivan.net.moneymanagermobile.utils.enums.CurrencyType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency  implements Serializable {
    private Integer id;
    private CurrencyType currencyType;
}
