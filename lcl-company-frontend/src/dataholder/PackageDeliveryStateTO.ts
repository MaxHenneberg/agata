import {ItemRow} from './ItemRow';

export class PackageDeliveryStateTO {
  linearId: string;
  arrivalParty: string;
  lclCompany: string;
  houseBolId: string;
  deliveryGoods: ItemRow[];
}
