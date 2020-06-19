package kavaliou.ivan.net.moneymanagermobile.forms;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordChangeForm {
    String oldPassword;
    String password;
    String passwordRepeat;
}

