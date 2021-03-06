package agata.lcl.dto;

import agata.bol.dataholder.ContainerInformation;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ContainerAssignment {
    private String vesselName;
    private ContainerInformation container;
}
