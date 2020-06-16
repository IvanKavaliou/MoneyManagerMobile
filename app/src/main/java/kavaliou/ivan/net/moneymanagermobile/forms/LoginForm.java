package kavaliou.ivan.net.moneymanagermobile.forms;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginForm  implements Serializable {
    private String email;
    private String password;
}
