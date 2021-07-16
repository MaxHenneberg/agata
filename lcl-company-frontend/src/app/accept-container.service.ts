import {Injectable} from '@angular/core';
import {BolTO} from '../dataholder/BolTO';
import {HttpBaseService} from './http-base.service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AcceptContainerService {

  constructor(private httpClient: HttpBaseService) {
  }

  resolveContainerToMasterBol(containerId: string): Observable<{ masterBol: BolTO }> {
    return this.httpClient.get('/loadings/proposals/masterBol/' + containerId);
  }

  resolveBol(bolId: string): Observable<BolTO> {
    return this.httpClient.get('/bill-of-ladings/' + bolId);
  }

  goodFromIdentityNumber(identityNumber: string) {
    return {
      mark: '12',
      identityNumber,
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

  acceptContainer(containerId: string) {
    console.log('Accepted');
    // The list of tracking state would be provided by a backend
    this.httpClient.post('/loadings/proposals/' + containerId + '/acceptance', {trackingStateIds: ['trackingStateId']});
  }

  acceptContainerFromShippingLine(containerId: string) {
    console.log('Accepted Container From Shipping Line');
    console.log(containerId);
  }

  acceptGooodsFromLcl(invoiceId: string) {
    console.log('Accepted Goods From Lcl Company');
    console.log(invoiceId);
  }

  requestGoodsConfirmation(bolId: string, receivedGoods: string[]) {
    this.httpClient.patch('/deliveries/proposals/' + bolId, {deliveredGoods: receivedGoods});
    console.log('Confirm Goods please');
  }
}
