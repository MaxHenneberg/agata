import {Address} from './Address';
import {ItemRow} from './ItemRow';

export class RequestSlotTO {
  buyer: string;
  supplier: string;
  arrivalParty: string;

  departureAddress: Address;
  arrivalAddress: Address;

  expectedGoods: ItemRow[];
}
