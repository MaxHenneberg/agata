import {BolTO} from "./BolTO";
import {PackageDeliveryStateTO} from "./PackageDeliveryStateTO";

export class DeliveryProposalTO{
  proposer: string;
  proposee: string;
  proposedState: PackageDeliveryStateTO;
  linearId: string;
}
