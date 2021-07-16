import {ItemRow} from './ItemRow';

export class PackageDeliveryStateTO {
  linearId: string;
  arrivalParty: string;
  lclCompany: string;
  houseBolId: {
    id: string;
    externalId: string;
  };
  deliveryGoods: ItemRow[];
}
