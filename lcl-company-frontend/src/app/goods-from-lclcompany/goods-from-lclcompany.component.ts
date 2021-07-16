import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ScannerDialogComponent} from '../scanner-dialog/scanner-dialog.component';
import {AcceptContainerDialogComponent} from '../accept-container-dialog/accept-container-dialog.component';
import {AcceptContainerService} from '../accept-container.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {BolTO} from '../../dataholder/BolTO';
import {ItemRow} from '../../dataholder/ItemRow';
import {MatTable} from '@angular/material/table';
import {ActivatedRoute} from '@angular/router';
import {AddGoodsService} from "../add-goods.service";

@Component({
  selector: 'app-goods-from-lclcompany',
  templateUrl: './goods-from-lclcompany.component.html',
  styleUrls: ['./goods-from-lclcompany.component.css']
})
export class GoodsFromLclcompanyComponent implements OnInit {
  @ViewChild(MatTable) table: MatTable<any>;
  columnsToDisplay = ['mark', 'id', '#packages', 'product', 'received'];

  scanDialogRef: MatDialogRef<ScannerDialogComponent>;
  acceptContainerDialogRef: MatDialogRef<AcceptContainerDialogComponent>;

  billOfLadingId: string;
  proposalId: string;
  billOfLading: BolTO;
  expectedGoods: ItemRow[];
  receivedGoods: string[];

  // tslint:disable-next-line:max-line-length
  constructor(private dialog: MatDialog, private acceptContainerService: AcceptContainerService, private addGoodsService: AddGoodsService, private snackBar: MatSnackBar, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.proposalId = (this.route.snapshot.paramMap.get('id'));
    this.expectedGoods = [];
    this.receivedGoods = [];
    this.acceptContainerService.resolveDeliveryProposalId(this.proposalId).subscribe((proposal) => {
      this.acceptContainerService.resolveBolId(proposal.proposedState.houseBolId).subscribe((bol) => {
        this.billOfLading = bol;
        this.expectedGoods = this.billOfLading.goodsList;
        this.table.renderRows();
      });
    });
  }

  isReceived(identityNumber: string): boolean {
    return this.receivedGoods.filter(r => r === identityNumber).length > 0;
  }

  openDialog() {
    this.scanDialogRef = this.dialog.open(ScannerDialogComponent);
    this.scanDialogRef.componentInstance.onSuccessfulScan.subscribe((event) => this.onSuccessfulScan(event));
  }

  onSuccessfulScan(idNumber: string) {
    this.scanDialogRef.close();
    if (this.isExpected(idNumber)) {
      const result = this.addGoodsService.resolveGoodsId(idNumber);
      this.receivedGoods.push(result.identityNumber);
      this.table.renderRows();
    }
  }

  isExpected(identityNumber: string): boolean {
    return this.expectedGoods.filter(g => g.identityNumber === identityNumber).length > 0;
  }

  isAllGoodsReceived() {
    return this.expectedGoods.length === this.receivedGoods.length;
  }

  requestConfirmation() {
    this.acceptContainerService.requestGoodsConfirmation(this.proposalId, this.receivedGoods);
  }

}
