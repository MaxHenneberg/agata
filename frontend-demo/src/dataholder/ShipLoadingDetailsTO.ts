import {PriceTO} from './PriceTO';

export class ShipLoadingDetailsTO {
  containerStateId: string;
  shippingLine: string;
  houseBolIds: string[];
  modeOfInitialCarriage: string;
  placeOfInitialReceipt: string;
  bookingNo: string;
  billOfLadingNo: string;
  exportReference: string[];
  freightPayableAt: string;
  typeOfMovement: string;
  freightChargesList: { charge: PriceTO, chargeReason: string }[];
  prepaid: PriceTO;
  collect: PriceTO;
}
