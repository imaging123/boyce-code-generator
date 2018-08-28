package data.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author boyce - 02/21/2018
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schema {
    private String name;
}
