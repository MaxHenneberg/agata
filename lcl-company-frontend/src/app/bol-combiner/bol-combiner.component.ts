import {Component, OnInit} from '@angular/core';
import {BolCombinerService} from '../bol-combiner.service';
import {BolTO} from '../../dataholder/BolTO';
import {ContainerTO} from '../../dataholder/ContainerTO';
import {ShipLoadingDetailsTO} from '../../dataholder/ShipLoadingDetailsTO';
import {HttpBaseService} from '../http-base.service';

@Component({
  selector: 'app-bol-combiner',
  templateUrl: './bol-combiner.component.html',
  styleUrls: ['./bol-combiner.component.css']
})
export class BolCombinerComponent implements OnInit {

  columnsToDisplay = ['containerId', 'sealNo', 'type', '#bol', 'buttonCol'];
  dataSource: BolTO[];
  containerMap: Map<string, BolTO[]>;

  constructor(private bolCombinerService: BolCombinerService, private httpClient: HttpBaseService) {
    this.containerMap = new Map();
  }

  ngOnInit(): void {
    this.dataSource = [];
    this.bolCombinerService.pollOpenBol().subscribe(e => {
      console.log(e);
      // @ts-ignore
      e.forEach(ele => {
          if (this.containerMap.has(ele.containerInformationList[0].containerNo)) {
            const arr = this.containerMap.get(ele.containerInformationList[0].containerNo);
            arr.push(ele);
            this.containerMap.set(ele.containerInformationList[0].containerNo, arr);
          } else {
            const arr = [];
            arr.push(ele);
            this.containerMap.set(ele.containerInformationList[0].containerNo, arr);
          }

        }
      );
    });
  }

  getContainerIds(): string[] {
    const result = [];
    this.containerMap.forEach((value, key) => result.push(key));
    return result;
  }


  initShiploading(containerId) {
    this.httpClient.get('/container-requests/containerStateById/' + containerId).subscribe(res => {
      console.log('Received Container State: ');
      console.log(res);
      const houseBol = this.containerMap.get(containerId);
      const shipLoadingDetailsTO = {
        containerStateId: res.linearId.id,
        shippingLine: res.shippingLine,
        // @ts-ignore
        houseBolIds: houseBol.map(bol => bol.linearId.id),
        modeOfInitialCarriage: houseBol[0].modeOfInitialCarriage,
        placeOfInitialReceipt: houseBol[0].placeOfInitialReceipt,
        bookingNo: '123-abc-567-def',
        billOfLadingNo: '123-abc-567-def',
        exportReference: houseBol[0].exportReference,
        freightPayableAt: houseBol[0].freightPayableAt,
        typeOfMovement: houseBol[0].typeOfMovement,
        freightChargesList: [{charge: {amount: 10.5, currency: 'EUR'}, chargeReason: 'Reason'}],
        prepaid: {amount: 10.5, currency: 'EUR'},
        collect: {amount: 10.5, currency: 'EUR'}
      } as ShipLoadingDetailsTO;
      this.httpClient.post('/loadings/proposals', shipLoadingDetailsTO).subscribe(resLoading => console.log(resLoading));
    });
  }
}
