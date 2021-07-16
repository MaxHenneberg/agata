import {ItemRow} from './ItemRow';
import {ContainerTO} from './ContainerTO';

export class BolTO {
  linearId: string;

  shipper: string;
  consignee: string;
  notifyParty: string;

  modeOfInitialCarriage: string;
  placeOfInitialReceipt: string;

  vesselName: string;
  portOfLoading: string;
  portOfDischarge: string;
  placeOfDeliveryByCarrier: string;

  bookingNo: string;
  billOfLadingNo: string;

  exportReference: string[];

  forwardingAgent: string;
  fmcNo: string;

  pointAndCountry: string;

  cargoReleaser: string;

  domesticRoutingInstructions: string;

  freightPayableAt: string;
  typeOfMovement: string;

  goodsList: ItemRow[];

  freightChargesList: string[];

  prepaid: string;
  collect: string;

  incotermList: string[];

  containerInformationList: ContainerTO[];
}
