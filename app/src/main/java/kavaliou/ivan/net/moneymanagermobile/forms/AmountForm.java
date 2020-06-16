package kavaliou.ivan.net.moneymanagermobile.forms;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmountForm implements Serializable {
    private String week;
    private String day;
    private String month;
    private String balance;
}
