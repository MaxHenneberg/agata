import {Injectable} from '@angular/core';
import {BolTO} from '../dataholder/BolTO';
import {ContainerTO} from '../dataholder/ContainerTO';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ItemRow} from '../dataholder/ItemRow';
import {HttpBaseService} from './http-base.service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AcceptContainerService {

  constructor(private httpClient: HttpBaseService) {
  }

  resolveContainerToMasterBol(containerId: string): Observable<{ masterBol: BolTO }> {
    return this.httpClient.get('/loadings//proposals/masterBol/' + containerId);
  }

  resolveBol(bolId: string): Observable<BolTO> {
    return this.httpClient.get('/delivery/houseBol/' + bolId);
  }

  resolveIdentityNumberIdToBol(identityNumber: string): { good: ItemRow, bol: BolTO } {
    return {
      good: {
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
      },
      bol: {
        billOfLadingNo: '123-abc',
        bookingNo: '',
        cargoReleaser: '',
        collect: '',
        consignee: 'O=PartyB, L=New York, C=US',
        containerInformationList: [{containerNo: '123-abc-456', type: '20', sealNo: 'seal'}],
        domesticRoutingInstructions: '',
        exportReference: [],
        fmcNo: '',
        forwardingAgent: '',
        freightChargesList: [],
        freightPayableAt: '',
        goodsList: [
          {
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
          },
          {
            mark: '12',
            identityNumber: 'otherId',
            noOfPackages: 2,
            descriptionOfGoods: {
              product: 'Foo',
              typeOfPackage: 'test',
              quantity: 123
            },
            netWeight: 1234,
            grossWeight: 1235,
            measurement: 456
          }
        ],
        incotermList: [],
        linearId: '123-abc-456-edf',
        modeOfInitialCarriage: '',
        notifyParty: 'O=PartyC, L=Berlin, C=DE',
        placeOfDeliveryByCarrier: '',
        placeOfInitialReceipt: '',
        pointAndCountry: '',
        portOfDischarge: 'Over The Rainbow',
        portOfLoading: 'Somewhere',
        prepaid: '',
        shipper: 'O=PartyA, L=London, C=GB',
        typeOfMovement: '',
        vesselName: 'My Vessel'
      }
    };
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
    this.httpClient.post('/loadings/proposals/' + containerId + '/acceptance', ['trackingStateId']);
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
    this.httpClient.patch('/delivery/proposals/' + bolId, {deliveredGoods: receivedGoods});
    console.log('Confirm Goods please');
  }
}
