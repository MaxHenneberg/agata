package agata.lcl.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TrackingStateReferenceList {

    private List<String> trackingStateIds;
}
