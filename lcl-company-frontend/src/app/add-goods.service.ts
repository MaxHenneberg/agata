import {Injectable} from '@angular/core';
import {ItemRow} from '../dataholder/ItemRow';

@Injectable({
  providedIn: 'root'
})
export class AddGoodsService {

  constructor() {
  }

  resolveGoodsId(id: string): ItemRow {
    return {
      mark: '12',
      identityNumber: '34abcd',
      noOfPackages: 2,
      descriptionOfGoods: {
        product: 'Foo',
        typeOfPackage: 'test',
        quantity: 123
      },
      netWeight: 1234,
      grossWeight: 1235,
      measurement: 456
    };
  }

  resolveProposalId(id: string): any {
    return {
      buyer: 'buyer123',
      supplier: 'supplier123',
      lclCompany: 'company24',
      invoiceId: '123-123-ABC'
    };
  }

  finishModfiy(id: string, addedGoods: ItemRow[]) {
    console.log('Finished Modify');
    console.log(addedGoods);
  }
}
