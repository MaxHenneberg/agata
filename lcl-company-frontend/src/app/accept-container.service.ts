import {Injectable} from '@angular/core';
import {BolTO} from '../dataholder/BolTO';
import {HttpBaseService} from './http-base.service';
import {Observable} from 'rxjs';
import {DeliveryProposalTO} from '../dataholder/DeliveryProposalTO';

@Injectable({
  providedIn: 'root'
})
export class AcceptContainerService {

  constructor(private httpClient: HttpBaseService) {
  }

  resolveContainerToMasterBol(containerId: string): Observable<{ masterBol: BolTO }> {
    return this.httpClient.get('/loadings/proposals/masterBol/' + containerId);
  }

  resolveBolId(bolId: string): Observable<BolTO> {
    return this.httpClient.get('/bill-of-ladings/' + bolId);
  }

  resolveDeliveryProposalId(proposalId: string): Observable<DeliveryProposalTO> {
    return this.httpClient.get('/proposals/' + proposalId);
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

  requestGoodsConfirmation(proposalId: string, receivedGoods: string[]) {
    this.httpClient.patch('/deliveries/proposals/' + proposalId, {deliveredGoods: receivedGoods}).subscribe(res => console.log(res));
    console.log('Confirm Goods please');
  }
}
