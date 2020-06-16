package kavaliou.ivan.net.moneymanagermobile.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User implements Serializable {
    private Integer id;
    private String email;
    private String password;
    private String registred;
}
