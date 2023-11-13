package ch.mobi.itc.mobiliar.rest.deployments;

import ch.mobi.itc.mobiliar.rest.dtos.AppWithVersionDTO;
import ch.mobi.itc.mobiliar.rest.dtos.DeploymentParameterDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "multipleDeploymentsRequest")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Data
@NoArgsConstructor
public class MultipleDeploymentsRequest {

    private List<String> appServerNames;
    private Date deploymentDate; // optional
    private Date stateToDeploy; // optional
    private String environmentName;
    private List<AppWithVersionDTO> appsWithVersion; // optional
    private Boolean requestOnly = false; // optional
    private Boolean simulate = false; // optional
    private Boolean executeShakedownTest = false; // optional
    private Boolean neighbourhoodTest = false; // optional
    private Boolean sendEmail = false; // optional
    private String releaseName; // optional
    private List<DeploymentParameterDTO> deploymentParameters; // optional
    private List<Integer> contextIds; // optional

}
