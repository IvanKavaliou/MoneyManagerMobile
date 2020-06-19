package kavaliou.ivan.net.moneymanagermobile.forms;

import kavaliou.ivan.net.moneymanagermobile.utils.enums.CurrencyType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationForm {
    private String email;
    private String password;
    private String passwordRepeat;
    private boolean agrements;
    private CurrencyType currency;
}
