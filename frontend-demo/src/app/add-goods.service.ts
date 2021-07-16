import {Injectable} from '@angular/core';
import {ItemRow} from '../dataholder/ItemRow';
import {HttpBaseService} from './http-base.service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AddGoodsService {

  constructor(private httpClient: HttpBaseService) {
  }

  resolveGoodsId(id: string): ItemRow {
    switch (id) {
      case '1234':
        return {
          mark: '1234',
          identityNumber: '1234',
          noOfPackages: 3,
          descriptionOfGoods: {
            product: 'XXL Underwear',
            typeOfPackage: 'Roll',
            quantity: 20
          },
          netWeight: 2,
          grossWeight: 2,
          measurement: 1
        };
      case '5678':
        return {
          mark: '5678',
          identityNumber: '5678',
          noOfPackages: 1,
          descriptionOfGoods: {
            product: 'I Phone X',
            typeOfPackage: 'Package',
            quantity: 100
          },
          netWeight: 23,
          grossWeight: 23,
          measurement: 23
        };
    }
  }

  resolveProposalId(id: string): Observable<any> {
    return this.httpClient.get('/pickups/proposals/' + id);
  }

  finishModfiy(id: string, invoiceId: string, addedGoods: ItemRow[]) {
    console.log('Finished Modify');
    console.log(invoiceId);
    console.log(addedGoods);
    return this.httpClient.patch('/pickups/proposals/' + id, {goods: addedGoods, invoiceId}).subscribe(res => console.log(res));
  }
}
